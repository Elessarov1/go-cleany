package com.cleany.rental;

import java.math.BigDecimal;
import java.util.Objects;

record RentalPriceInput(
        BigDecimal baseDailyPrice,
        String currency
) {

    RentalPriceInput {
        baseDailyPrice = Objects.requireNonNull(baseDailyPrice, "baseDailyPrice");
        currency = Objects.requireNonNull(currency, "currency");
    }

    static RentalPriceInput from(RentalProperty property) {
        return new RentalPriceInput(property.getBaseDailyPrice(), property.getCurrency());
    }
}
