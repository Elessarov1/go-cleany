package com.cleany.customer;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cleany.order.PhoneNumberNormalizer;

class CustomerAccountServiceTest {

    @Test
    void authenticatedIdentity_storedAndReturnedWithChannelMetadata() {
        CustomerIdentityProvider identityProvider = Mockito.mock(CustomerIdentityProvider.class);
        CustomerAccountRepository accountRepository = Mockito.mock(CustomerAccountRepository.class);
        CustomerExternalIdentityRepository identityRepository =
                Mockito.mock(CustomerExternalIdentityRepository.class);
        CustomerAccount account = Mockito.mock(CustomerAccount.class);
        CustomerExternalIdentity persistedIdentity = Mockito.mock(CustomerExternalIdentity.class);
        Mockito.when(identityProvider.currentIdentity()).thenReturn(
                new AuthenticatedCustomerIdentity(
                        ExternalIdentityProvider.TELEGRAM,
                        "900001",
                        "alex",
                        "Alex",
                        " EN_us "
                )
        );
        Mockito.when(account.getId()).thenReturn(77L);
        Mockito.when(accountRepository.save(Mockito.any(CustomerAccount.class))).thenReturn(account);
        Mockito.when(identityRepository.save(Mockito.any(CustomerExternalIdentity.class)))
                .thenReturn(persistedIdentity);
        Mockito.when(persistedIdentity.getId()).thenReturn(88L);
        Mockito.when(persistedIdentity.getProvider()).thenReturn(ExternalIdentityProvider.TELEGRAM);
        Mockito.when(persistedIdentity.getExternalSubject()).thenReturn("900001");
        Mockito.when(persistedIdentity.getUsername()).thenReturn("alex");
        Mockito.when(persistedIdentity.getDisplayName()).thenReturn("Alex");
        Mockito.when(persistedIdentity.getLanguageCode()).thenReturn("en-us");
        var service = new CustomerAccountService(
                identityProvider,
                accountRepository,
                identityRepository,
                new PhoneNumberNormalizer(),
                Clock.fixed(Instant.parse("2026-08-21T09:00:00Z"), ZoneOffset.UTC)
        );

        CurrentCustomer customer = service.currentCustomer();

        var identityCaptor = ArgumentCaptor.forClass(CustomerExternalIdentity.class);
        Mockito.verify(identityRepository).save(identityCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals("900001", identityCaptor.getValue().getExternalSubject()),
                () -> Assertions.assertEquals("en-us", identityCaptor.getValue().getLanguageCode()),
                () -> Assertions.assertEquals(77L, customer.customerId()),
                () -> Assertions.assertEquals(88L, customer.externalIdentityId()),
                () -> Assertions.assertEquals(ExternalIdentityProvider.TELEGRAM, customer.provider()),
                () -> Assertions.assertEquals("900001", customer.externalSubject()),
                () -> Assertions.assertEquals("alex", customer.username()),
                () -> Assertions.assertEquals("Alex", customer.displayName()),
                () -> Assertions.assertEquals("en-us", customer.languageCode())
        );
    }
}
