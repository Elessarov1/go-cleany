package com.cleany.reminder;

public record SmartReminderProcessingResult(
        int notified,
        int superseded,
        int expired
) {
}
