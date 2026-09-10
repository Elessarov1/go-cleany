package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.authorization.CustomerRole;
import com.cleany.authorization.CustomerRoleRepository;
import com.cleany.authorization.PlatformRole;
import com.cleany.customer.CustomerExternalIdentity;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.notification.CustomerNotificationDispatcher;

class RentalBookingAdminNotificationListenerTest {

    private final RentalBookingAdminNotificationQueryService queryService =
            Mockito.mock(RentalBookingAdminNotificationQueryService.class);
    private final CustomerRoleRepository roleRepository = Mockito.mock(CustomerRoleRepository.class);
    private final CustomerExternalIdentityRepository identityRepository =
            Mockito.mock(CustomerExternalIdentityRepository.class);
    private final CustomerNotificationDispatcher dispatcher = Mockito.mock(CustomerNotificationDispatcher.class);
    private final RentalBookingAdminNotificationListener listener =
            new RentalBookingAdminNotificationListener(
                    queryService, roleRepository, identityRepository, dispatcher);

    @Test
    void listener_persistsDeliveryBeforeCommit() throws NoSuchMethodException {
        var method = RentalBookingAdminNotificationListener.class.getDeclaredMethod(
                "notifyAdmins",
                RentalBookingAdminEvent.class
        );
        var annotation = method.getAnnotation(TransactionalEventListener.class);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(annotation),
                () -> Assertions.assertEquals(TransactionPhase.BEFORE_COMMIT, annotation.phase()),
                () -> Assertions.assertFalse(annotation.fallbackExecution())
        );
    }

    @Test
    void event_isResolvedAndQueuedThroughDurableBoundary() {
        var event = new RentalBookingAdminEvent(
                42L,
                RentalBookingAdminEvent.Type.CREATED
        );
        RentalBookingAdminNotification notification = notification(RentalTermType.DATE_RANGE);
        Mockito.when(queryService.get(42L)).thenReturn(notification);
        CustomerRole role = new CustomerRole(7L, PlatformRole.ADMIN, Instant.EPOCH);
        CustomerExternalIdentity identity = Mockito.mock(CustomerExternalIdentity.class);
        Mockito.when(identity.getId()).thenReturn(11L);
        Mockito.when(roleRepository.findAllByRole(PlatformRole.ADMIN)).thenReturn(List.of(role));
        Mockito.when(identityRepository.findAllByCustomerIdOrderByProvider(7L)).thenReturn(List.of(identity));

        listener.notifyAdmins(event);

        Mockito.verify(dispatcher).send(
                Mockito.eq(7L), Mockito.eq(11L),
                Mockito.eq(new RentalBookingAdminCustomerNotification(event.type(), notification)));
    }

    @Test
    void durablePersistenceFailureEscapesAndCanRollBackBusinessTransaction() {
        var event = new RentalBookingAdminEvent(
                42L,
                RentalBookingAdminEvent.Type.CANCELLED_BY_CUSTOMER
        );
        RentalBookingAdminNotification notification = notification(RentalTermType.DATE_RANGE);
        Mockito.when(queryService.get(42L)).thenReturn(notification);
        CustomerRole role = new CustomerRole(7L, PlatformRole.ADMIN, Instant.EPOCH);
        CustomerExternalIdentity identity = Mockito.mock(CustomerExternalIdentity.class);
        Mockito.when(identity.getId()).thenReturn(11L);
        Mockito.when(roleRepository.findAllByRole(PlatformRole.ADMIN)).thenReturn(List.of(role));
        Mockito.when(identityRepository.findAllByCustomerIdOrderByProvider(7L)).thenReturn(List.of(identity));
        Mockito.doThrow(new IllegalStateException("database unavailable"))
                .when(dispatcher).send(Mockito.eq(7L), Mockito.eq(11L), Mockito.any());

        Assertions.assertThrows(IllegalStateException.class, () -> listener.notifyAdmins(event));
    }

    private static RentalBookingAdminNotification notification(RentalTermType termType) {
        return new RentalBookingAdminNotification(
                42L,
                "Sea View 1+1",
                "Alexandr",
                "+90 555 123 45 67",
                termType,
                LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 9, 29),
                null,
                14,
                new BigDecimal("2000.00"),
                null,
                new BigDecimal("28000.00"),
                "TRY"
        );
    }
}
