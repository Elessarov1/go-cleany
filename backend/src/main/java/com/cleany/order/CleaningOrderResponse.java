package com.cleany.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.cleany.finance.CustomerDiscountType;

public record CleaningOrderResponse(
        long id,
        long communicationIdentityId,
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
        Instant completedAt,
        CustomerCleaningReportResponse report
) {

    public static CleaningOrderResponse from(CleaningOrder order) {
        return new CleaningOrderResponse(
                order.getId(),
                order.getCommunicationIdentityId(),
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
                order.getCompletedAt(),
                null
        );
    }

    public static CleaningOrderResponse from(
            CleaningOrder order,
            CustomerCleaningReportResponse report
    ) {
        CleaningOrderResponse base = from(order);
        return new CleaningOrderResponse(
                base.id(), base.communicationIdentityId(), base.customerName(), base.phone(),
                base.area(), base.address(), base.apartmentType(), base.duplex(), base.cleaningType(),
                base.price(), base.basePrice(), base.customerDiscount(), base.finalCustomerPrice(),
                base.customerDiscountType(), base.currency(), base.requestedDate(), base.customerComment(),
                base.cleanerComment(), base.cleanerTelegramUserId(), base.status(), base.createdAt(),
                base.acceptedAt(), base.completedAt(), report
        );
    }
}
