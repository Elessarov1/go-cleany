package com.cleany.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.cleany.finance.CustomerDiscountType;

public record CleaningOrderResponse(
        long id,
        long telegramUserId,
        String telegramUsername,
        String customerName,
        String phone,
        ServiceArea area,
        String address,
        ApartmentType apartmentType,
        boolean duplex,
        CleaningType cleaningType,
        BigDecimal price,
        BigDecimal basePrice,
        BigDecimal customerDiscount,
        BigDecimal finalCustomerPrice,
        CustomerDiscountType customerDiscountType,
        String currency,
        LocalDate requestedDate,
        String customerComment,
        String cleanerComment,
        Long cleanerTelegramUserId,
        CleaningOrderStatus status,
        Instant createdAt,
        Instant acceptedAt,
        Instant completedAt
) {

    public static CleaningOrderResponse from(CleaningOrder order) {
        return new CleaningOrderResponse(
                order.getId(),
                order.getTelegramUserId(),
                order.getTelegramUsername(),
                order.getCustomerName(),
                order.getPhone(),
                order.getArea(),
                order.getAddress(),
                order.getApartmentType(),
                order.isDuplex(),
                order.getCleaningType(),
                order.getPrice(),
                order.getBasePrice(),
                order.getCustomerDiscount(),
                order.getFinalCustomerPrice(),
                order.getCustomerDiscountType(),
                order.getCurrency(),
                order.getRequestedDate(),
                order.getCustomerComment(),
                order.getCleanerComment(),
                order.getCleanerTelegramUserId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getAcceptedAt(),
                order.getCompletedAt()
        );
    }
}
