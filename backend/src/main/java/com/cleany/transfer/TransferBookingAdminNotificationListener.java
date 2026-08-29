package com.cleany.transfer;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.authorization.CustomerRoleRepository;
import com.cleany.authorization.PlatformRole;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.notification.CustomerNotificationDispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferBookingAdminNotificationListener {

    private final CustomerRoleRepository roleRepository;
    private final CustomerExternalIdentityRepository identityRepository;
    private final CustomerNotificationDispatcher dispatcher;
    private final TransferBookingNotificationQueryService queryService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyAdmins(TransferBookingCreatedEvent event) {
        TransferAdminNewRequestNotification notification;
        try {
            notification = queryService.adminRequested(event.booking().id());
        } catch (RuntimeException exception) {
            log.error("Transfer admin notification preparation failed for booking {}", event.booking().id(), exception);
            return;
        }
        roleRepository.findAllByRole(PlatformRole.ADMIN).stream()
                .map(role -> role.getCustomerId())
                .distinct()
                .forEach(customerId -> send(customerId, notification));
    }

    private void send(long customerId, TransferAdminNewRequestNotification notification) {
        var identities = identityRepository.findAllByCustomerIdOrderByProvider(customerId);
        if (identities.isEmpty()) {
            log.warn("Admin {} has no external identity for transfer notification", customerId);
            return;
        }
        try {
            dispatcher.send(customerId, identities.getFirst().getId(), notification);
        } catch (RuntimeException exception) {
            log.error(
                    "Transfer admin notification failed for booking {} and admin {}",
                    notification.bookingId(), customerId, exception
            );
        }
    }
}
