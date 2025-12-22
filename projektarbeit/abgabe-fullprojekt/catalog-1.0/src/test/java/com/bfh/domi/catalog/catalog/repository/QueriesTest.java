package com.bfh.domi.catalog.catalog.repository;

import com.bfh.domi.catalog.TestcontainersConfiguration;
import com.bfh.domi.catalog.catalog.common.testdata.TestInputValues;
import com.bfh.domi.catalog.catalog.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class QueriesTest {

    @Autowired
    private BookRepository bookRepository;

    // Query 3: Finde das Buch (Book) mit einer bestimmten ISBN-Nummer
    @Test
    @Sql("/test-data-books.sql")
    void q3_testFindBookByIsbn() {
        String isbn = TestInputValues.VALID_ISBN.getValue();
        Optional<Book> bookResult = bookRepository.findByIsbn(isbn);
        assertNotNull(bookResult);
        assertThat(bookResult.get().getIsbn()).isEqualTo(isbn);
    }

    // Query 4: Finde alle Bücher (Book) nach einer beliebigen Anzahl von Keywords (Gross-/Kleinschreibung ignorieren).
    // ALLE Keywords müssen im Buch enthalten sein und zwar entweder im Feld Titel ODER Autoren ODER Herausgeber
    @Test
    @Sql("/test-data-books.sql")
    void q4_testFindBookByKeywords() {
        Set<String> singleKeyword = new HashSet<>();
        singleKeyword.add(TestInputValues.SINGLE_KEYWORD.getValue()); // We have two books with -Spring- in title (they are found even the titles are in uppercase)
        List<Book> booksForSingleKeyword = bookRepository.findBooksByKeywords(singleKeyword);
        assertThat(booksForSingleKeyword.size()).isEqualTo(2);

        Set<String> multiKeywords1 = new HashSet<>();
        multiKeywords1.add(TestInputValues.MULTI_KEYWORD1.getValue()); // We have 6 books with -Pulications- in publisher
        multiKeywords1.add(TestInputValues.MULTI_KEYWORD2.getValue()); // and -Action- in title
        List<Book> booksForMultiKeywords = bookRepository.findBooksByKeywords(multiKeywords1);
        assertThat(booksForMultiKeywords.size()).isEqualTo(6);

        // Same as multiKeyword1, but one more criteria which leads to less result
        Set<String> multiKeywords2 = new HashSet<>();
        multiKeywords2.add(TestInputValues.MULTI_KEYWORD1.getValue()); // We have only 2 books with -Pulications- in publisher
        multiKeywords2.add(TestInputValues.MULTI_KEYWORD2.getValue()); // and -Action- in title
        multiKeywords2.add(TestInputValues.MULTI_KEYWORD3.getValue()); // and -Craig- in authors
        List<Book> booksForMultiKeywords2 = bookRepository.findBooksByKeywords(multiKeywords2);
        assertThat(booksForMultiKeywords2.size()).isEqualTo(2);

        // No keywords
        Set<String> noKeywords = new HashSet<>();
        List<Book> booksForNoKeywords = bookRepository.findBooksByKeywords(noKeywords);
        assertThat(booksForNoKeywords.size()).isEqualTo(0);
    }
}
