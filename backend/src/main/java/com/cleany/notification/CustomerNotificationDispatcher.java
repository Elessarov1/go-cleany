package com.cleany.notification;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;

@Component
public class CustomerNotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(CustomerNotificationDispatcher.class);

    private final CustomerExternalIdentityRepository identityRepository;
    private final Map<ExternalIdentityProvider, CustomerNotificationSender> senders;

    public CustomerNotificationDispatcher(
            CustomerExternalIdentityRepository identityRepository,
            List<CustomerNotificationSender> senders
    ) {
        this.identityRepository = identityRepository;
        var sendersByProvider = new EnumMap<ExternalIdentityProvider, CustomerNotificationSender>(
                ExternalIdentityProvider.class
        );
        for (CustomerNotificationSender sender : senders) {
            CustomerNotificationSender existing = sendersByProvider.putIfAbsent(
                    Objects.requireNonNull(sender.provider(), "sender provider"),
                    sender
            );
            if (existing != null) {
                throw new IllegalStateException(
                        "Multiple customer notification senders registered for " + sender.provider()
                );
            }
        }
        this.senders = Map.copyOf(sendersByProvider);
    }

    public boolean send(
            long customerId,
            long communicationIdentityId,
            CustomerNotification notification
    ) {
        Objects.requireNonNull(notification, "notification");
        var identity = identityRepository.findByIdAndCustomerId(communicationIdentityId, customerId)
                .orElseThrow(() -> new IllegalStateException(
                        "Communication identity " + communicationIdentityId
                                + " is unavailable for customer " + customerId
                ));
        var target = new CommunicationTarget(
                identity.getCustomerId(),
                communicationIdentityId,
                identity.getProvider(),
                identity.getExternalSubject(),
                identity.getLanguageCode()
        );
        CustomerNotificationSender sender = senders.get(target.provider());
        if (sender == null) {
            log.warn(
                    "Customer notification sender is unavailable for provider {} and identity {}",
                    target.provider(),
                    target.externalIdentityId()
            );
            return false;
        }
        sender.send(target, notification);
        return true;
    }
}
