package com.bfh.domi.shipping.shipping.dto;

import java.util.concurrent.ScheduledFuture;

public record ShippingTask(
    ShippingOrder shippingOrder,
    ScheduledFuture<?> scheduledFuture
) {
}
