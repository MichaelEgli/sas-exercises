package com.bfh.domi.order.common.client;

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

    private static final Logger logger = LoggerFactory.getLogger(ShippingClient.class);

    private final JmsTemplate jmsTemplate;

    private final OrderRepository orderRepository;

    @Value("${shipping.order.queue}")
    private String shippingOrderQueue;
    @Value("${shipping.cancel.queue}")
    private String shippingCancelQueue;
    private final List<ShippingOrder> shippingOrders;

    public ShippingClient(JmsTemplate jmsTemplate, OrderRepository orderRepository, List<ShippingOrder> shippingOrders) {
        this.jmsTemplate = jmsTemplate;
        this.orderRepository = orderRepository;
        this.shippingOrders = shippingOrders;
    }

    public void sendShippingOrder(ShippingOrder shippingOrder) {
        logger.info("Shipping Order to send {}", shippingOrder);

        shippingOrders.add(shippingOrder);
        jmsTemplate.convertAndSend(shippingOrderQueue, shippingOrder);
    }

    public void sendShippingCancel(ShippingInfo shippingInfo) {
        logger.info("Shipping Order to cancel {}", shippingInfo);

        jmsTemplate.convertAndSend(shippingCancelQueue, shippingInfo);
    }

    @Transactional
    @JmsListener(destination = "${shipping.info.queue}")
    public void onShippingInfo(ShippingInfo shippingInfo) throws OrderNotFoundException {

        int orderId = shippingInfo.orderId();
        ShippingStatus status = shippingInfo.status();

        logger.info("Reveived Shipping OrderId : {}", orderId);
        logger.info("Reveived Shipping Status : {}", status);

        int updated = orderRepository.updateOrderStatus((long) orderId, convertFromShippingStatus(status));
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
