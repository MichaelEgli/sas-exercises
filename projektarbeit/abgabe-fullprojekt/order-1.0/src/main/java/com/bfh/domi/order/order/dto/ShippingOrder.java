package com.bfh.domi.order.order.dto;

import java.util.List;

public record ShippingOrder(
        int id,
        CustomerDto customer,
        AddressDto address,
        List<Item> items
) {
}
