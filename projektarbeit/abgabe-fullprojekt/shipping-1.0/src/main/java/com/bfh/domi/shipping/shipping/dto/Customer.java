package com.bfh.domi.shipping.shipping.dto;

public record Customer(
        int id,
        String firstName,
        String lastName,
        String email
) {
}
