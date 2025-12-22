package com.bfh.domi.order.order.repository;

import com.bfh.domi.order.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
