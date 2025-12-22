package com.bfh.domi.order.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PurchaseOrder(
        @NotNull
        Long customerId,
        @NotEmpty
        List<@Valid PurchaseOrderItem> items
) {
}
