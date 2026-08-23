package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalBookingQuoteResponse(
        RentalBookingPropertyResponse property,
        RentalTermType termType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer rentalMonths,
        int durationDays,
        BigDecimal baseDailyPrice,
        BigDecimal monthlyPrice,
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
                quote.termType(),
                checkInDate,
                checkOutDate,
                quote.rentalMonths(),
                quote.durationDays(),
                quote.baseDailyPrice(),
                quote.monthlyPrice(),
                quote.baseAmount(),
                quote.longTermDiscountApplied(),
                quote.discountRate(),
                quote.discountAmount(),
                quote.totalPrice(),
                quote.currency()
        );
    }
}
