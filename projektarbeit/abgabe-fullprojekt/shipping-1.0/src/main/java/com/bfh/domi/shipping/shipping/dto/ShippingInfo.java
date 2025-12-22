package com.bfh.domi.shipping.shipping.dto;

public record ShippingInfo(
        int orderId,
        ShippingStatus status
) {
}
