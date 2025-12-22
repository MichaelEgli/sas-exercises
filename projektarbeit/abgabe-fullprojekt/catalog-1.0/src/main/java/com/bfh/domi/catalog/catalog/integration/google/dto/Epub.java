package com.bfh.domi.catalog.catalog.integration.google.dto;

public record Epub(
        boolean isAvailable,
        String downloadLink,
        String acsTokenLink
) {
}
