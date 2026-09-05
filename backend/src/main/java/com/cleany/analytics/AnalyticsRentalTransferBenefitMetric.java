package com.cleany.analytics;

import java.util.List;

public record AnalyticsRentalTransferBenefitMetric(
        AnalyticsActionFunnelMetric funnel,
        List<AnalyticsRentalTransferBenefitAmount> completedAmounts
) {
}
