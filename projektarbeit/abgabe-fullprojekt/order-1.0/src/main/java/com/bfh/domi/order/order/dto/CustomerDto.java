package com.bfh.domi.order.order.dto;

public record CustomerDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
