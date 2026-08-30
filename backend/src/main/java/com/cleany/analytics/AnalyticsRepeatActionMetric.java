package com.cleany.analytics;

import java.math.BigDecimal;

import com.cleany.catalog.PlatformService;

public record AnalyticsRepeatActionMetric(
        PlatformService service,
        long shownSources,
        long startedSources,
        long createdRepeatSources,
        long completedRepeatSources,
        BigDecimal startRate,
        BigDecimal completionRate,
        BigDecimal medianHoursToRepeat
) {
}
