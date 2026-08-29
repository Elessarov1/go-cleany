package com.cleany.analytics;

public record AcquisitionMetric(
        AcquisitionChannel channel,
        Long campaignId,
        String campaignName,
        AcquisitionMedium medium,
        long entries,
        long newCustomers,
        long completedTransactions
) {
}
