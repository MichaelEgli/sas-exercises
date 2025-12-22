package com.bfh.domi.shipping.shipping.dto;

public record Address(
        String street,
        String city,
        String stateProvince,
        String postalCode,
        String country
) {
}
