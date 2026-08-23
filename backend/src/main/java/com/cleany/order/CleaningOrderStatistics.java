package com.cleany.order;

import java.math.BigDecimal;

public interface CleaningOrderStatistics {

    long getTotalOrders();

    long getOrdersToday();

    long getNewOrders();

    long getActiveOrders();

    long getCompletedOrders();

    long getCancelledOrders();

    BigDecimal getCompletedAmount();
}
