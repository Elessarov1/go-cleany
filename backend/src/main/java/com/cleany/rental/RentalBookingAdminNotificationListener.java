package com.cleany.rental;

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
public class RentalBookingAdminNotificationListener {

    private final RentalBookingAdminNotificationQueryService queryService;
    private final CustomerRoleRepository roleRepository;
    private final CustomerExternalIdentityRepository identityRepository;
    private final CustomerNotificationDispatcher dispatcher;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void notifyAdmins(RentalBookingAdminEvent event) {
        RentalBookingAdminCustomerNotification notification =
                new RentalBookingAdminCustomerNotification(event.type(), queryService.get(event.bookingId()));
        roleRepository.findAllByRole(PlatformRole.ADMIN).stream()
                .map(role -> role.getCustomerId())
                .distinct()
                .forEach(customerId -> notifyAdmin(customerId, notification));
    }

    private void notifyAdmin(long customerId, RentalBookingAdminCustomerNotification notification) {
        var identities = identityRepository.findAllByCustomerIdOrderByProvider(customerId);
        if (identities.isEmpty()) {
            log.warn("Admin {} has no identity for rental notification", customerId);
            return;
        }
        dispatcher.send(customerId, identities.getFirst().getId(), notification);
    }
}
