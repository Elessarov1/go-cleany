package com.cleany.analytics;

import java.util.List;

public record AnalyticsRentalSearchMetrics(
        AnalyticsRentalSearchFunnelMetric total,
        List<AnalyticsRentalSearchModeMetric> byMode
) {
}
