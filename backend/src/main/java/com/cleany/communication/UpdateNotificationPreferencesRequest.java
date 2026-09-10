package com.cleany.communication;

public record UpdateNotificationPreferencesRequest(Boolean telegramEnabled, Boolean pushEnabled) {
    public UpdateNotificationPreferencesRequest {
        if (telegramEnabled == null && pushEnabled == null) {
            throw new IllegalArgumentException("At least one preference must be provided");
        }
    }
}
