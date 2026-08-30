package com.cleany.analytics;

import java.util.List;

public record AnalyticsOverviewResponse(
        AnalyticsPeriodResponse period,
        AnalyticsCustomerMetrics customers,
        AnalyticsBusinessHealthMetrics businessHealth,
        AnalyticsRetentionMetrics retention,
        List<AnalyticsTransitionMetric> transitions,
        List<AnalyticsRepeatActionMetric> repeatActions,
        List<AverageCheckMetric> averageChecks,
        List<AcquisitionMetric> acquisition
) {
}
