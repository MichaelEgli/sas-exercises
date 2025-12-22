package com.bfh.domi.catalog.catalog.common.testdata;

import com.bfh.domi.catalog.catalog.model.Book;

import java.math.BigDecimal;
import java.util.Set;

public class TestdataCreator {

    public static Book getNewBook() {
        Book book = new Book();
        book.setIsbn(TestInputValues.ISBN_NEW_BOOK.getValue());
        book.setTitle("New Book");
        book.setSubtitle("New Subtitle");
        book.setAuthors("New Authors");
        book.setPublisher("New Publisher");
        book.setPrice(BigDecimal.valueOf(15.55));

        return book;
    }

    public static Set<Book> getBooksFromGoogleBooksApi() {
        Book book = new Book();
        book.setIsbn(TestInputValues.ISBN_GOOGLE_BOOK.getValue());
        book.setTitle("Google Book Title");
        book.setSubtitle("Google Book Subtitle");
        book.setAuthors("Google Book Authors");
        book.setPublisher("Google Book Publisher");
        book.setPrice(BigDecimal.valueOf(20.00));

        return Set.of(book);
    }
}
