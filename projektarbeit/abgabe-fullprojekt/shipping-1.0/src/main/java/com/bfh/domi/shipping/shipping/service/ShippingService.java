package com.bfh.domi.shipping.shipping.service;

import com.bfh.domi.shipping.shipping.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class ShippingService {

    @Value("${bookstore.shipping.processingtime:30000}")
    private long processingTime;
    @Value("${bookstore.shipping.infoqueue}")
    private String shippingInfoQueue;

    private final JmsTemplate jmsTemplate;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final ConcurrentHashMap<Integer, ShippingTask> shippingOrders = new ConcurrentHashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(ShippingService.class);

    public ShippingService(JmsTemplate jmsTemplate, ThreadPoolTaskScheduler taskScheduler) {
        this.jmsTemplate = jmsTemplate;
        this.taskScheduler = taskScheduler;
    }

    @JmsListener(destination = "${bookstore.shipping.orderqueue}")
    public void onShippingOrder(ShippingOrder shippingOrder) {
        LOG.info("Received shipping order: {}", shippingOrder.id());
        shippingOrders.put(shippingOrder.id(), new ShippingTask(shippingOrder, scheduleShippingTask(shippingOrder)));
        sendShippingInfo(new ShippingInfo(shippingOrder.id(), ShippingStatus.PROCESSING));
    }

    @JmsListener(destination = "${bookstore.shipping.cancelqueue}")
    public void onShippingCancel(ShippingCancel shippingCancel) {
        LOG.info("Received shipping cancel: {}", shippingCancel);

        ShippingTask task = shippingOrders.get(shippingCancel.orderId());
        if (task == null) {
            LOG.warn("Order not found for cancellation: {}", shippingCancel.orderId());
            return;
        }

        ScheduledFuture<?> future = task.scheduledFuture();
        if (future == null || !future.cancel(true)) {
            LOG.warn("Failed to cancel shipping task for order id: {}", shippingCancel.orderId());
            return;
        }

        if (shippingOrders.remove(shippingCancel.orderId()) != null)
            sendShippingInfo(new ShippingInfo(shippingCancel.orderId(), ShippingStatus.CANCELED));
    }

    public void sendShippingInfo(ShippingInfo shippingInfo) {
        LOG.info("Sending shipping info: {}", shippingInfo);
        jmsTemplate.convertAndSend(shippingInfoQueue, shippingInfo);
    }

    private ScheduledFuture<?> scheduleShippingTask(ShippingOrder shippingOrder) {
        return taskScheduler.schedule(() -> {
            if (shippingOrders.remove(shippingOrder.id()) != null)
                sendShippingInfo(new ShippingInfo(shippingOrder.id(), ShippingStatus.SHIPPED));
        }, Instant.now().plusMillis(processingTime));
    }
}
