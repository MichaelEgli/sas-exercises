package com.bfh.domi.order.order.model;

import com.bfh.domi.order.common.model.Address;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.payment.model.Payment;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "BOOK_ORDER")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_order_seq")
    @SequenceGenerator(name = "book_order_seq", sequenceName = "book_order_seq")
    private Long id;
    @Column(name = "ORDER_DATE")
    private LocalDateTime date;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @OneToOne
    private Customer customer;
    // Payment must be removed if Order is removed
    @OneToOne(orphanRemoval = true)
    private Payment payment;
    // OrderItems must be removed if Order is removed
    // No cascade needed as we don't persist OrderItems via Order
    // Lazy fetch to avoid loading all items when loading Order
    // Use Set to make order items unique
    @OneToMany(mappedBy = "order", orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();
    @Embedded
    private Address orderAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItem) {
        this.orderItems = orderItem;
    }

    public Address getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(Address orderAdress) {
        this.orderAddress = orderAdress;
    }
}
