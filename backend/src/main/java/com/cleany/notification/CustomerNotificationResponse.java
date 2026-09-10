package com.cleany.notification;

import java.time.Instant;

import com.cleany.action.ActionTarget;

public record CustomerNotificationResponse(
        long id,
        CustomerNotificationType type,
        ActionTarget action,
        Instant createdAt,
        Instant readAt
) {
}
