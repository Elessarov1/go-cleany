package com.cleany.analytics;

import com.cleany.rental.RentalSearchMode;

public record AnalyticsRentalSearchModeMetric(
        RentalSearchMode mode,
        AnalyticsRentalSearchFunnelMetric funnel
) {
}
