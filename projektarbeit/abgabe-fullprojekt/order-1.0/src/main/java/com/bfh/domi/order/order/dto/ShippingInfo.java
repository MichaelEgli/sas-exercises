package com.bfh.domi.order.order.dto;

public record ShippingInfo(
        int orderId,
        ShippingStatus status
) {
}
