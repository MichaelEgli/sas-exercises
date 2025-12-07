package com.bfh.domi.order.order.service;

import com.bfh.domi.order.TestcontainersConfiguration;
import com.bfh.domi.order.common.client.ShippingClient;
import com.bfh.domi.order.customer.exception.CustomerNotFoundException;
import com.bfh.domi.order.order.dto.ShippingOrder;
import com.bfh.domi.order.order.exception.OrderAlreadyShippedException;
import com.bfh.domi.order.order.exception.OrderNotFoundException;
import com.bfh.domi.order.order.model.Book;
import com.bfh.domi.order.order.model.OrderItem;
import com.bfh.domi.order.order.model.OrderStatus;
import com.bfh.domi.order.payment.exception.PaymentFailedException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/test-data-50-orders.sql")
class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private ShippingClient shippingClient;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${shipping.info.queue}")
    private String shippingInfoQueue;

    Long CUSTOMER_ID = 10000L;
    Long VALID_ORDER_ID = 100000L;
    Long INVALID_ORDER_ID = 999999L;
    Long SHIPPED_ORDER_ID = 100100L;
    Long ACCEPTED_ORDER_ID = 101500L;

    @Test
    void findOrder() throws OrderNotFoundException {
        var result = orderService.findOrder(VALID_ORDER_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(VALID_ORDER_ID);

    }

    @Test
    void orderNotFound() {
        assertThatThrownBy(() -> orderService.findOrder(INVALID_ORDER_ID))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with id: " + INVALID_ORDER_ID);
    }

    @Test
    void searchOrders() {
        int YEAR = 2023;

        var results = orderService.searchOrders(CUSTOMER_ID, YEAR);

        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.getFirst().date().getYear()).isEqualTo(YEAR);

    }

    @Test
    @Transactional
    void placeOrder() throws PaymentFailedException, CustomerNotFoundException {

        Mockito.doNothing().when(shippingClient).sendShippingOrder(Mockito.any(ShippingOrder.class));

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

        var newOrder = orderService.placeOrder(CUSTOMER_ID, orderItems);

        assertThat(newOrder).isNotNull();
        assertThat(newOrder.getId()).isNotNull();
        assertThat(newOrder.getCustomer().getId()).isEqualTo(CUSTOMER_ID);
        assertThat(newOrder.getOrderItems()).isNotNull();
        assertThat(newOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(newOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(newOrder.getAmount()).isEqualTo(book.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
    }

    @Test
    @Transactional
    void cancelOrder() throws OrderNotFoundException, OrderAlreadyShippedException {
        orderService.cancelOrder(ACCEPTED_ORDER_ID);

        var canceledOrder = orderService.findOrder(ACCEPTED_ORDER_ID);

        assertThat(canceledOrder).isNotNull();
        assertThat(canceledOrder.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @Transactional
    void cancelOrderNotFound() {
        assertThatThrownBy(() -> orderService.cancelOrder(INVALID_ORDER_ID))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with id: " + INVALID_ORDER_ID);
    }

    @Test
    @Transactional
    void cancelOrderAlreadyShipped() {
        assertThatThrownBy(() -> orderService.cancelOrder(SHIPPED_ORDER_ID))
                .isInstanceOf(OrderAlreadyShippedException.class)
                .hasMessageContaining("Order with id: " + SHIPPED_ORDER_ID + " has already been shipped and cannot be canceled.");
    }
}