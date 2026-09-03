package com.cleany.reminder;

public record SmartReminderProcessingResult(
        int candidates,
        int processed,
        int skipped,
        int failed,
        int notified,
        int superseded,
        int expired
) {
}
