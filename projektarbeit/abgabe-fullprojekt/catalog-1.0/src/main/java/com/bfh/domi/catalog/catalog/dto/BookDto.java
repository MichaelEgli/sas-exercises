package com.bfh.domi.catalog.catalog.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

public record BookDto(
        @NotEmpty(message = "Missing book ISBN")
        @Size(min = 10, max = 13, message = "ISBN must be between 10 and 13 characters")
        String isbn,
        @NotEmpty(message = "Missing book title")
        String title,
        @NotEmpty(message = "Missing book authors")
        String authors,
        @NotEmpty(message = "Missing book publisher")
        String publisher,
        @Min(value = 1000, message = "Publication year must be a four-digit year")
        @Max(value = 9999, message = "Publication year must be a four-digit year")
        Integer publicationYear,
        @PositiveOrZero(message= "Number of pages must be zero or positive")
        Integer numberOfPages,
        String description,
        @URL(message = "Image URL must be a valid URL")
        String imageUrl,
        @NotNull(message = "Missing book price")
        @DecimalMin(value = "0.0", message = "Price must be zero or positive")
        BigDecimal price
) {
}
