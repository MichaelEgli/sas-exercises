package com.bfh.domi.catalog.catalog.integration.google;

import com.bfh.domi.catalog.catalog.integration.google.dto.Volume;
import com.bfh.domi.catalog.catalog.integration.google.dto.VolumeListResponse;
import com.bfh.domi.catalog.catalog.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class GoogleBooksClient {

    private final static Logger LOG = LoggerFactory.getLogger(GoogleBooksClient.class);

    @Value("${bookstore.googlebooks.apiurl}")
    private String baseUrl;

    @Value("${bookstore.googlebooks.maxresults:20}")
    private int maxResults;

    private final RestClient restClient;

    private final ConversionService conversionService;

    public GoogleBooksClient(ConversionService conversionService) {
        this.conversionService = conversionService;
        this.restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Optional<Book> findBook(String isbn) {
        try {
            VolumeListResponse response = restClient.get()
                    .uri(baseUrl + "/volumes?q=isbn:{isbn}", isbn)
                    .retrieve()
                    .body(VolumeListResponse.class);

            if (response == null || response.items() == null || response.items().length == 0) {
                return Optional.empty();
            }

            Book book = conversionService.convert(response.items()[0], Book.class);

            return hasMandatoryFields(book) ? Optional.of(book) : Optional.empty();

        } catch (Exception e) {
            LOG.error("Error calling Google Books API for ISBN {}: {}", isbn, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public Set<Book> searchBooks(Set<String> keywords) {

        Set<Book> bookSet = new LinkedHashSet<>();

        String queryString = String.join("+", keywords);

        try {
            VolumeListResponse response = restClient.get()
                    .uri(baseUrl + "/volumes?q={queryString}&maxResults={maxResults}", queryString, maxResults)
                    .retrieve()
                    .body(VolumeListResponse.class);

            if (response == null || response.items() == null) return bookSet;

            for (Volume volume : response.items()) {
                Book book = conversionService.convert(volume, Book.class);
                if (hasMandatoryFields(book)) {
                    bookSet.add(book);
                }
            }

            for (Book book : bookSet) {
                LOG.info("Found book from Google Books API: ISBN={}, Title={}", book.getIsbn(), book.getTitle());
            }

        } catch (Exception e) {
            LOG.error("Error calling Google Books API for search query '{}': {}", queryString, e.getMessage(), e);
        }

        return bookSet;
    }

    private boolean hasMandatoryFields(Book book) {
        return book.getIsbn() != null && book.getAuthors() != null && book.getPublisher() != null && book.getPrice() != null;
    }
}
