package com.bfh.domi.order.order.integration.catalog;

import com.bfh.domi.order.order.exception.BookNotFoundException;
import com.bfh.domi.order.order.model.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

@Component
public class CatalogClient {

    @Value("${bookstore.catalog.apiurl}")
    private String baseUrl;

    private final RestClient restClient;

    public CatalogClient() {
        this.restClient = RestClient.builder().defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build();
    }

    public Book findBook(String isbn) throws BookNotFoundException {
        try {
            return restClient.get().uri(baseUrl + "/{isbn}", isbn).retrieve().body(Book.class);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BookNotFoundException("Book with ISBN " + isbn + " not found");
            } else {
                throw ex;
            }
        }
    }
}
