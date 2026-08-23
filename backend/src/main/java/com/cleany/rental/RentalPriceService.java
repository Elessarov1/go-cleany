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

    public RentalPriceQuote calculate(RentalProperty property, int durationDays) {
        if (property.getBaseDailyPrice() == null || property.getCurrency() == null) {
            throw new RentalPropertyNotAvailableException(property.getId());
        }
        if (durationDays <= 0) {
            throw new InvalidRentalDateRangeException();
        }

        BigDecimal dailyPrice = property.getBaseDailyPrice().setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal baseAmount = dailyPrice
                .multiply(BigDecimal.valueOf(durationDays))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        boolean longTerm = durationDays >= properties.longTermMinDays();
        BigDecimal discountRate = longTerm
                ? properties.longTermDiscountRate()
                : BigDecimal.ZERO;
        BigDecimal discountAmount = baseAmount
                .multiply(discountRate)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal totalPrice = baseAmount.subtract(discountAmount)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        return new RentalPriceQuote(
                durationDays,
                dailyPrice,
                baseAmount,
                longTerm,
                discountRate,
                discountAmount,
                totalPrice,
                property.getCurrency()
        );
    }
}
