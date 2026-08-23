package com.cleany.rental;

import java.math.BigDecimal;
import java.util.Objects;

public record RentalPriceQuote(
        int durationDays,
        BigDecimal baseDailyPrice,
        BigDecimal baseAmount,
        boolean longTermDiscountApplied,
        BigDecimal discountRate,
        BigDecimal discountAmount,
        BigDecimal totalPrice,
        String currency
) {

    public RentalPriceQuote {
        if (durationDays <= 0) {
            throw new IllegalArgumentException("durationDays must be positive");
        }
        baseDailyPrice = requireMoney(baseDailyPrice, "baseDailyPrice");
        baseAmount = requireMoney(baseAmount, "baseAmount");
        discountRate = Objects.requireNonNull(discountRate, "discountRate");
        discountAmount = requireMoney(discountAmount, "discountAmount");
        totalPrice = requireMoney(totalPrice, "totalPrice");
        currency = Objects.requireNonNull(currency, "currency");
    }

    private static BigDecimal requireMoney(BigDecimal value, String name) {
        BigDecimal required = Objects.requireNonNull(value, name);
        if (required.signum() < 0) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
        return required;
    }
}
