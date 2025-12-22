package com.bfh.domi.catalog.catalog.integration.google.dto;

public record Volume(
        String kind,
        String id,
        String etag,
        String selfLink,
        VolumeInfo volumeInfo,
        UserInfo userInfo,
        SaleInfo saleInfo,
        AccessInfo accessInfo,
        SearchInfo searchInfo) {
}
