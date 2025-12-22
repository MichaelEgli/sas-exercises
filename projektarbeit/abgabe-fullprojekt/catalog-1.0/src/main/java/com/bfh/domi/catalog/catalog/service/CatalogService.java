package com.bfh.domi.catalog.catalog.service;

import com.bfh.domi.catalog.catalog.integration.google.GoogleBooksClient;
import com.bfh.domi.catalog.catalog.model.Book;
import com.bfh.domi.catalog.catalog.exception.BookAlreadyExistsException;
import com.bfh.domi.catalog.catalog.exception.BookNotFoundException;
import com.bfh.domi.catalog.catalog.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CatalogService {

    @Value("${bookstore.catalog.keywordssplitter:,}")
    private String keywordSplitter;

    private final BookRepository bookRepository;

    private final GoogleBooksClient googleBooksClient;

    private final static Logger LOG = LoggerFactory.getLogger(CatalogService.class);

    public CatalogService(BookRepository bookRepository, GoogleBooksClient googleBooksClient) {
        this.bookRepository = bookRepository;
        this.googleBooksClient = googleBooksClient;
    }

    public Book findBook(String isbn) throws BookNotFoundException {
        return bookRepository.findByIsbn(isbn)
                .or(() -> googleBooksClient.findBook(isbn))
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN " + isbn + " not found"));
    }

    public Book addBook(Book book) throws BookAlreadyExistsException {
        Optional<Book> isbnExisting = bookRepository.findByIsbn(book.getIsbn());
        if (isbnExisting.isPresent()) {
            throw new BookAlreadyExistsException("Book with ISBN " + book.getIsbn() + " already exists");
        } else {
            return bookRepository.saveAndFlush(book);
        }
    }

    public Book updateBook(Book book) throws BookNotFoundException {
        Optional<Book> isbnExisting = bookRepository.findByIsbn(book.getIsbn());
        if (isbnExisting.isEmpty()) {
            throw new BookNotFoundException("Book with ISBN " + book.getIsbn() + " not found");
        } else {
            return bookRepository.saveAndFlush(book);
        }
    }

    public List<Book> searchBook(String keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> keywordSet = new LinkedHashSet<>(Arrays.asList(keywords.replace(" ", "").split(keywordSplitter)));

        LOG.info("Searching for books with keywords {}", keywordSet);

        Set<Book> resultSet = new LinkedHashSet<>(bookRepository.findBooksByKeywords(keywordSet));

        for (Book book : resultSet){
            LOG.info("Found book in local database....: ISBN={}, Title={}", book.getIsbn(), book.getTitle());
        }

        resultSet.addAll(googleBooksClient.searchBooks(keywordSet));

        for (Book book : resultSet){
            LOG.info("Resulting Set of Books contains.: ISBN={}, Title={}", book.getIsbn(), book.getTitle());
        }

        return resultSet.stream().toList();
    }
}
