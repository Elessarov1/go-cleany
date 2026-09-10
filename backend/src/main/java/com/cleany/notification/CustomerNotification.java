package com.cleany.notification;

import com.cleany.action.ActionTarget;

public interface CustomerNotification {

    CustomerNotificationType type();

    ActionTarget action();

    String deduplicationKey();
}
