package com.bfh.domi.order.order.dto;

public record Item(
        BookDto book,
        int quantity
) {
}
