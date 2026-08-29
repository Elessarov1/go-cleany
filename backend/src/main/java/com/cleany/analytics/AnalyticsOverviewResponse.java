package com.cleany.analytics;

import java.util.List;

public record AnalyticsOverviewResponse(
        AnalyticsPeriodResponse period,
        AnalyticsCustomerMetrics customers,
        List<AverageCheckMetric> averageChecks,
        List<AcquisitionMetric> acquisition
) {
}
