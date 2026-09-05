package com.cleany.transfer;

import java.math.BigDecimal;
import java.util.Objects;

public record TransferPriceQuote(
        BigDecimal baseAmount,
        BigDecimal discountAmount,
        BigDecimal payableAmount,
        String currency,
        TransferBenefitType appliedBenefit,
        BigDecimal benefitRate
) {

    public TransferPriceQuote {
        Objects.requireNonNull(baseAmount, "baseAmount");
        Objects.requireNonNull(discountAmount, "discountAmount");
        Objects.requireNonNull(payableAmount, "payableAmount");
        Objects.requireNonNull(currency, "currency");
    }

    public static TransferPriceQuote standard(TransferPrice price) {
        BigDecimal amount = price.getAmount();
        return new TransferPriceQuote(
                amount,
                BigDecimal.ZERO.setScale(amount.scale()),
                amount,
                price.getCurrency(),
                null,
                null
        );
    }
}
