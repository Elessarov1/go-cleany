package com.cleany.analytics;

import java.util.List;

public record AnalyticsRentalTransferBenefitMetrics(
        AnalyticsRentalTransferBenefitMetric total,
        List<AnalyticsRentalTransferBenefitContextMetric> byContext
) {
}
