package com.bfh.domi.order.order.service;

import com.bfh.domi.order.common.logging.LoggingService;
import com.bfh.domi.order.order.integration.shipping.ShippingClient;
import com.bfh.domi.order.customer.exception.CustomerNotFoundException;
import com.bfh.domi.order.customer.service.CustomerService;
import com.bfh.domi.order.order.dto.*;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.order.model.Order;
import com.bfh.domi.order.order.model.OrderItem;
import com.bfh.domi.order.order.model.OrderStatus;
import com.bfh.domi.order.order.repository.OrderItemRepository;
import com.bfh.domi.order.order.repository.OrderRepository;
import com.bfh.domi.order.order.exception.OrderAlreadyShippedException;
import com.bfh.domi.order.order.exception.OrderNotFoundException;
import com.bfh.domi.order.payment.exception.PaymentFailedException;
import com.bfh.domi.order.payment.service.PaymentService;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    private final CustomerService customerService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShippingClient shippingClient;
    private final ConversionService conversionService;
    private final LoggingService loggingService;

    public OrderService(CustomerService customerService, PaymentService paymentService, OrderRepository orderRepository, OrderItemRepository orderItemRepository, ShippingClient shippingClient, ConversionService conversionService, LoggingService loggingService) {
        this.customerService = customerService;
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.shippingClient = shippingClient;
        this.conversionService = conversionService;
        this.loggingService = loggingService;
    }

    public Order findOrder(long id) throws OrderNotFoundException {
        return orderRepository.findOrderById(id).orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    public List<OrderInfo> searchOrders(long customerId, int year) {

        LocalDateTime startYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endYear = LocalDateTime.of(year, 12, 31, 23, 59);

        return orderRepository.findAllByCustomerIdAndDateBetween(customerId, startYear, endYear);
    }

    @Transactional(rollbackFor = IllegalStateException.class)
    public Order placeOrder(long customerId, List<OrderItem> items) throws CustomerNotFoundException, PaymentFailedException {

        Customer customer = customerService.findCustomer(customerId);

        Set<OrderItem> orderItems = new HashSet<>(items);

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderAddress(customer.getAddress());
        order.setDate(LocalDateTime.now());
        order.setAmount(calculateTotalAmount(orderItems));
        order.setPayment(paymentService.makePayment(customer, order.getAmount()));
        order.setStatus(OrderStatus.ACCEPTED);
        order.setOrderItems(orderItems);
        Order savedOrder;

        try {
            savedOrder = orderRepository.save(order);
            orderItems.forEach(oi -> {
                oi.setOrder(order);
                orderItemRepository.save(oi);
            });
        } catch (Exception e) {
            loggingService.log(e.getMessage());
            throw new IllegalStateException(e);
        }

        shippingClient.sendShippingOrder(Objects.requireNonNull(conversionService.convert(savedOrder, ShippingOrder.class)));

        return savedOrder;
    }

    @Transactional(rollbackFor = IllegalStateException.class)
    public void cancelOrder(long id) throws OrderNotFoundException, OrderAlreadyShippedException {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        if (order.get().getStatus() == OrderStatus.SHIPPED) {
            throw new OrderAlreadyShippedException("Order with id: " + id + " has already been shipped and cannot be canceled.");
        }
//        order.get().setStatus(OrderStatus.CANCELED);

        try {
            shippingClient.sendShippingCancel(conversionService.convert(order.get(), ShippingCancel.class));
        } catch (Exception e) {
            loggingService.log(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    private BigDecimal calculateTotalAmount(Set<OrderItem> items) {
        double total = items.stream()
                .mapToDouble(item -> item.getBook().getPrice().doubleValue() * item.getQuantity())
                .sum();
        return BigDecimal.valueOf(total);
    }
}
