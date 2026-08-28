package com.cleany.notification;

import java.time.Instant;

public record CustomerNotificationResponse(
        long id,
        CustomerNotificationType type,
        String targetPath,
        Instant createdAt,
        Instant readAt
) {
}
