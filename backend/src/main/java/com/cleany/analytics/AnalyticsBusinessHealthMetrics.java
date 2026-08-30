package com.cleany.analytics;

import java.math.BigDecimal;

public record AnalyticsBusinessHealthMetrics(
        long completedTasks,
        long activeCustomers,
        BigDecimal completedTasksPerActiveCustomer,
        long customersWithTwoPlusCompletedTasks,
        long customersUsingTwoPlusServices,
        BigDecimal crossServiceCustomerRate
) {
}
