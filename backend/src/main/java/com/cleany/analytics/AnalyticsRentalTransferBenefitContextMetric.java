package com.cleany.analytics;

import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;

public record AnalyticsRentalTransferBenefitContextMetric(
        RentalTransferContextType context,
        AnalyticsRentalTransferBenefitMetric metric
) {
}
