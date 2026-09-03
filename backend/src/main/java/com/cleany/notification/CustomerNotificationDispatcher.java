package com.cleany.notification;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
        return prepare(customerId, communicationIdentityId, notification)
                .map(this::deliver)
                .orElse(false);
    }

    public boolean sendAfterCommit(
            long customerId,
            long communicationIdentityId,
            CustomerNotification notification
    ) {
        Optional<Delivery> prepared = prepare(
                customerId,
                communicationIdentityId,
                notification
        );
        if (prepared.isEmpty()) {
            return false;
        }

        Delivery delivery = prepared.orElseThrow();
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return deliver(delivery);
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deliver(delivery);
            }
        });
        return true;
    }

    private Optional<Delivery> prepare(
            long customerId,
            long communicationIdentityId,
            CustomerNotification notification
    ) {
        Objects.requireNonNull(notification, "notification");
        if (!recorder.record(customerId, notification)) {
            log.debug("Skipping duplicate notification {} for customer {}", notification.deduplicationKey(), customerId);
            return Optional.empty();
        }
        var identity = identityRepository.findByIdAndCustomerId(communicationIdentityId, customerId)
                .orElse(null);
        var identities = identityRepository.findAllByCustomerIdOrderByProvider(customerId);
        if (identities == null || identities.isEmpty()) {
            identities = identity == null ? Collections.emptyList() : List.of(identity);
        }
        var targets = identities.stream()
                .filter(candidate -> senders.containsKey(candidate.getProvider()))
                .filter(candidate -> candidate.getProvider() != ExternalIdentityProvider.TELEGRAM
                        || candidate.isWriteAccessAllowed())
                .map(candidate -> new CommunicationTarget(
                        candidate.getCustomerId(),
                        candidate.getId(),
                        candidate.getProvider(),
                        candidate.getExternalSubject(),
                        candidate.getLanguageCode()
                ))
                .toList();
        return Optional.of(new Delivery(customerId, notification, targets));
    }

    private boolean deliver(Delivery delivery) {
        boolean delivered = false;
        var deliveredProviders = new HashSet<ExternalIdentityProvider>();
        for (CommunicationTarget target : delivery.targets()) {
            CustomerNotificationSender sender = senders.get(target.provider());
            if (sender == null || !deliveredProviders.add(target.provider())) {
                continue;
            }
            try {
                sender.send(target, delivery.notification());
                delivered = true;
            } catch (RuntimeException exception) {
                log.warn(
                        "Customer notification {} was recorded but {} delivery failed for customer {}",
                        delivery.notification().type(), target.provider(), delivery.customerId(), exception
                );
            }
        }
        if (!delivered) {
            log.debug("No enabled notification channel for customer {}", delivery.customerId());
        }
        return delivered;
    }

    private record Delivery(
            long customerId,
            CustomerNotification notification,
            List<CommunicationTarget> targets
    ) {
    }
}
