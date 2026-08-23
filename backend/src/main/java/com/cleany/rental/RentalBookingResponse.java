package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record RentalBookingResponse(
        long id,
        RentalBookingPropertyResponse property,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int durationDays,
        String customerName,
        String phone,
        int guests,
        String comment,
        BigDecimal baseDailyPriceSnapshot,
        BigDecimal longTermDiscountRateSnapshot,
        BigDecimal discountAmount,
        BigDecimal totalPrice,
        String currency,
        RentalBookingStatus status,
        Instant createdAt,
        Instant cancelledAt,
        String cancellationReason,
        Instant completedAt
) {

    static RentalBookingResponse from(RentalBooking booking) {
        return new RentalBookingResponse(
                booking.getId(),
                RentalBookingPropertyResponse.from(booking.getProperty()),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getDurationDays(),
                booking.getCustomerName(),
                booking.getPhone(),
                booking.getGuests(),
                booking.getComment(),
                booking.getBaseDailyPriceSnapshot(),
                booking.getLongTermDiscountRateSnapshot(),
                booking.getDiscountAmount(),
                booking.getTotalPrice(),
                booking.getCurrency(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getCancelledAt(),
                booking.getCancellationReason(),
                booking.getCompletedAt()
        );
    }
}
