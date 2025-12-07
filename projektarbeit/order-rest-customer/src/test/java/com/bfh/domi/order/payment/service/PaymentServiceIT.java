package com.bfh.domi.order.payment.service;

import com.bfh.domi.order.TestcontainersConfiguration;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.customer.repository.CustomerRepository;
import com.bfh.domi.order.payment.exception.PaymentFailedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/test-data-04-orders.sql")
class PaymentServiceIT {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @Transactional
    void makePayment() throws PaymentFailedException {
        Long CUSTOMER_ID = 10000L;
        BigDecimal PAYMENT_AMOUNT = BigDecimal.valueOf(500);

        Optional<Customer> customer = customerRepository.findById(CUSTOMER_ID);
        Assertions.assertTrue(customer.isPresent());

        var payment = paymentService.makePayment(customer.get(), PAYMENT_AMOUNT);

        assertThat(payment).isNotNull();
        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getAmount()).isEqualByComparingTo(PAYMENT_AMOUNT);

    }

    @Test
    @Transactional
    void paymentFailed_missingCreditCard() {
        Long CUSTOMER_ID = 20000L;

        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);

        assertThatThrownBy(() -> paymentService.makePayment(customer, BigDecimal.valueOf(500)))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("No valid credit card on file for customer ID: " + CUSTOMER_ID);
    }

    @Test
    @Transactional
    void paymentFailed_paymentLimitExceeded() {
        Long CUSTOMER_ID = 10000L;
        BigDecimal PAYMENT_AMOUNT = BigDecimal.valueOf(1500);

        Optional<Customer> customer = customerRepository.findById(CUSTOMER_ID);
        Assertions.assertTrue(customer.isPresent());

        assertThatThrownBy(() -> paymentService.makePayment(customer.get(), PAYMENT_AMOUNT))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Payment amount exceeds limit of 1000 USD for customer ID: " + CUSTOMER_ID);
    }


}