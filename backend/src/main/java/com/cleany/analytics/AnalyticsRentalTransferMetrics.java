package com.cleany.analytics;

import java.util.List;

public record AnalyticsRentalTransferMetrics(
        AnalyticsActionFunnelMetric total,
        List<AnalyticsRentalTransferContextMetric> byContext
) {
}
