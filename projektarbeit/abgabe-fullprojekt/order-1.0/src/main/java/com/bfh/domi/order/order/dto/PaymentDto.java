package com.bfh.domi.order.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDto(
        LocalDateTime date,
        BigDecimal amount,
        String creditCardNumber,
        String transactionId
) {
}
