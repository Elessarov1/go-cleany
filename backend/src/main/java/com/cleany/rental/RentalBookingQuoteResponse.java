package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalBookingQuoteResponse(
        RentalBookingPropertyResponse property,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int durationDays,
        BigDecimal baseDailyPrice,
        BigDecimal baseAmount,
        boolean longTermDiscountApplied,
        BigDecimal discountRate,
        BigDecimal discountAmount,
        BigDecimal totalPrice,
        String currency
) {

    static RentalBookingQuoteResponse from(
            RentalProperty property,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            RentalPriceQuote quote
    ) {
        return new RentalBookingQuoteResponse(
                RentalBookingPropertyResponse.from(property),
                checkInDate,
                checkOutDate,
                quote.durationDays(),
                quote.baseDailyPrice(),
                quote.baseAmount(),
                quote.longTermDiscountApplied(),
                quote.discountRate(),
                quote.discountAmount(),
                quote.totalPrice(),
                quote.currency()
        );
    }
}
