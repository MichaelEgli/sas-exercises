package com.bfh.domi.order.order.exception;

public class OrderAlreadyShippedException extends Exception {
    public OrderAlreadyShippedException(String message) {
        super(message);
    }
}
