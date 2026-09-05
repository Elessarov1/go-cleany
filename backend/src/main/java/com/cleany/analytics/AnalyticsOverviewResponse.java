package com.cleany.analytics;

import java.util.List;

public record AnalyticsOverviewResponse(
        AnalyticsPeriodResponse period,
        AnalyticsCustomerMetrics customers,
        AnalyticsBusinessHealthMetrics businessHealth,
        AnalyticsRetentionMetrics retention,
        List<AnalyticsTransitionMetric> transitions,
        AnalyticsRentalTransferMetrics rentalToTransfer,
        AnalyticsRentalTransferBenefitMetrics rentalTransferBenefit,
        List<AnalyticsRepeatActionMetric> repeatActions,
        List<AnalyticsReminderMetric> reminders,
        List<AverageCheckMetric> averageChecks,
        List<AcquisitionMetric> acquisition
) {
}
