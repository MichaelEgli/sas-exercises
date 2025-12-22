package com.bfh.domi.catalog.catalog.integration.google.dto;

public record DownloadAccess(
        String kind,
        String volumeId,
        boolean restricted,
        boolean deviceAllowed,
        boolean justAcquired,
        Integer maxDownloadDevices,
        Integer downloadsAcquired,
        String nonce,
        String source,
        String reasonCode,
        String message,
        String signature
) {
}
