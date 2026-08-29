package com.cleany.notification;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
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
    private final CustomerNotificationRecorder recorder;
    private final Map<ExternalIdentityProvider, CustomerNotificationSender> senders;

    public CustomerNotificationDispatcher(
            CustomerExternalIdentityRepository identityRepository,
            CustomerNotificationRecorder recorder,
            List<CustomerNotificationSender> senders
    ) {
        this.identityRepository = identityRepository;
        this.recorder = recorder;
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
        if (!recorder.record(customerId, notification)) {
            log.debug("Skipping duplicate notification {} for customer {}", notification.deduplicationKey(), customerId);
            return false;
        }
        var identity = identityRepository.findByIdAndCustomerId(communicationIdentityId, customerId)
                .orElse(null);
        var identities = identityRepository.findAllByCustomerIdOrderByProvider(customerId);
        if (identities == null || identities.isEmpty()) {
            identities = identity == null ? Collections.emptyList() : List.of(identity);
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
            try {
                sender.send(new CommunicationTarget(
                        candidate.getCustomerId(),
                        candidate.getId(),
                        candidate.getProvider(),
                        candidate.getExternalSubject(),
                        candidate.getLanguageCode()
                ), notification);
                delivered = true;
            } catch (RuntimeException exception) {
                log.warn(
                        "Customer notification {} was recorded but {} delivery failed for customer {}",
                        notification.type(), candidate.getProvider(), customerId, exception
                );
            }
        }
        if (!delivered) {
            log.debug("No enabled notification channel for customer {}", customerId);
        }
        return delivered;
    }
}
