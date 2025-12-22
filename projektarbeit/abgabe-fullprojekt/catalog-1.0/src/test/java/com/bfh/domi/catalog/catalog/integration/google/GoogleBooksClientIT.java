package com.bfh.domi.catalog.catalog.integration.google;

import com.bfh.domi.catalog.TestcontainersConfiguration;
import com.bfh.domi.catalog.catalog.model.Book;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Disabled   // Disabled as we can not guarantee that the external Google Books API is always available during tests
class GoogleBooksClientIT {

    @Autowired
    private GoogleBooksClient googleBooksClient;

    @Test
    void findBook() {
        String ISBN = "9783037921173";

        var result = googleBooksClient.findBook(ISBN);
        Book book = result.orElse(null);
        assertThat(book).isNotNull();
        assertThat(book.getIsbn()).isEqualTo(ISBN);
    }

    @Test
    void findBook_NotFound() {
        String ISBN = "0000000000000"; // Non-existing ISBN

        var result = googleBooksClient.findBook(ISBN);
        var book = result.orElse(null);
        assertThat(book).isNull();
    }

    @Test
    void searchBooks() {
        var keywords = Set.of("Kästner", "Klassenzimmer");

        var books = googleBooksClient.searchBooks(keywords);

        for (Book book : books) {
            System.out.println(book.getIsbn() + ": " + book.getTitle() + " - " + book.getAuthors() + " - " + book.getPublisher() + " - " + book.getPrice());
        }
        assertThat(books).isNotEmpty();
        keywords.forEach(
                keyword -> assertThat(
                        books.stream().anyMatch(
                                b -> (b.getTitle() != null && b.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                                        || (b.getAuthors() != null && b.getAuthors().toLowerCase().contains(keyword.toLowerCase()))
                                        || (b.getPublisher() != null && b.getPublisher().toLowerCase().contains(keyword.toLowerCase()))
                        )
                ).isTrue()
        );
        assertThat(books.stream().filter(b -> b.getIsbn() == null).count()).isEqualTo(0);
        assertThat(books.stream().filter(b -> b.getTitle() == null).count()).isEqualTo(0);
        assertThat(books.stream().filter(b -> b.getAuthors() == null).count()).isEqualTo(0);
        assertThat(books.stream().filter(b -> b.getPublisher() == null).count()).isEqualTo(0);
        assertThat(books.stream().filter(b -> b.getPrice() == null).count()).isEqualTo(0);
    }

    @Test
    void searchBooks_NotFound() {
        var keywords = Set.of("hsleiukirlkzgez"); // Nonsense keyword

        var books = googleBooksClient.searchBooks(keywords);
        assertThat(books).isEmpty();
    }
}