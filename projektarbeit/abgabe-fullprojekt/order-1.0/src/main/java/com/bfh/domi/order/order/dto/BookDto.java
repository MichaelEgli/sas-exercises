package com.bfh.domi.order.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BookDto(
        @NotEmpty(message = "Missing book ISBN")
        @Size(min = 10, max = 13, message = "ISBN must be between 10 and 13 characters")
        String isbn,
        @NotEmpty(message = "Missing book title")
        String title,
        @NotEmpty(message = "Missing book author")
        String author,
        @NotEmpty(message = "Missing book publisher")
        String publisher,
        @PositiveOrZero(message = "Price must be zero or positive")
        BigDecimal price
) {
}
