package com.bfh.domi.order.order.repository;

import com.bfh.domi.order.TestcontainersConfiguration;
import com.bfh.domi.order.common.model.Address;
import com.bfh.domi.order.customer.repository.CustomerRepository;
import com.bfh.domi.order.order.model.Book;
import com.bfh.domi.order.customer.model.CreditCard;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.order.model.Order;
import com.bfh.domi.order.order.model.OrderItem;
import com.bfh.domi.order.payment.model.Payment;
import com.bfh.domi.order.customer.model.CreditCardType;
import com.bfh.domi.order.order.model.OrderStatus;
import com.bfh.domi.order.payment.repository.PaymentRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private EntityManager em;

    // Schritt 1: Bestellung erstellen, finden, löschen
    @Test
    void createOrder() {
        String FIRSTNAME = "Michael";
        String LASTNAME = "Rothenbühler";
        String CREDIT_CARD = "7575 7575 7575 7575";
        String ORDER_ITEM_TITLE = "Lerne Java Spring";
        String ADDRESS_COUNTRY = "Switzerland";

        // Customer
        Address address = new Address();
        address.setStreet("Grabmattweg 16");
        address.setStateProvince("BE");
        address.setPostalCode("3176");
        address.setCity("Neuenegg");
        address.setCountry(ADDRESS_COUNTRY);

        CreditCard creditCard = new CreditCard();
        creditCard.setType(CreditCardType.VISA);
        creditCard.setNumber(CREDIT_CARD);
        creditCard.setExpirationMonth(10);
        creditCard.setExpirationYear(2028);

        Customer customer = new Customer();
        customer.setFirstName(FIRSTNAME);
        customer.setLastName(LASTNAME);
        customer.setEmail("michael.rothenbuehler@gmail.com");
        customer.setAddress(address);
        customer.setCreditCard(creditCard);

        Customer savedCustomer = customerRepository.saveAndFlush(customer);
        assertThat(savedCustomer.getFirstName()).isEqualTo(FIRSTNAME);
        assertThat(savedCustomer.getLastName()).isEqualTo(LASTNAME);

        // Payment
        Payment payment = new Payment();
        payment.setDate(LocalDateTime.now());
        payment.setAmount(BigDecimal.valueOf(75.5));
        payment.setCreditCardNumber(CREDIT_CARD);
        payment.setTransactionId("7654321");

        Payment savedPayment = paymentRepository.saveAndFlush(payment);
        assertThat(savedPayment.getCreditCardNumber()).isEqualTo(CREDIT_CARD);

        // Order
        Order order = new Order();
        order.setDate(LocalDateTime.now());
        order.setAmount(BigDecimal.valueOf(75.5));
        order.setStatus(OrderStatus.ACCEPTED);
        order.setCustomer(customer);
        order.setPayment(savedPayment);
        order.setOrderAddress(savedCustomer.getAddress());

        Order savedOrderWithoutItems = orderRepository.saveAndFlush(order);
        assertThat(savedOrderWithoutItems.getCustomer().getFirstName()).isEqualTo(FIRSTNAME);
        assertThat(savedOrderWithoutItems.getCustomer().getLastName()).isEqualTo(LASTNAME);
        assertThat(savedOrderWithoutItems.getOrderAddress().getCountry()).isEqualTo(ADDRESS_COUNTRY);

        // Order Item
        Book book = new Book();
        book.setIsbn("978-0-439-02348-1");
        book.setTitle(ORDER_ITEM_TITLE);
        book.setAuthors("Dominik & Michael");
        book.setPublisher("Flying Pirates Verlag");
        book.setPrice(BigDecimal.valueOf(33.50));

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(2);
        orderItem.setBook(book);
        orderItem.setOrder(savedOrderWithoutItems);

        OrderItem savedItem = orderItemRepository.saveAndFlush(orderItem);
        assertThat(savedItem.getBook().getTitle()).isEqualTo(ORDER_ITEM_TITLE);

        em.clear();

        Customer customerFromDb = em.find(Customer.class, customer.getId());
        assertThat(customerFromDb.getId()).isNotNull();
        assertThat(customerFromDb.getFirstName()).isEqualTo(FIRSTNAME);
        assertThat(customerFromDb.getLastName()).isEqualTo(LASTNAME);

        Payment paymentFromDb = em.find(Payment.class, payment.getId());
        assertThat(paymentFromDb.getCreditCardNumber()).isEqualTo(CREDIT_CARD);

        Order orderFromDb = em.find(Order.class, order.getId());
        assertThat(orderFromDb.getId()).isNotNull();
        assertThat(orderFromDb.getOrderItems().iterator().next().getBook().getTitle()).isEqualTo(ORDER_ITEM_TITLE);
        assertThat(orderFromDb.getOrderAddress().getCountry()).isEqualTo(ADDRESS_COUNTRY);
    }

    @Test
    @Sql("/test-data-04-orders.sql")
    void findOrder() {
        Long ORDER_ID = 100000L;

        Optional<Order> foundOrder = orderRepository.findById(ORDER_ID);
        assertThat(foundOrder.isPresent()).isTrue();
        assertThat(foundOrder.get().getId()).isEqualTo(ORDER_ID);
        assertThat(foundOrder.get().getOrderItems().size()).isEqualTo(2);
        assertThat(foundOrder.get().getCustomer().getFirstName()).isEqualTo("Anna");
    }

    @Test
    @Sql("/test-data-04-orders.sql")
    void deleteOrder() {
        Long ORDER_ID = 100000L;
        Long ORDER_ITEM_1_ID = 1000L;
        Long ORDER_ITEM_2_ID = 1001L;
        Long PAYMENT_ID = 1000L;
        Long CUSTOMER_ID = 10000L;

        orderRepository.deleteById(ORDER_ID);
        orderRepository.flush();

        Optional<Order> deletedOrder = orderRepository.findById(ORDER_ID);
        assertThat(deletedOrder.isPresent()).isFalse();

        // Dependency Test: OrderItems should be deleted
        Optional<OrderItem> deletedOrderItem1 = orderItemRepository.findById(ORDER_ITEM_1_ID);
        assertThat(deletedOrderItem1.isPresent()).isFalse();
        Optional<OrderItem> deletedOrderItem2 = orderItemRepository.findById(ORDER_ITEM_2_ID);
        assertThat(deletedOrderItem2.isPresent()).isFalse();

        // Dependency Test: Payment should be deleted
        Optional<Payment> payment = paymentRepository.findById(PAYMENT_ID);
        assertThat(payment.isPresent()).isFalse();

        // Dependency Test: Customer should NOT be deleted
        Optional<Customer> customer = customerRepository.findById(CUSTOMER_ID);
        assertThat(customer.isPresent()).isTrue();
    }
}
