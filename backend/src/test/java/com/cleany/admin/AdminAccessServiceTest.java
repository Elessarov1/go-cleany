package com.cleany.admin;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.configuration.AdminProperties;
import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CustomerIdentityProvider;
import com.cleany.customer.ExternalIdentityProvider;

class AdminAccessServiceTest {

    private static final long ADMIN_ID = 900001L;

    @Test
    void currentTelegramIdentity_configuredAsAdmin_accessGranted() {
        CustomerIdentityProvider identityProvider = Mockito.mock(CustomerIdentityProvider.class);
        Mockito.when(identityProvider.currentIdentity()).thenReturn(identity(
                ExternalIdentityProvider.TELEGRAM,
                Long.toString(ADMIN_ID)
        ));
        var service = new AdminAccessService(new AdminProperties(List.of(ADMIN_ID)), identityProvider);

        Assertions.assertEquals(ADMIN_ID, service.requireCurrentAdmin());
    }

    @Test
    void currentNonTelegramIdentity_accessRejected() {
        CustomerIdentityProvider identityProvider = Mockito.mock(CustomerIdentityProvider.class);
        Mockito.when(identityProvider.currentIdentity()).thenReturn(identity(
                ExternalIdentityProvider.WHATSAPP,
                Long.toString(ADMIN_ID)
        ));
        var service = new AdminAccessService(new AdminProperties(List.of(ADMIN_ID)), identityProvider);

        Assertions.assertThrows(AdminNotAuthorizedException.class, service::requireCurrentAdmin);
    }

    @Test
    void currentTelegramIdentity_withNonNumericSubject_accessRejected() {
        CustomerIdentityProvider identityProvider = Mockito.mock(CustomerIdentityProvider.class);
        Mockito.when(identityProvider.currentIdentity()).thenReturn(identity(
                ExternalIdentityProvider.TELEGRAM,
                "not-a-telegram-id"
        ));
        var service = new AdminAccessService(new AdminProperties(List.of(ADMIN_ID)), identityProvider);

        Assertions.assertThrows(AdminNotAuthorizedException.class, service::requireCurrentAdmin);
    }

    private static AuthenticatedCustomerIdentity identity(
            ExternalIdentityProvider provider,
            String externalSubject
    ) {
        return new AuthenticatedCustomerIdentity(
                provider,
                externalSubject,
                "alex",
                "Alex",
                "ru"
        );
    }
}
