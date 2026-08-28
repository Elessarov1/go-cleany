package com.cleany.notification;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashSet;

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
        var identities = identityRepository.findAllByCustomerIdOrderByProvider(customerId);
        if (identities == null || identities.isEmpty()) {
            identities = List.of(identity);
        }
        boolean delivered = false;
        var deliveredProviders = new HashSet<ExternalIdentityProvider>();
        for (var candidate : identities) {
            CustomerNotificationSender sender = senders.get(candidate.getProvider());
            if (sender == null || !deliveredProviders.add(candidate.getProvider())) {
                continue;
            }
            if (candidate.getProvider() == ExternalIdentityProvider.TELEGRAM
                    && !candidate.isWriteAccessAllowed()) {
                continue;
            }
            sender.send(new CommunicationTarget(
                    candidate.getCustomerId(),
                    candidate.getId(),
                    candidate.getProvider(),
                    candidate.getExternalSubject(),
                    candidate.getLanguageCode()
            ), notification);
            delivered = true;
        }
        if (!delivered) {
            log.debug("No enabled notification channel for customer {}", customerId);
        }
        return delivered;
    }
}
