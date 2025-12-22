package com.bfh.domi.catalog.catalog.integration.google.dto;

import java.time.LocalDateTime;

public record UserInfo(
        Object review,
        Object readingPosition,
        boolean isPurchased,
        boolean isPreordered,
        LocalDateTime updated
) {
}
