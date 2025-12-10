package com.bfh.domi.order.payment.service;

import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.payment.exception.PaymentFailedException;
import com.bfh.domi.order.payment.model.Payment;
import com.bfh.domi.order.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment makePayment(Customer customer, BigDecimal amount) throws PaymentFailedException {

        // TODO: Improve validation of Credit Card number
        if (customer.getCreditCard() == null || customer.getCreditCard().getNumber() == null) {
            throw new PaymentFailedException("No valid credit card on file for customer ID: " + customer.getId());
        }

        // TODO: Make limit configurable
        if (amount.compareTo(new BigDecimal("1000")) > 0) {
            throw new PaymentFailedException("Payment amount exceeds limit of 1000 USD for customer ID: " + customer.getId());
        }

        Payment payment = new Payment();
        payment.setDate(LocalDateTime.now());
        payment.setAmount(amount);
        payment.setCreditCardNumber(customer.getCreditCard().getNumber());
        payment.setTransactionId("TXN" + System.currentTimeMillis());

        return paymentRepository.saveAndFlush(payment);
    }

}

