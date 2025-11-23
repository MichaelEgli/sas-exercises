package com.bfh.domi.order.dto;

import com.bfh.domi.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderInfo(Long id, LocalDateTime date, BigDecimal amount, OrderStatus status) {
}
