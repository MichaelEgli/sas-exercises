package com.bfh.domi.order.entity;

import com.bfh.domi.order.embeddable.Book;
import jakarta.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    @SequenceGenerator(name = "order_item_seq", sequenceName = "order_item_seq")
    private Long id;
    private int quantity;
    @Embedded
    private Book book;
    // OrderItem can be persisted without an update in Order, so no cascade needed
    @ManyToOne
    private Order order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
