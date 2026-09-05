package com.cleany.transfer;

import java.math.BigDecimal;

public record TransferQuoteResponse(
        BigDecimal baseAmount,
        BigDecimal discountAmount,
        BigDecimal payableAmount,
        String currency,
        TransferBenefitType appliedBenefit
) {

    static TransferQuoteResponse from(TransferPriceQuote quote) {
        return new TransferQuoteResponse(
                quote.baseAmount(),
                quote.discountAmount(),
                quote.payableAmount(),
                quote.currency(),
                quote.appliedBenefit()
        );
    }
}
