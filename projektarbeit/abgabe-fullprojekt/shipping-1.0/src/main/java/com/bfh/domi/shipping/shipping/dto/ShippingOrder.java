package com.bfh.domi.shipping.shipping.dto;

import java.util.List;

public record ShippingOrder(
        int id,
        Customer customer,
        Address address,
        List<Item> items
) {
}
