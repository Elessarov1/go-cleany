package com.cleany.notification;

public class CustomerNotificationNotFoundException extends RuntimeException {

    public CustomerNotificationNotFoundException(long notificationId) {
        super("Customer notification not found: " + notificationId);
    }
}
