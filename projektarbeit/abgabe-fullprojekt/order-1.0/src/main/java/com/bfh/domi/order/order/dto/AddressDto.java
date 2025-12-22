package com.bfh.domi.order.order.dto;

public record AddressDto(
        String street,
        String city,
        String stateProvince,
        String postalCode,
        String country
) {
}
