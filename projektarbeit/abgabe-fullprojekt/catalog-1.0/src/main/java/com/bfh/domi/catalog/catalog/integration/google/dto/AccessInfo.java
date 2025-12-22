package com.bfh.domi.catalog.catalog.integration.google.dto;

public record AccessInfo(
        String country,
        String viewability,
        Boolean embeddable,
        Boolean publicDomain,
        String textToSpeechPermission,
        Epub epub,
        Pdf pdf,
        String webReaderLink,
        String accessViewStatus,
        DownloadAccess downloadAccess) {
}
