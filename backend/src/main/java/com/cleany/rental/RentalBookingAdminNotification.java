package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalBookingAdminNotification(
        long bookingId,
        String apartment,
        String customerName,
        String phone,
        RentalTermType termType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer rentalMonths,
        int durationDays,
        BigDecimal dailyPrice,
        BigDecimal monthlyPrice,
        BigDecimal totalPrice,
        String currency
) {
}
