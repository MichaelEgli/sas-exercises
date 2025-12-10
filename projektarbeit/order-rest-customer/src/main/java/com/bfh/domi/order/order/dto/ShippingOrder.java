package com.bfh.domi.order.order.dto;

import java.util.List;

public record ShippingOrder(
        int id,
        Customer customer,
        Address address,
        List<Item> items
) {
}
