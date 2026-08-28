package com.cleany.notification;

public interface CustomerNotification {

    CustomerNotificationType type();

    String targetPath();

    String deduplicationKey();
}
