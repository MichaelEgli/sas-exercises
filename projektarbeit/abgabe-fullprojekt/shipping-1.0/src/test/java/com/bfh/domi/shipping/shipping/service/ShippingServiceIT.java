package com.bfh.domi.shipping.shipping.service;

import com.bfh.domi.shipping.TestcontainersConfiguration;
import com.bfh.domi.shipping.shipping.dto.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ShippingServiceIT {

    @Autowired
    private ShippingService shippingService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Value("${bookstore.shipping.orderqueue}")
    private String shippingOrderQueue;
    @Value("${bookstore.shipping.infoqueue}")
    private String shippingInfoQueue;
    @Value("${bookstore.shipping.cancelqueue}")
    private String shippingCancelQueue;
    @Value("${bookstore.shipping.processingtime}")
    private long processingTime;

    Customer CUSTOMER = new Customer(
            10001,
            "John",
            "Doe",
            "john.doe@example.net"
    );

    Address ADDRESS = new Address(
            "Main Street 1",
            "12345",
            "Sample City",
            "43341",
            "Country"
    );

    @BeforeEach
    void drainShippingInfoQueue() {
        while (true) {
            var shippingInfo = (ShippingInfo) jmsTemplate.receiveAndConvert(shippingInfoQueue);
            if (shippingInfo == null) break;
        }
    }

    @Test
    void sendShippingOrder() {
        ShippingOrder SHIPPING_ORDER = getShippingOrder(CUSTOMER, ADDRESS);

        jmsTemplate.convertAndSend(shippingOrderQueue, SHIPPING_ORDER);

        var firstShippingInfo = (ShippingInfo) jmsTemplate.receiveAndConvert(shippingInfoQueue);
        assertThat(firstShippingInfo).isEqualTo(new ShippingInfo(SHIPPING_ORDER.id(), ShippingStatus.PROCESSING));

        var secondShippingInfo = (ShippingInfo) jmsTemplate.receiveAndConvert(shippingInfoQueue);
        assertThat(secondShippingInfo).isEqualTo(new ShippingInfo(SHIPPING_ORDER.id(), ShippingStatus.SHIPPED));
    }

    @Test
    void sendShippingCancel_whenOrderInProcessing() throws InterruptedException {
        ShippingOrder SHIPPING_ORDER = getShippingOrder(CUSTOMER, ADDRESS);
        jmsTemplate.convertAndSend(shippingOrderQueue, SHIPPING_ORDER);

        Thread.sleep(processingTime - 50);

        ShippingCancel SHIPPING_CANCEL = new ShippingCancel(SHIPPING_ORDER.id());
        jmsTemplate.convertAndSend(shippingCancelQueue, SHIPPING_CANCEL);

        var firstShippingInfo = (ShippingInfo) jmsTemplate.receiveAndConvert(shippingInfoQueue);
        assertThat(firstShippingInfo).isEqualTo(new ShippingInfo(SHIPPING_ORDER.id(), ShippingStatus.PROCESSING));

        var secondShippingInfo = (ShippingInfo) jmsTemplate.receiveAndConvert(shippingInfoQueue);
        assertThat(secondShippingInfo).isEqualTo(new ShippingInfo(SHIPPING_ORDER.id(), ShippingStatus.CANCELED));
    }

    @Test
    void sendShippingCancel_whenOrderAlreadyShipped() throws InterruptedException {
        ShippingOrder SHIPPING_ORDER = getShippingOrder(CUSTOMER, ADDRESS);
        jmsTemplate.convertAndSend(shippingOrderQueue, SHIPPING_ORDER);

        Thread.sleep(processingTime + 50);

        ShippingCancel SHIPPING_CANCEL = new ShippingCancel(SHIPPING_ORDER.id());
        jmsTemplate.convertAndSend(shippingCancelQueue, SHIPPING_CANCEL);

        var firstShippingInfo = (ShippingInfo) jmsTemplate.receiveAndConvert(shippingInfoQueue);
        assertThat(firstShippingInfo).isEqualTo(new ShippingInfo(SHIPPING_ORDER.id(), ShippingStatus.PROCESSING));

        var secondShippingInfo = (ShippingInfo) jmsTemplate.receiveAndConvert(shippingInfoQueue);
        assertThat(secondShippingInfo).isEqualTo(new ShippingInfo(SHIPPING_ORDER.id(), ShippingStatus.SHIPPED));

        var thirdShippingInfo = jmsTemplate.receiveAndConvert(shippingInfoQueue);
        assertThat(thirdShippingInfo).isNull();
    }

    @Test
    void sendShippingCancel_whenOrderNotFound() {
        ShippingCancel SHIPPING_CANCEL = new ShippingCancel(99999);
        jmsTemplate.convertAndSend(shippingCancelQueue, SHIPPING_CANCEL);

        var received = jmsTemplate.receiveAndConvert(shippingInfoQueue);
        assertThat(received).isNull();
    }

    @Test
    void receiveShippingInfo() {
        ShippingInfo SHIPPING_INFO = new ShippingInfo(
                20001,
                ShippingStatus.SHIPPED
        );

        shippingService.sendShippingInfo(SHIPPING_INFO);

        var received = (ShippingInfo) jmsTemplate.receiveAndConvert(shippingInfoQueue);
        assertThat(received).isEqualTo(SHIPPING_INFO);
    }

    private static @NotNull ShippingOrder getShippingOrder(Customer CUSTOMER, Address ADDRESS) {
        Book BOOK1 = new Book(
                "ISBN-001",
                "Sample Book 1",
                "Author A",
                "Publisher X"
        );

        Book BOOK2 = new Book(
                "ISBN-002",
                "Sample Book 2",
                "Author B",
                "Publisher Y"
        );

        List<Item> ITEMS = new ArrayList<Item>();
        ITEMS.add(new Item(BOOK1, 2));
        ITEMS.add(new Item(BOOK2, 1));

        return new ShippingOrder(
                20001,
                CUSTOMER,
                ADDRESS,
                ITEMS
        );
    }
}