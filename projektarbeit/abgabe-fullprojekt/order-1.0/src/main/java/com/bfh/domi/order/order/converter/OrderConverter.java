package com.bfh.domi.order.order.converter;

import com.bfh.domi.order.common.model.Address;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.order.dto.*;
import com.bfh.domi.order.order.model.Order;
import com.bfh.domi.order.order.model.OrderItem;
import com.bfh.domi.order.payment.model.Payment;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OrderConverter implements Converter<Order, SalesOrder> {
    @Override
    public @Nullable SalesOrder convert(Order order) {
        CustomerDto customerDto = convertToCustomerDto(order.getCustomer());
        AddressDto addressDto = convertToAddressDto(order.getOrderAddress());
        PaymentDto paymentDto = convertToPaymentDto(order.getPayment());
        SalesOrderItem[] salesOrderItems = order.getOrderItems().stream()
                .map(this::convertToSalesOrderItem)
                .toArray(SalesOrderItem[]::new);

        return new SalesOrder(
                order.getId(),
                order.getDate(),
                order.getAmount(),
                order.getStatus(),
                customerDto,
                addressDto,
                paymentDto,
                salesOrderItems);
    }

    private CustomerDto convertToCustomerDto(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );
    }

    private AddressDto convertToAddressDto(Address address) {
        return new AddressDto(
                address.getStreet(),
                address.getCity(),
                address.getStateProvince(),
                address.getPostalCode(),
                address.getCountry()
        );
    }

    private PaymentDto convertToPaymentDto(Payment payment) {
        return new PaymentDto(
                payment.getDate(),
                payment.getAmount(),
                payment.getCreditCardNumber(),
                payment.getTransactionId()
        );
    }

    private SalesOrderItem convertToSalesOrderItem(OrderItem orderItem) {
        BookDto bookDto = new BookDto(
                orderItem.getBook().getIsbn(),
                orderItem.getBook().getTitle(),
                orderItem.getBook().getAuthors(),
                orderItem.getBook().getPublisher(),
                orderItem.getBook().getPrice()
        );
        return new SalesOrderItem(bookDto, orderItem.getQuantity());
    }
}
