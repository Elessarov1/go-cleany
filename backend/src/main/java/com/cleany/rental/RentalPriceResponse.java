package com.cleany.rental;

import java.math.BigDecimal;

public record RentalPriceResponse(
        BigDecimal baseDailyPrice,
        BigDecimal baseMonthlyPrice,
        BigDecimal monthlyPrice,
        BigDecimal baseAmount,
        BigDecimal discountRate,
        BigDecimal discountAmount,
        boolean longTermDiscountApplied,
        BigDecimal totalPrice,
        String currency,
        Integer rentalMonths,
        int durationDays
) {

    static RentalPriceResponse from(RentalPriceQuote quote) {
        return new RentalPriceResponse(
                quote.baseDailyPrice(),
                quote.baseMonthlyPrice(),
                quote.monthlyPrice(),
                quote.baseAmount(),
                quote.discountRate(),
                quote.discountAmount(),
                quote.longTermDiscountApplied(),
                quote.totalPrice(),
                quote.currency(),
                quote.rentalMonths(),
                quote.durationDays()
        );
    }
}
