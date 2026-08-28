package com.cleany.rental;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.admin.AdminAccessService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentity;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.customer.TelegramIdentityNotLinkedException;

class RentalAdminNotificationPreferenceServiceTest {

    private static final Instant NOW = Instant.parse("2026-08-23T10:15:30Z");
    private static final long CUSTOMER_ID = 77L;

    private final AdminAccessService accessService = Mockito.mock(AdminAccessService.class);
    private final CustomerAccountService customerAccountService = Mockito.mock(CustomerAccountService.class);
    private final CustomerExternalIdentityRepository identityRepository =
            Mockito.mock(CustomerExternalIdentityRepository.class);
    private final RentalAdminNotificationPreferenceRepository repository =
            Mockito.mock(RentalAdminNotificationPreferenceRepository.class);
    private final RentalAdminNotificationPreferenceService service =
            new RentalAdminNotificationPreferenceService(
                    accessService,
                    customerAccountService,
                    identityRepository,
                    repository,
                    Clock.fixed(NOW, ZoneOffset.UTC)
            );

    @BeforeEach
    void setUp() {
        Mockito.when(customerAccountService.currentCustomer()).thenReturn(new CurrentCustomer(
                CUSTOMER_ID, 88L, ExternalIdentityProvider.GOOGLE, "google-sub", null, "Alex", "ru"
        ));
    }

    @Test
    void unlinkedGoogleAdmin_readsNormalDisabledState() {
        Mockito.when(identityRepository.findByCustomerIdAndProvider(
                CUSTOMER_ID,
                ExternalIdentityProvider.TELEGRAM
        )).thenReturn(Optional.empty());

        RentalAdminNotificationPreferenceResponse response = service.current();

        Assertions.assertAll(
                () -> Assertions.assertFalse(response.telegramLinked()),
                () -> Assertions.assertFalse(response.telegramEnabled()),
                () -> Assertions.assertThrows(
                        TelegramIdentityNotLinkedException.class,
                        () -> service.update(new UpdateRentalAdminNotificationPreferenceRequest(true))
                )
        );
    }

    @Test
    void linkedGoogleAdmin_updatesPreferenceByCustomerId() {
        CustomerExternalIdentity telegram = Mockito.mock(CustomerExternalIdentity.class);
        Mockito.when(telegram.getUsername()).thenReturn("alex");
        Mockito.when(telegram.isWriteAccessAllowed()).thenReturn(true);
        Mockito.when(identityRepository.findByCustomerIdAndProvider(
                CUSTOMER_ID,
                ExternalIdentityProvider.TELEGRAM
        )).thenReturn(Optional.of(telegram));
        Mockito.when(repository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        RentalAdminNotificationPreferenceResponse response = service.update(
                new UpdateRentalAdminNotificationPreferenceRequest(false)
        );

        Assertions.assertAll(
                () -> Assertions.assertTrue(response.telegramLinked()),
                () -> Assertions.assertFalse(response.telegramEnabled()),
                () -> Assertions.assertEquals("alex", response.telegramUsername())
        );
        Mockito.verify(repository).save(Mockito.argThat(preference ->
                preference.getCustomerId() == CUSTOMER_ID && !preference.isTelegramEnabled()
        ));
    }
}
