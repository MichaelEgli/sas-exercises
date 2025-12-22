package com.bfh.domi.order.order.integration.shipping;

import com.bfh.domi.order.order.dto.ShippingCancel;
import com.bfh.domi.order.order.dto.ShippingInfo;
import com.bfh.domi.order.order.dto.ShippingOrder;
import com.bfh.domi.order.order.dto.ShippingStatus;
import com.bfh.domi.order.order.exception.OrderNotFoundException;
import com.bfh.domi.order.order.model.OrderStatus;
import com.bfh.domi.order.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ShippingClient {

    private static final Logger LOG = LoggerFactory.getLogger(ShippingClient.class);

    private final JmsTemplate jmsTemplate;

    private final OrderRepository orderRepository;

    @Value("${bookstore.shipping.orderqueue}")
    private String shippingOrderQueue;
    @Value("${bookstore.shipping.cancelqueue}")
    private String shippingCancelQueue;
    private final List<ShippingOrder> shippingOrders;

    public ShippingClient(JmsTemplate jmsTemplate, OrderRepository orderRepository, List<ShippingOrder> shippingOrders) {
        this.jmsTemplate = jmsTemplate;
        this.orderRepository = orderRepository;
        this.shippingOrders = shippingOrders;
    }

    public void sendShippingOrder(ShippingOrder shippingOrder) {
        LOG.info("Sending shipping order: {}", shippingOrder.id());
        shippingOrders.add(shippingOrder);
        jmsTemplate.convertAndSend(shippingOrderQueue, shippingOrder);
    }

    public void sendShippingCancel(ShippingCancel shippingCancel) {
        LOG.info("Sending shipping cancel: {}", shippingCancel);
        jmsTemplate.convertAndSend(shippingCancelQueue, shippingCancel);
    }

    @Transactional
    @JmsListener(destination = "${bookstore.shipping.infoqueue}")
    public void onShippingInfo(ShippingInfo shippingInfo) throws OrderNotFoundException {
        LOG.info("Received shipping info: {}", shippingInfo);
        int orderId = shippingInfo.orderId();
        int updated = orderRepository.updateOrderStatus((long) orderId, convertFromShippingStatus(shippingInfo.status()));
        if (updated == 0) throw new OrderNotFoundException("Order not found with id: " + orderId);
    }

    private OrderStatus convertFromShippingStatus(ShippingStatus status) {
        return switch (status) {
            case PROCESSING -> OrderStatus.PROCESSING;
            case SHIPPED -> OrderStatus.SHIPPED;
            case CANCELED -> OrderStatus.CANCELED;
        };
    }
}
