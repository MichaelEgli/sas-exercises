package com.bfh.domi.catalog.catalog.integration.google.dto;

public record Pdf(
        boolean isAvailable,
        String downloadLink,
        String acsTokenLink
) {
}
