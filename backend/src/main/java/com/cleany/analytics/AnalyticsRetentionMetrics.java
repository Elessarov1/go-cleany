package com.cleany.analytics;

import java.math.BigDecimal;

public record AnalyticsRetentionMetrics(
        AnalyticsCohortMetric repeat30Days,
        AnalyticsCohortMetric repeat90Days,
        AnalyticsCohortMetric secondOrderConversion,
        BigDecimal medianDaysToSecondTask
) {
}
