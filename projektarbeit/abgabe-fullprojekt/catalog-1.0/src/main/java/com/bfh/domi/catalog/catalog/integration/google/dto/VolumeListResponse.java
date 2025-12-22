package com.bfh.domi.catalog.catalog.integration.google.dto;

public record VolumeListResponse(
        String kind,
        Integer totalItems,
        Volume[] items
) {}
