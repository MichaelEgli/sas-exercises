package com.bfh.domi.order.order.converter;

import com.bfh.domi.order.order.dto.ShippingCancel;
import com.bfh.domi.order.order.model.Order;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OrderToShippingCancelConverter implements Converter<Order, ShippingCancel> {
    @Override
    public @Nullable ShippingCancel convert(Order order) {
        return new ShippingCancel(order.getId().intValue());
    }
}
