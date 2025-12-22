package com.bfh.domi.catalog.catalog.integration.google.dto;

public record VolumeInfo(
        String title,
        String subtitle,
        String[] authors,
        String publisher,
        String publishedDate,
        String description,
        IndustryIdentifier[] industryIdentifiers,
        Integer pageCount,
        Dimensions dimensions,
        String printType,
        String mainCategory,
        String[] categories,
        Double averageRating,
        Integer ratingsCount,
        String contentVersion,
        ImageLinks imageLinks,
        String language,
        String previewLink,
        String infoLink,
        String canonicalVolumeLink
) {
}
