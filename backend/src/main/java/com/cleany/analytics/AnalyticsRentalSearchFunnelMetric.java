package com.cleany.analytics;

import java.math.BigDecimal;

public record AnalyticsRentalSearchFunnelMetric(
        long searchExecutions,
        long zeroResultSearches,
        long openedSearches,
        long createdBookingSearches,
        long completedBookingSearches,
        long conflictSearches,
        BigDecimal zeroResultRate,
        BigDecimal openRate,
        BigDecimal creationRate,
        BigDecimal completionRate,
        BigDecimal conflictRate,
        BigDecimal medianApiDurationMs,
        BigDecimal medianFirstCardDurationMs,
        BigDecimal medianHoursToBooking
) {
}
