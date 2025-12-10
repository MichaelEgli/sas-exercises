package com.bfh.domi.order.order.service;

import com.bfh.domi.order.common.client.ShippingClient;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final CustomerService customerService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShippingClient shippingClient;

    public OrderService(CustomerService customerService, PaymentService paymentService, OrderRepository orderRepository, OrderItemRepository orderItemRepository, ShippingClient shippingClient) {
        this.customerService = customerService;
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.shippingClient = shippingClient;
    }

    public Order findOrder(long id) throws OrderNotFoundException {
        return orderRepository.findOrderById(id).orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    public List<OrderInfo> searchOrders(long customerId, int year) {

        LocalDateTime startYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endYear = LocalDateTime.of(year, 12, 31, 23, 59);

        return orderRepository.findAllByCustomerIdAndDateBetween(customerId, startYear, endYear);
    }

    public Order placeOrder(long customerId, List<OrderItem> items) throws CustomerNotFoundException, PaymentFailedException {

        Customer customer = customerService.findCustomer(customerId);
        Set<OrderItem> orderItems = new HashSet<>(items);

        // Create and save the order
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderAddress(customer.getAddress());
        order.setDate(LocalDateTime.now());
        order.setAmount(calculateTotalAmount(orderItems));
        order.setPayment(paymentService.makePayment(customer, order.getAmount()));
        order.setStatus(OrderStatus.ACCEPTED);
        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.saveAndFlush(order);

        // Set the order reference in each order item and save them
        orderItems.forEach(oi -> {
            oi.setOrder(order);
            orderItemRepository.saveAndFlush(oi);
        });

        shippingClient.sendShippingOrder(convertToShippingOrder(savedOrder));

        return savedOrder;
    }

    public void cancelOrder(long id) throws OrderNotFoundException, OrderAlreadyShippedException {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        if (order.get().getStatus() == OrderStatus.SHIPPED) {
            throw new OrderAlreadyShippedException("Order with id: " + id + " has already been shipped and cannot be canceled.");
        }
        order.get().setStatus(OrderStatus.CANCELED);
        orderRepository.saveAndFlush(order.get());
    }

    private BigDecimal calculateTotalAmount(Set<OrderItem> items) {
        double total = items.stream()
                .mapToDouble(item -> item.getBook().getPrice().doubleValue() * item.getQuantity())
                .sum();
        return BigDecimal.valueOf(total);
    }

    // Konvertierungsmethode in OrderService
    private ShippingOrder convertToShippingOrder(Order order) {
        // Convert customer entity to DTO
        com.bfh.domi.order.order.dto.Customer customerDto = new com.bfh.domi.order.order.dto.Customer(
                order.getCustomer().getId().intValue(),
                order.getCustomer().getFirstName(),
                order.getCustomer().getLastName(),
                order.getCustomer().getEmail()
        );

        // Convert address entity to DTO
        Address addressDto = new Address(
                order.getOrderAddress().getStreet(),
                order.getOrderAddress().getCity(),
                order.getOrderAddress().getStateProvince(),
                order.getOrderAddress().getPostalCode(),
                order.getOrderAddress().getCountry()
        );

        // Convert order items to DTOs
        List<Item> items = order.getOrderItems().stream()
                .map(orderItem -> {
                    Book bookDto = new Book(
                            orderItem.getBook().getIsbn(),
                            orderItem.getBook().getTitle(),
                            orderItem.getBook().getAuthors(),
                            orderItem.getBook().getPublisher()
                    );
                    return new Item(bookDto, orderItem.getQuantity());
                })
                .collect(Collectors.toList());

        // Create and return ShippingOrder
        return new ShippingOrder(
                order.getId().intValue(),
                customerDto,
                addressDto,
                items
        );
    }
}
