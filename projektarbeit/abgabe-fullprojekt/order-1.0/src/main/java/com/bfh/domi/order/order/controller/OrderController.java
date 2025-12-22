package com.bfh.domi.order.order.controller;

import com.bfh.domi.order.customer.exception.CustomerNotFoundException;
import com.bfh.domi.order.order.dto.*;
import com.bfh.domi.order.order.exception.BookNotFoundException;
import com.bfh.domi.order.order.exception.OrderAlreadyShippedException;
import com.bfh.domi.order.order.exception.OrderNotFoundException;
import com.bfh.domi.order.order.model.Book;
import com.bfh.domi.order.order.integration.catalog.CatalogClient;
import com.bfh.domi.order.order.model.Order;
import com.bfh.domi.order.order.model.OrderItem;
import com.bfh.domi.order.order.service.OrderService;
import com.bfh.domi.order.payment.exception.PaymentFailedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderService orderService;
    private final CatalogClient catalogClient;
    private final ConversionService conversionService;

    public OrderController(OrderService orderService, CatalogClient catalogClient, ConversionService conversionService) {
        this.orderService = orderService;
        this.catalogClient = catalogClient;
        this.conversionService = conversionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place a new order")
    @ApiResponse(responseCode = "201", description = "Order placed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid order data provided")
    @ApiResponse(responseCode = "422", description = "Customer or Book not found, or payment failed")
    public ResponseEntity<SalesOrder> placeOrder(@Valid @RequestBody PurchaseOrder purchaseOrder) throws BookNotFoundException, CustomerNotFoundException, PaymentFailedException {
        List<OrderItem> orderItems = new ArrayList<>();
        for (PurchaseOrderItem item : purchaseOrder.items()) {
            orderItems.add(createOrderItem(item));
        }
        Order order = orderService.placeOrder(purchaseOrder.customerId(), orderItems);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(location).body(conversionService.convert(order, SalesOrder.class));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search orders by customer ID and year")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Order not found for the given criteria")
    public List<OrderInfo> searchOrders(@RequestParam @NotNull Long customerId, @RequestParam @NotNull @Min(2000) int year) {
        return orderService.searchOrders(customerId, year);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find an order by ID")
    @ApiResponse(responseCode = "200", description = "Order found successfully")
    @ApiResponse(responseCode = "404", description = "Order with the given ID not found")
    public SalesOrder findOrder(@PathVariable @NotNull @Positive Long id) throws OrderNotFoundException {
        return conversionService.convert(orderService.findOrder(id), SalesOrder.class);
    }

    @PatchMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancel an order by ID")
    @ApiResponse(responseCode = "204", description = "Order cancelled successfully")
    @ApiResponse(responseCode = "404", description = "Order with the given ID not found")
    @ApiResponse(responseCode = "409", description = "Order has already been shipped and cannot be cancelled")
    public void cancelOrder(@PathVariable @NotNull @Positive Long id) throws OrderNotFoundException, OrderAlreadyShippedException {
        orderService.cancelOrder(id);
    }

    private OrderItem createOrderItem(PurchaseOrderItem item) throws BookNotFoundException {
        OrderItem orderItem = new OrderItem();
        Book book = catalogClient.findBook(item.isbn());
        orderItem.setBook(book);
        orderItem.setQuantity(item.quantity());
        return orderItem;
    }
}
