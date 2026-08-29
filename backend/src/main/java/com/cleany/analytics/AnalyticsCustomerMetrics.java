package com.cleany.analytics;

import java.math.BigDecimal;

public record AnalyticsCustomerMetrics(
        long newCustomers,
        long activeCustomers,
        long repeatCustomers,
        BigDecimal repeatRate
) {
}
