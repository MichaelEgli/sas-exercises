package com.bfh.domi.catalog.catalog.service;

import com.bfh.domi.catalog.TestcontainersConfiguration;
import com.bfh.domi.catalog.catalog.common.testdata.TestInputValues;
import com.bfh.domi.catalog.catalog.common.testdata.TestdataCreator;
import com.bfh.domi.catalog.catalog.integration.google.GoogleBooksClient;
import com.bfh.domi.catalog.catalog.model.Book;
import com.bfh.domi.catalog.catalog.exception.BookAlreadyExistsException;
import com.bfh.domi.catalog.catalog.exception.BookNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/test-data-books.sql")
public class CatalogServiceIT {

    @Autowired
    private CatalogService catalogService;

    @MockitoBean
    private GoogleBooksClient googleBooksClient;

    String ISBN_VALID = TestInputValues.VALID_ISBN.getValue();
    String ISBN_NOT_FOUND = TestInputValues.ISBN_NOT_FOUND.getValue();
    String ISBN_NEW_BOOK = TestInputValues.ISBN_NEW_BOOK.getValue();
    String ISBN_GOOGLE_BOOK = TestInputValues.ISBN_GOOGLE_BOOK.getValue(); // This book is only available via Google Books API

    @Test
    public void findBookByIsbn() throws BookNotFoundException {
        var result = catalogService.findBook(ISBN_VALID);
        assertThat(result.getIsbn()).isEqualTo(ISBN_VALID);
    }

    @Test
    public void findBookByIsbn_GoogleBooks() throws BookNotFoundException {
        // Mocking Google Books API response as we cannot guarantee its content during tests
        Mockito.when(googleBooksClient.findBook(ISBN_GOOGLE_BOOK))
                .thenReturn(TestdataCreator.getBooksFromGoogleBooksApi().stream().findFirst());

        var result = catalogService.findBook(ISBN_GOOGLE_BOOK);
        assertThat(result.getIsbn()).isEqualTo(ISBN_GOOGLE_BOOK);
    }

    @Test
    public void bookNotFoundException() {
        assertThatThrownBy(() -> catalogService.findBook(ISBN_NOT_FOUND))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ISBN " + ISBN_NOT_FOUND + " not found");
    }

    @Test
    @Transactional
    public void addBook() throws BookAlreadyExistsException {
        var result = catalogService.addBook(TestdataCreator.getNewBook());
        assertThat(result.getIsbn()).isEqualTo(ISBN_NEW_BOOK);
    }

    @Test
    @Transactional
    public void bookAlredyExistsException() {
        Book book = TestdataCreator.getNewBook();
        book.setIsbn(ISBN_VALID); // Setting an existing ISBN

        assertThatThrownBy(() -> catalogService.addBook(book))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessageContaining("Book with ISBN " + book.getIsbn() + " already exists");
    }

    @Test
    @Transactional
    public void updateBook() throws BookNotFoundException {
        Book book = TestdataCreator.getNewBook();
        book.setIsbn(ISBN_VALID);
        book.setTitle("New Book Updated");

        var result = catalogService.updateBook(book);
        assertThat(result.getIsbn()).isEqualTo(ISBN_VALID);
        assertThat(result.getTitle()).isEqualTo("New Book Updated");
    }

    @Test
    @Transactional
    public void updateBook_BookNotFoundException() {
        Book book = TestdataCreator.getNewBook();
        book.setIsbn(ISBN_NOT_FOUND);
        assertThatThrownBy(() -> catalogService.findBook(ISBN_NOT_FOUND))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ISBN " + ISBN_NOT_FOUND + " not found");
    }

    @Test
    public void searchBooksByKeywords() {

        String singleKeyword = TestInputValues.SINGLE_KEYWORD.getValue(); // We have two books with -Spring- in title (tests also lower-/upper-case requirement) in localdb and
        List<Book> booksForSingleKeyword = catalogService.searchBook(singleKeyword);
        assertThat(booksForSingleKeyword.size()).isEqualTo(2);

        // Keywords are separated with spaces between
        String multiKeywords1 = TestInputValues.MULTI_KEYWORD1.getValue() + ", " + TestInputValues.MULTI_KEYWORD2.getValue(); // We have 8 books with -Pulications- in publisher and -Action- in title in localdb + Google Books API

        // Mocking Google Books API response as we cannot guarantee its content during tests
        Mockito.when(googleBooksClient.searchBooks(any()))
                .thenReturn(TestdataCreator.getBooksFromGoogleBooksApi());

        List<Book> booksForMultiKeywords = catalogService.searchBook(multiKeywords1);
        assertThat(booksForMultiKeywords.size()).isEqualTo(7);

        // Same as multiKeyword1, but one more criteria which leads to less result
        // Keywords are separated without spaces
        String multiKeywords2 = TestInputValues.MULTI_KEYWORD1.getValue() + "," + TestInputValues.MULTI_KEYWORD2.getValue() + "," + TestInputValues.MULTI_KEYWORD3.getValue(); // We have only 2 books with -Pulications- in publisher and -Action- in title and -Craig- in authors in localdb + Google Books API
        List<Book> booksForMultiKeywords2 = catalogService.searchBook(multiKeywords2);
        assertThat(booksForMultiKeywords2.size()).isEqualTo(3);
    }

    @Test
    public void searchBooksByKeywords_NoDuplicates() {
        // Using keywords that will find the same book in both test data and Google Books API
        // "Automate the Boring Stuff with Python, 3rd Edition" is in test-data-books.sql
        // and also available via Google Books API
        String keywords = TestInputValues.KEYWORDS.getValue();

        List<Book> books = catalogService.searchBook(keywords);
        // Extract all ISBNs from results
        List<String> isbns = books.stream()
                .map(Book::getIsbn)
                .toList();

        // Verify no duplicate ISBNs in results
        long uniqueIsbns = isbns.stream().distinct().count();
        assertThat(isbns.size()).isEqualTo(uniqueIsbns);

        // Verify we got results (at least 1 book)
        assertThat(books).isNotEmpty();

        // Verify that the specific book ISBN appears only once
        String pythonBookIsbn = TestInputValues.PYTHON_BOOK_ISBN.getValue();
        long countOfPythonBook = isbns.stream()
                .filter(isbn -> isbn.equals(pythonBookIsbn))
                .count();
        assertThat(countOfPythonBook).isEqualTo(1);
    }
}
