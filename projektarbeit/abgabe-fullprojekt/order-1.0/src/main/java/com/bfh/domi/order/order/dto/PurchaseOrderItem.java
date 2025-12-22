package com.bfh.domi.order.order.dto;

import jakarta.validation.constraints.*;

public record PurchaseOrderItem(
        @NotBlank(message = "Missing book ISBN")
        @Size(min = 10, max = 13, message = "ISBN must be between 10 and 13 characters")
        String isbn,
        @NotNull
        @Min(1)
        Integer quantity
) {
}
