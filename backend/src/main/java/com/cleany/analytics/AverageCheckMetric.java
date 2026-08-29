package com.cleany.analytics;

import java.math.BigDecimal;

import com.cleany.catalog.PlatformService;

public record AverageCheckMetric(
        PlatformService service,
        String currency,
        BigDecimal amount,
        long completedTransactions
) {
}
