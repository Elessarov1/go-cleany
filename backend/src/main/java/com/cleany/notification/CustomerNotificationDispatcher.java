package com.cleany.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.communication.CommunicationEndpoint;
import com.cleany.communication.CommunicationEndpointService;
import com.cleany.communication.CommunicationEndpointType;
import com.cleany.communication.NotificationDeliveryQueue;
import com.cleany.communication.NotificationPreferenceService;

@Component
public class CustomerNotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(CustomerNotificationDispatcher.class);

    private final CustomerExternalIdentityRepository identityRepository;
    private final CustomerNotificationRecorder recorder;
    private final CommunicationEndpointService endpointService;
    private final NotificationPreferenceService preferenceService;
    private final NotificationDeliveryQueue deliveryQueue;

    public CustomerNotificationDispatcher(
            CustomerExternalIdentityRepository identityRepository,
            CustomerNotificationRecorder recorder,
            CommunicationEndpointService endpointService,
            NotificationPreferenceService preferenceService,
            NotificationDeliveryQueue deliveryQueue
    ) {
        this.identityRepository = identityRepository;
        this.recorder = recorder;
        this.endpointService = endpointService;
        this.preferenceService = preferenceService;
        this.deliveryQueue = deliveryQueue;
    }

    @Transactional
    public boolean send(
            long customerId,
            long communicationIdentityId,
            CustomerNotification notification
    ) {
        Objects.requireNonNull(notification, "notification");
        Long notificationId = recorder.recordId(customerId, notification);
        if (notificationId == null) {
            log.debug("Skipping duplicate notification {} for customer {}",
                    notification.deduplicationKey(), customerId);
            return false;
        }
        var preferences = preferenceService.forCustomer(customerId);
        List<CommunicationEndpoint> endpoints = new ArrayList<>();
        if (preferences.telegramEnabled()) {
            identityRepository.findAllByCustomerIdOrderByProvider(customerId).stream()
                    .filter(identity -> identity.getProvider() == ExternalIdentityProvider.TELEGRAM)
                    .filter(identity -> identity.isWriteAccessAllowed())
                    .findFirst()
                    .map(endpointService::ensureTelegram)
                    .ifPresent(endpoints::add);
        }
        if (preferences.pushEnabled()) {
            endpointService.active(customerId).stream()
                    .filter(endpoint -> endpoint.getType() == CommunicationEndpointType.FCM)
                    .forEach(endpoints::add);
        }
        return deliveryQueue.enqueue(notificationId, endpoints) > 0;
    }

    @Transactional
    public boolean sendDurably(
            long customerId,
            long communicationIdentityId,
            CustomerNotification notification
    ) {
        return send(customerId, communicationIdentityId, notification);
    }
}
