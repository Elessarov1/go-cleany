package com.cleany.configuration;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

/** Stable wire representation: decimal amounts are strings, never binary floating point. */
public record Money(String amount, String currency) {
    public Money {
        Objects.requireNonNull(amount, "amount");
        currency = Objects.requireNonNull(currency, "currency").trim().toUpperCase(Locale.ROOT);
        if (!currency.matches("[A-Z]{3}")) throw new IllegalArgumentException("currency must be ISO 4217");
        new BigDecimal(amount);
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(Objects.requireNonNull(amount, "amount").toPlainString(), currency);
    }
}
