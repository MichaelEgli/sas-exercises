package com.bfh.domi.order.order.converter;

import com.bfh.domi.order.order.dto.*;
import com.bfh.domi.order.order.model.Order;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShippingOrderConverter implements Converter<Order, ShippingOrder> {
    @Override
    public @Nullable ShippingOrder convert(Order order) {
        CustomerDto customerDto = new CustomerDto(
                order.getCustomer().getId(),
                order.getCustomer().getFirstName(),
                order.getCustomer().getLastName(),
                order.getCustomer().getEmail()
        );
        AddressDto addressDto = new AddressDto(
                order.getOrderAddress().getStreet(),
                order.getOrderAddress().getCity(),
                order.getOrderAddress().getStateProvince(),
                order.getOrderAddress().getPostalCode(),
                order.getOrderAddress().getCountry()
        );
        List<Item> items = order.getOrderItems().stream()
                .map(orderItem -> {
                    BookDto bookDto = new BookDto(
                            orderItem.getBook().getIsbn(),
                            orderItem.getBook().getTitle(),
                            orderItem.getBook().getAuthors(),
                            orderItem.getBook().getPublisher(),
                            orderItem.getBook().getPrice()
                    );
                    return new Item(bookDto, orderItem.getQuantity());
                })
                .collect(Collectors.toList());

        return new ShippingOrder(
                order.getId().intValue(),
                customerDto,
                addressDto,
                items
        );
    }
}
