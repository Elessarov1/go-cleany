package com.cleany.admin;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderStatus;
import com.cleany.order.CleaningType;
import com.cleany.order.ServiceArea;

public record AdminOrderSummaryResponse(
        long id,
        String customerName,
        ServiceArea area,
        CleaningType cleaningType,
        LocalDate requestedDate,
        BigDecimal price,
        String currency,
        CleaningOrderStatus status,
        Long cleanerTelegramUserId,
        Instant createdAt
) {

    public static AdminOrderSummaryResponse from(CleaningOrder order) {
        return new AdminOrderSummaryResponse(
                order.getId(),
                order.getCustomerName(),
                order.getArea(),
                order.getCleaningType(),
                order.getRequestedDate(),
                order.getPrice(),
                order.getCurrency(),
                order.getStatus(),
                order.getCleanerTelegramUserId(),
                order.getCreatedAt()
        );
    }
}
