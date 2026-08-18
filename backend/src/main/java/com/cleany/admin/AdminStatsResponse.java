package com.cleany.admin;

import java.math.BigDecimal;

public record AdminStatsResponse(
        long totalOrders,
        long ordersToday,
        long newOrders,
        long activeOrders,
        long completedOrders,
        long cancelledOrders,
        BigDecimal completedAmount,
        String currency
) {
}
