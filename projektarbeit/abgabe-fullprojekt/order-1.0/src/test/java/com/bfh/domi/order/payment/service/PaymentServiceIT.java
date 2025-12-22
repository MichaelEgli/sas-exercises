package com.bfh.domi.order.payment.service;

import com.bfh.domi.order.TestcontainersConfiguration;
import com.bfh.domi.order.common.testdata.TestdataCreator;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.customer.repository.CustomerRepository;
import com.bfh.domi.order.payment.exception.PaymentFailedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@SpringBootTest(properties = {
        "spring.jms.listener.auto-startup=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/test-data-04-orders.sql")
class PaymentServiceIT {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CustomerRepository customerRepository;
    @Value("${bookstore.payment.limit:1000}")
    private String paymentLimit;

    Long CUSTOMER_ID = TestdataCreator.getCustomerId();

    @Test
    @Transactional
    void makePayment() throws PaymentFailedException {
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
        BigDecimal PAYMENT_AMOUNT = BigDecimal.valueOf(1500);

        Optional<Customer> customer = customerRepository.findById(CUSTOMER_ID);
        Assertions.assertTrue(customer.isPresent());
        assertThatThrownBy(() -> paymentService.makePayment(customer.get(), PAYMENT_AMOUNT))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining(String.format("Payment amount exceeds limit of %s USD for customer ID: %d", paymentLimit, CUSTOMER_ID));
    }

    @Test
    @Transactional
    void paymentFailed_invalidCreditCardNumberFormat() {
        Optional<Customer> customer = customerRepository.findById(CUSTOMER_ID);
        Assertions.assertTrue(customer.isPresent());

        // Modify credit card number to include invalid characters
        customer.get().getCreditCard().setNumber("1234-ABCD-5678");
        assertThatThrownBy(() -> paymentService.makePayment(customer.get(), BigDecimal.valueOf(500)))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Invalid credit card number format for customer ID: " + CUSTOMER_ID);
    }

    @Test
    @Transactional
    void paymentFailed_expiredCreditCard() {
        Optional<Customer> customer = customerRepository.findById(CUSTOMER_ID);
        Assertions.assertTrue(customer.isPresent());

        // Set expired date
        customer.get().getCreditCard().setExpirationYear(2020);
        customer.get().getCreditCard().setExpirationMonth(1);
        assertThatThrownBy(() -> paymentService.makePayment(customer.get(), BigDecimal.valueOf(500)))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Credit card has expired for customer ID: " + CUSTOMER_ID);
    }

    @Test
    @Transactional
    void paymentFailed_missingExpirationDate() {
        Optional<Customer> customer = customerRepository.findById(CUSTOMER_ID);
        Assertions.assertTrue(customer.isPresent());

        customer.get().getCreditCard().setExpirationYear(0);
        customer.get().getCreditCard().setExpirationMonth(0);
        assertThatThrownBy(() -> paymentService.makePayment(customer.get(), BigDecimal.valueOf(500)))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Credit card expiration date missing for customer ID: " + CUSTOMER_ID);
    }

    @Test
    @Transactional
    void paymentFailed_missingCreditCardType() {
        Optional<Customer> customer = customerRepository.findById(CUSTOMER_ID);
        Assertions.assertTrue(customer.isPresent());

        customer.get().getCreditCard().setType(null);
        assertThatThrownBy(() -> paymentService.makePayment(customer.get(), BigDecimal.valueOf(500)))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Credit card type missing for customer ID: " + CUSTOMER_ID);
    }
}