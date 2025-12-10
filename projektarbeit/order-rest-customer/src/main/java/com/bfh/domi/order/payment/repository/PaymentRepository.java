package com.bfh.domi.order.payment.repository;

import com.bfh.domi.order.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
