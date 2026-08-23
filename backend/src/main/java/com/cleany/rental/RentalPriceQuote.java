package com.cleany.rental;

import java.math.BigDecimal;
import java.util.Objects;

public record RentalPriceQuote(
        RentalTermType termType,
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

    public RentalPriceQuote {
        termType = Objects.requireNonNull(termType, "termType");
        if (durationDays <= 0) {
            throw new IllegalArgumentException("durationDays must be positive");
        }
        baseDailyPrice = requireMoney(baseDailyPrice, "baseDailyPrice");
        if (termType == RentalTermType.DATE_RANGE) {
            if (rentalMonths != null || monthlyPrice != null) {
                throw new IllegalArgumentException(
                        "DATE_RANGE price must not contain monthly values"
                );
            }
        } else {
            if (rentalMonths == null || rentalMonths <= 0) {
                throw new IllegalArgumentException("MONTHLY price requires rentalMonths");
            }
            monthlyPrice = requireMoney(monthlyPrice, "monthlyPrice");
        }
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
