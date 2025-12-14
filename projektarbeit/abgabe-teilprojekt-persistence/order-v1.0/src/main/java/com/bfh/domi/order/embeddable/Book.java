package com.bfh.domi.order.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class Book {

    @Column(name = "BOOK_ISBN")
    private String isbn;
    @Column(name = "BOOK_TITLE")
    private String title;
    @Column(name = "BOOK_PRICE")
    private BigDecimal price;
    @Column(name = "BOOK_AUTHORS")
    private String authors;
    @Column(name = "BOOK_PUBLISHER")
    private String publisher;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
