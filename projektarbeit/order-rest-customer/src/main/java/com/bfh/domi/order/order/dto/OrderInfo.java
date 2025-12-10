package com.bfh.domi.order.order.dto;

import com.bfh.domi.order.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderInfo(Long id, LocalDateTime date, BigDecimal amount, OrderStatus status) {
}
