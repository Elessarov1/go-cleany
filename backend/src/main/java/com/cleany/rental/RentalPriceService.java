package com.cleany.rental;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalPriceService {

    private static final int MONEY_SCALE = 2;

    private final RentalProperties properties;

    public RentalPriceQuote calculate(RentalProperty property, ResolvedRentalTerm term) {
        if (property.getBaseDailyPrice() == null || property.getCurrency() == null) {
            throw new RentalPropertyNotAvailableException(property.getId());
        }
        BigDecimal dailyPrice = property.getBaseDailyPrice().setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        return switch (term.termType()) {
            case DATE_RANGE -> dateRange(dailyPrice, property.getCurrency(), term);
            case MONTHLY -> monthly(dailyPrice, property.getCurrency(), term);
        };
    }

    private static RentalPriceQuote dateRange(
            BigDecimal dailyPrice,
            String currency,
            ResolvedRentalTerm term
    ) {
        BigDecimal baseAmount = dailyPrice
                .multiply(BigDecimal.valueOf(term.durationDays()))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        return new RentalPriceQuote(
                RentalTermType.DATE_RANGE,
                null,
                term.durationDays(),
                dailyPrice,
                null,
                baseAmount,
                false,
                BigDecimal.ZERO,
                BigDecimal.ZERO.setScale(MONEY_SCALE),
                baseAmount,
                currency
        );
    }

    private RentalPriceQuote monthly(
            BigDecimal dailyPrice,
            String currency,
            ResolvedRentalTerm term
    ) {
        BigDecimal discountRate = properties.longTermDiscountRate();
        BigDecimal monthlyBase = dailyPrice
                .multiply(BigDecimal.valueOf(30))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal monthlyPrice = monthlyBase
                .multiply(BigDecimal.ONE.subtract(discountRate))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal baseAmount = monthlyBase
                .multiply(BigDecimal.valueOf(term.rentalMonths()))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal totalPrice = monthlyPrice
                .multiply(BigDecimal.valueOf(term.rentalMonths()))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal discountAmount = baseAmount
                .subtract(totalPrice)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        return new RentalPriceQuote(
                RentalTermType.MONTHLY,
                term.rentalMonths(),
                term.durationDays(),
                dailyPrice,
                monthlyPrice,
                baseAmount,
                discountRate.signum() > 0,
                discountRate,
                discountAmount,
                totalPrice,
                currency
        );
    }
}
