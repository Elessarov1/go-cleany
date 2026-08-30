package com.cleany.analytics;

import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;

public record AnalyticsRentalTransferContextMetric(
        RentalTransferContextType context,
        AnalyticsActionFunnelMetric funnel
) {
}
