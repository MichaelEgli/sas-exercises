package com.bfh.domi.order.dto;

import java.math.BigDecimal;

public interface OrderStatistics {
    Integer getYear();

    Long getCustomerId();

    String getCustomerName();

    BigDecimal getTotalOrderPrice();

    Long getItemCount();

    Double getAverageItemAmount();
}

