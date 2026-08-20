package com.cleany.customer;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cleany.order.PhoneNumberNormalizer;
import com.cleany.telegram.CustomerIdentityProvider;
import com.cleany.telegram.TelegramPrincipal;

class CustomerAccountServiceTest {

    @Test
    void telegramLanguage_storedWithChannelIdentity() {
        CustomerIdentityProvider identityProvider = Mockito.mock(CustomerIdentityProvider.class);
        CustomerAccountRepository accountRepository = Mockito.mock(CustomerAccountRepository.class);
        CustomerExternalIdentityRepository identityRepository =
                Mockito.mock(CustomerExternalIdentityRepository.class);
        CustomerAccount account = Mockito.mock(CustomerAccount.class);
        Mockito.when(identityProvider.currentCustomer()).thenReturn(
                new TelegramPrincipal(900001L, "alex", "Alex", null, " EN_us ")
        );
        Mockito.when(account.getId()).thenReturn(77L);
        Mockito.when(accountRepository.save(Mockito.any(CustomerAccount.class))).thenReturn(account);
        var service = new CustomerAccountService(
                identityProvider,
                accountRepository,
                identityRepository,
                new PhoneNumberNormalizer(),
                Clock.fixed(Instant.parse("2026-08-21T09:00:00Z"), ZoneOffset.UTC)
        );

        service.currentCustomer();

        var identityCaptor = ArgumentCaptor.forClass(CustomerExternalIdentity.class);
        Mockito.verify(identityRepository).save(identityCaptor.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals("900001", identityCaptor.getValue().getExternalSubject()),
                () -> Assertions.assertEquals("en-us", identityCaptor.getValue().getLanguageCode())
        );
    }
}
