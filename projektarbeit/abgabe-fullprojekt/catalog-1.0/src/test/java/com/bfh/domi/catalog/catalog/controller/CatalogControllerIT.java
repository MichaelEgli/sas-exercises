package com.bfh.domi.catalog.catalog.controller;

import com.bfh.domi.catalog.TestcontainersConfiguration;
import com.bfh.domi.catalog.catalog.dto.BookDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/test-data-books.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogControllerIT {

    private static final String BASE_PATH = "/books";
    private final RestClient restClient;

    CatalogControllerIT(@LocalServerPort int port) {
        restClient = RestClient.create("http://localhost:" + port);
    }

    private String loadJsonFromFile(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("requests/" + fileName);
        return Files.readString(resource.getFile().toPath());
    }

    @Test
    void addBook() throws IOException {
        String bookJson = loadJsonFromFile("add-book.json");

        ResponseEntity<BookDto> response = restClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .toEntity(BookDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().isbn()).isNotNull();
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString()).endsWith("/books/" + response.getBody().isbn());
    }

    @Test
    void addExistingBook() throws IOException {
        String bookJson = loadJsonFromFile("add-existing-book.json");

        ResponseEntity<ProblemDetail> response = restClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> System.out.println(res.getStatusCode()))
                .toEntity(ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getDetail()).contains("Book with ISBN 9780134682020 already exists");
    }

    @Test
    void findBook() {
        String isbn = "9780134682020";

        ResponseEntity<BookDto> response = restClient.get().uri(BASE_PATH + "/" + isbn).retrieve().toEntity(BookDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isbn()).isEqualTo(isbn);
    }

    @Test
    void findBook_NotFound() {
        String isbn = "9999999999";

        ResponseEntity<ProblemDetail> response = restClient.get()
                .uri(BASE_PATH + "/" + isbn)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> System.out.println(res.getStatusCode()))
                .toEntity(ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getDetail()).contains("Book with ISBN " + isbn + " not found");
    }

    @Test
    void searchBooks() {
        String keywords = "Boring,Python";

        ResponseEntity<BookDto[]> response = restClient.get().uri(uriBuilder ->
                uriBuilder.path(BASE_PATH)
                        .queryParam("keywords", keywords)
                        .build()
        ).retrieve().toEntity(BookDto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void updateBook() throws IOException {
        String isbn = "9780134682020";
        String bookJson = loadJsonFromFile("update-book.json");

        ResponseEntity<BookDto> response = restClient.put()
                .uri(BASE_PATH + "/" + isbn)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .toEntity(BookDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().title()).isEqualTo("Effective Java - Updated Edition");
    }

    @Test
    void updateBook_NotFound() throws IOException {
        String isbn = "9999999999";
        String bookJson = loadJsonFromFile("update-book-not-found.json");

        ResponseEntity<ProblemDetail> response = restClient.put()
                .uri(BASE_PATH + "/" + isbn)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> System.out.println(res.getStatusCode()))
                .toEntity(ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getDetail()).contains("Book with ISBN " + isbn + " not found");
    }

    @Test
    void updateBook_IsbnMismatch() throws IOException {
        String isbnInPath = "9780134682020";
        String bookJson = loadJsonFromFile("update-book-isbn-mismatch.json");

        ResponseEntity<ProblemDetail> response = restClient.put()
                .uri(BASE_PATH + "/" + isbnInPath)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> System.out.println(res.getStatusCode()))
                .toEntity(ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetail()).contains("ISBN in path and body do not match");
    }

    @Test
    void addBook_MissingTitle() throws IOException {
        String bookJson = loadJsonFromFile("add-book-missing-title.json");

        ResponseEntity<ProblemDetail> response = restClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> System.out.println(res.getStatusCode()))
                .toEntity(ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetail().toLowerCase()).contains("title");
    }

    @Test
    void addBook_InvalidIsbnLength() throws IOException {
        String bookJson = loadJsonFromFile("add-book-invalid-isbn.json");

        ResponseEntity<ProblemDetail> response = restClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> System.out.println(res.getStatusCode()))
                .toEntity(ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetail().toLowerCase()).contains("isbn");
    }

    @Test
    void addBook_MissingIsbn() throws IOException {
        String bookJson = loadJsonFromFile("add-book-missing-isbn.json");

        ResponseEntity<ProblemDetail> response = restClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> System.out.println(res.getStatusCode()))
                .toEntity(ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetail().toLowerCase()).contains("isbn");
    }

    @Test
    void addBook_NegativePrice() throws IOException {
        String bookJson = loadJsonFromFile("add-book-negative-price.json");

        ResponseEntity<ProblemDetail> response = restClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> System.out.println(res.getStatusCode()))
                .toEntity(ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetail().toLowerCase()).contains("price");
    }

    @Test
    void addBook_MissingAuthors() throws IOException {
        String bookJson = loadJsonFromFile("add-book-missing-authors.json");

        ResponseEntity<ProblemDetail> response = restClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> System.out.println(res.getStatusCode()))
                .toEntity(ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetail().toLowerCase()).contains("authors");
    }

    @Test
    void addBook_MissingPublisher() throws IOException {
        String bookJson = loadJsonFromFile("add-book-missing-publisher.json");

        ResponseEntity<ProblemDetail> response = restClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> System.out.println(res.getStatusCode()))
                .toEntity(ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetail().toLowerCase()).contains("publisher");
    }

    @Test
    void addBook_MissingPrice() throws IOException {
        String bookJson = loadJsonFromFile("add-book-missing-price.json");

        ResponseEntity<ProblemDetail> response = restClient.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookJson)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (req, res) -> System.out.println(res.getStatusCode()))
                .toEntity(ProblemDetail.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetail().toLowerCase()).contains("price");
    }
}