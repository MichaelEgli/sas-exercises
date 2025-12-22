package com.bfh.domi.order.order.service;

import com.bfh.domi.order.TestcontainersConfiguration;
import com.bfh.domi.order.customer.exception.CustomerNotFoundException;
import com.bfh.domi.order.order.dto.ShippingInfo;
import com.bfh.domi.order.order.exception.OrderAlreadyShippedException;
import com.bfh.domi.order.order.exception.OrderNotFoundException;
import com.bfh.domi.order.order.model.Book;
import com.bfh.domi.order.order.model.Order;
import com.bfh.domi.order.order.model.OrderItem;
import com.bfh.domi.order.order.model.OrderStatus;
import com.bfh.domi.order.order.repository.OrderRepository;
import com.bfh.domi.order.payment.exception.PaymentFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@EnabledIfEnvironmentVariable(named = "SPRING_PROFILES_ACTIVE", matches = "gitlab")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/test-data-50-orders.sql")
@TestPropertySource(properties = {
        "spring.jms.template.receive-timeout=200"
})
class OrderServiceE2eIT {

    @Value("${bookstore.shipping.orderqueue}")
    private String shippingOrderQueue;
    @Value("${bookstore.shipping.infoqueue}")
    private String shippingInfoQueue;
    @Value("${bookstore.shipping.cancelqueue}")
    private String shippingCancelQueue;
    @Value("${bookstore.shipping.processingtime}")
    private long processingTime;
    @Value("${bookstore.test.asyncprocessingtime:1000}")
    private long asyncProcessingTime;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JmsTemplate jmsTemplate;

    Long CUSTOMER_ID = 10000L;

    @BeforeEach
    void drainShippingQueues() {
        while (true) {
            var message = (ShippingInfo) jmsTemplate.receiveAndConvert(shippingOrderQueue);
            if (message == null) break;
        }
        while (true) {
            var message = (ShippingInfo) jmsTemplate.receiveAndConvert(shippingInfoQueue);
            if (message == null) break;
        }
        while (true) {
            var message = (ShippingInfo) jmsTemplate.receiveAndConvert(shippingCancelQueue);
            if (message == null) break;
        }
    }

    @Test
    void placeOrder() throws CustomerNotFoundException, PaymentFailedException, InterruptedException {
        Book book = new Book();
        book.setIsbn("978-3-16-148410-0");
        book.setTitle("Test Book");
        book.setAuthors("John Doe");
        book.setPublisher("Test Publisher");
        book.setPrice(new BigDecimal("29.99"));

        OrderItem orderItem = new OrderItem();
        orderItem.setBook(book);
        orderItem.setQuantity(2);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        Order placedOrder = orderService.placeOrder(CUSTOMER_ID, orderItems);
        assertThat(placedOrder).isNotNull();
        assertThat(placedOrder.getId()).isNotNull();

        Thread.sleep(asyncProcessingTime);

        Optional<Order> processedOrder = orderRepository.findById(placedOrder.getId());
        assertThat(processedOrder).isPresent();
        assertThat(processedOrder.get().getStatus()).isEqualTo(OrderStatus.PROCESSING);

        Thread.sleep(processingTime + asyncProcessingTime);

        Optional<Order> shippedOrder = orderRepository.findById(placedOrder.getId());
        assertThat(shippedOrder).isPresent();
        assertThat(shippedOrder.get().getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void cancelOrder() throws CustomerNotFoundException, PaymentFailedException, InterruptedException, OrderNotFoundException, OrderAlreadyShippedException {
        Book book = new Book();
        book.setIsbn("978-3-16-148410-0");
        book.setTitle("Test Book");
        book.setAuthors("John Doe");
        book.setPublisher("Test Publisher");
        book.setPrice(new BigDecimal("29.99"));

        OrderItem orderItem = new OrderItem();
        orderItem.setBook(book);
        orderItem.setQuantity(2);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        Order placedOrder = orderService.placeOrder(CUSTOMER_ID, orderItems);
        assertThat(placedOrder).isNotNull();
        assertThat(placedOrder.getId()).isNotNull();

        Thread.sleep(asyncProcessingTime);

        Optional<Order> processedOrder = orderRepository.findById(placedOrder.getId());
        assertThat(processedOrder).isPresent();
        assertThat(processedOrder.get().getStatus()).isEqualTo(OrderStatus.PROCESSING);

        Thread.sleep(processingTime - asyncProcessingTime - 100);

        orderService.cancelOrder(placedOrder.getId());

        Thread.sleep(asyncProcessingTime);

        Optional<Order> canceledOrder = orderRepository.findById(placedOrder.getId());
        assertThat(canceledOrder).isPresent();
        assertThat(canceledOrder.get().getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    void cancelOrder_whenTooLate_throws_exception() throws CustomerNotFoundException, PaymentFailedException, InterruptedException {
        Book book = new Book();
        book.setIsbn("978-3-16-148410-0");
        book.setTitle("Test Book");
        book.setAuthors("John Doe");
        book.setPublisher("Test Publisher");
        book.setPrice(new BigDecimal("29.99"));

        OrderItem orderItem = new OrderItem();
        orderItem.setBook(book);
        orderItem.setQuantity(2);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        Order placedOrder = orderService.placeOrder(CUSTOMER_ID, orderItems);
        assertThat(placedOrder).isNotNull();
        assertThat(placedOrder.getId()).isNotNull();

        Thread.sleep(asyncProcessingTime);

        Optional<Order> processedOrder = orderRepository.findById(placedOrder.getId());
        assertThat(processedOrder).isPresent();
        assertThat(processedOrder.get().getStatus()).isEqualTo(OrderStatus.PROCESSING);

        Thread.sleep(processingTime + asyncProcessingTime);

        assertThatThrownBy(() -> orderService.cancelOrder(placedOrder.getId()))
                .isInstanceOf(OrderAlreadyShippedException.class);
    }

}