package com.cleany.communication;

import com.cleany.notification.CustomerNotification;

public interface NotificationDeliveryChannel {
    CommunicationEndpointType endpointType();
    void deliver(long notificationId, CommunicationEndpoint endpoint,
                 CustomerNotification notification);
}
