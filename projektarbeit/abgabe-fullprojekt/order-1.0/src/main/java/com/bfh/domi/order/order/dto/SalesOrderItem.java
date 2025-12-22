package com.bfh.domi.order.order.dto;

public record SalesOrderItem(
        BookDto book,
        Integer quantity
) {
}
