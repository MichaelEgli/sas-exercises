package com.bfh.domi.order.payment.service;

import com.bfh.domi.order.customer.model.CreditCardType;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.payment.exception.PaymentFailedException;
import com.bfh.domi.order.payment.model.Payment;
import com.bfh.domi.order.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Value("${bookstore.payment.limit:1000}")
    private String paymentLimit;

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment makePayment(Customer customer, BigDecimal amount) throws PaymentFailedException {

        validateCreditCard(customer);

        if (amount.compareTo(new BigDecimal(paymentLimit)) > 0) {
            throw new PaymentFailedException(String.format("Payment amount exceeds limit of %s USD for customer ID: %d", paymentLimit, customer.getId()));
        }

        Payment payment = new Payment();
        payment.setDate(LocalDateTime.now());
        payment.setAmount(amount);
        payment.setCreditCardNumber(customer.getCreditCard().getNumber());
        payment.setTransactionId("TXN" + System.currentTimeMillis());

        return paymentRepository.saveAndFlush(payment);
    }

    private void validateCreditCard(Customer customer) throws PaymentFailedException {
        if (customer.getCreditCard() == null || customer.getCreditCard().getNumber() == null) {
            throw new PaymentFailedException("No valid credit card on file for customer ID: " + customer.getId());
        }

        String cardNumber = customer.getCreditCard().getNumber().replaceAll("\\s", "");
        if (!cardNumber.matches("\\d+")) {
            throw new PaymentFailedException("Invalid credit card number format for customer ID: " + customer.getId());
        }

        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        int expirationYear = customer.getCreditCard().getExpirationYear();
        int expirationMonth = customer.getCreditCard().getExpirationMonth();

        if (expirationYear == 0 || expirationMonth == 0) {
            throw new PaymentFailedException("Credit card expiration date missing for customer ID: " + customer.getId());
        }

        if (expirationYear < currentYear || (expirationYear == currentYear && expirationMonth < currentMonth)) {
            throw new PaymentFailedException("Credit card has expired for customer ID: " + customer.getId());
        }

        CreditCardType cardType = customer.getCreditCard().getType();
        if (cardType == null) {
            throw new PaymentFailedException("Credit card type missing for customer ID: " + customer.getId());
        }
    }
}

