package com.cleany.analytics;

import java.math.BigDecimal;

public record AnalyticsCohortMetric(
        long cohortCustomers,
        long convertedCustomers,
        BigDecimal rate
) {
}
