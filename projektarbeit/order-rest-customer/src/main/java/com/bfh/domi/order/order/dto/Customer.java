package com.bfh.domi.order.order.dto;

public record Customer(
        int id,
        String firstName,
        String lastName,
        String email
) {
}
