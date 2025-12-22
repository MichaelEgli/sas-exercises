package com.bfh.domi.catalog.catalog.integration.google.dto;

import java.time.LocalDateTime;

public record SaleInfo(
        String country,
        String saleability,
        LocalDateTime onSaleDate,
        boolean isEbook,
        ListPrice listPrice,
        RetailPrice retailPrice,
        String buyLink
) {
}
