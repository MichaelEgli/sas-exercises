package com.bfh.domi.catalog.repos;

import com.bfh.domi.catalog.TestcontainersConfiguration;
import com.bfh.domi.catalog.entities.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        String isbn = "978-0-13-489207-5";
        Optional<Book> bookResult = bookRepository.findByIsbn(isbn);
        assertNotNull(bookResult);
        assertThat(bookResult.get().getIsbn()).isEqualTo(isbn);
    }

    // Query 4: Finde alle Bücher (Book) nach einer beliebigen Anzahl von Keywords (Gross-/Kleinschreibung ignorieren).
    // ALLE Keywords müssen im Buch enthalten sein und zwar entweder im Feld Titel ODER Autoren ODER Herausgeber
    @Test
    @Sql("/test-data-books.sql")
    void q4_testFindBookByKeywords() {

        List<String> singleKeyword = new ArrayList<>();
        singleKeyword.add("spring"); // We have two books with -Spring- in title (they are found even the titles are in uppercase)
        List<Book> booksForSingleKeyword = bookRepository.findBooksByKeywords(singleKeyword);
        assertThat(booksForSingleKeyword.size()).isEqualTo(2);

        List<String> multiKeywords1 = new ArrayList<>();
        multiKeywords1.add("publications"); // We have 6 books with -Pulications- in publisher
        multiKeywords1.add("Action"); // and -Action- in title
        List<Book> booksForMultiKeywords = bookRepository.findBooksByKeywords(multiKeywords1);
        assertThat(booksForMultiKeywords.size()).isEqualTo(6);

        // Same as multiKeyword1, but one more criteria which leads to less result
        List<String> multiKeywords2 = new ArrayList<>();
        multiKeywords2.add("publications"); // We have only 2 books with -Pulications- in publisher
        multiKeywords2.add("Action"); // and -Action- in title
        multiKeywords2.add("Craig"); // and -Craig- in authors
        List<Book> booksForMultiKeywords2 = bookRepository.findBooksByKeywords(multiKeywords2);
        assertThat(booksForMultiKeywords2.size()).isEqualTo(2);

        // No keywords
        List<String> noKeywords = new ArrayList<>();
        List<Book> booksForNoKeywords = bookRepository.findBooksByKeywords(noKeywords);
        assertThat(booksForNoKeywords.size()).isEqualTo(0);

    }
}
