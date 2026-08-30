package com.cleany.analytics;

import java.math.BigDecimal;

import com.cleany.catalog.PlatformService;

public record AnalyticsTransitionMetric(
        PlatformService fromService,
        PlatformService toService,
        long cohortCustomers,
        long convertedCustomers,
        BigDecimal conversionRate
) {
}
