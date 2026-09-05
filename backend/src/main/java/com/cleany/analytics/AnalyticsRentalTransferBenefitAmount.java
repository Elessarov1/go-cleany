package com.cleany.analytics;

import java.math.BigDecimal;

public record AnalyticsRentalTransferBenefitAmount(
        String currency,
        long completedTransfers,
        BigDecimal baseAmount,
        BigDecimal discountAmount,
        BigDecimal payableAmount
) {
}
