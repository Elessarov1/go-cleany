package com.cleany.notification;

import com.cleany.customer.ExternalIdentityProvider;

public interface CustomerNotificationSender {

    ExternalIdentityProvider provider();

    void send(CommunicationTarget target, CustomerNotification notification);
}
