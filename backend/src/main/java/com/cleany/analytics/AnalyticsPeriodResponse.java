package com.cleany.analytics;

import java.time.LocalDate;

public record AnalyticsPeriodResponse(
        LocalDate from,
        LocalDate to,
        AnalyticsServiceDimension service
) {
}
