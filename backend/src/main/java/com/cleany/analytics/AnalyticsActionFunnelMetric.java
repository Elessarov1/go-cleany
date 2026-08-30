package com.cleany.analytics;

import java.math.BigDecimal;

public record AnalyticsActionFunnelMetric(
        long shownSources,
        long startedSources,
        long createdSources,
        long completedSources,
        BigDecimal startRate,
        BigDecimal creationRate,
        BigDecimal completionRate,
        BigDecimal medianHoursToCreation
) {
}
