package com.cleany.analytics;

import java.math.BigDecimal;

import com.cleany.catalog.PlatformService;
import com.cleany.reminder.CustomerReminderType;

public record AnalyticsReminderMetric(
        CustomerReminderType type,
        PlatformService sourceService,
        long notificationsCreated,
        Long targetTasksCreated,
        long targetTasksCompleted,
        BigDecimal creationRate,
        BigDecimal completionRate
) {
}
