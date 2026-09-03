package com.cleany.customer;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cleany.authorization.CustomerRoleBootstrapService;
import com.cleany.order.PhoneNumberNormalizer;

class CustomerAccountServiceTest {

    private static final Instant NOW = Instant.parse("2026-08-21T09:00:00Z");

    private CustomerIdentityProvider identityProvider;
    private CustomerAccountRepository accountRepository;
    private CustomerExternalIdentityRepository identityRepository;
    private CustomerRoleBootstrapService roleBootstrapService;
    private CurrentCustomerRequestCache requestCache;
    private CustomerAccountResolutionService resolutionService;
    private PhoneNumberNormalizer phoneNumberNormalizer;
    private CustomerAccountService service;

    @BeforeEach
    void setUp() {
        identityProvider = Mockito.mock(CustomerIdentityProvider.class);
        accountRepository = Mockito.mock(CustomerAccountRepository.class);
        identityRepository = Mockito.mock(CustomerExternalIdentityRepository.class);
        roleBootstrapService = Mockito.mock(CustomerRoleBootstrapService.class);
        requestCache = Mockito.mock(CurrentCustomerRequestCache.class);
        phoneNumberNormalizer = Mockito.mock(PhoneNumberNormalizer.class);
        resolutionService = new CustomerAccountResolutionService(
                accountRepository,
                identityRepository,
                roleBootstrapService,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
        service = new CustomerAccountService(
                identityProvider,
                accountRepository,
                resolutionService,
                requestCache,
                phoneNumberNormalizer
        );
    }

    @Test
    void normalizedPhone_isStoredWithoutParsingItAgain() {
        CustomerAccount account = Mockito.mock(CustomerAccount.class);
        Mockito.when(accountRepository.findById(77L)).thenReturn(Optional.of(account));

        service.updateNormalizedPhone(77L, "+905551234567");

        Mockito.verify(account).updatePhone("+905551234567");
        Mockito.verifyNoInteractions(phoneNumberNormalizer);
    }

    @Test
    void explicitGoogleIdentity_customerAndExternalIdentityCreated() {
        CustomerAccount account = Mockito.mock(CustomerAccount.class);
        CustomerExternalIdentity persistedIdentity = Mockito.mock(CustomerExternalIdentity.class);
        var authenticatedIdentity = new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.GOOGLE,
                "905551234567",
                null,
                "Alex",
                " RU_tr "
        );
        Mockito.when(account.getId()).thenReturn(77L);
        Mockito.when(accountRepository.save(Mockito.any(CustomerAccount.class))).thenReturn(account);
        Mockito.when(identityRepository.save(Mockito.any(CustomerExternalIdentity.class)))
                .thenReturn(persistedIdentity);
        stubIdentity(
                persistedIdentity,
                77L,
                88L,
                ExternalIdentityProvider.GOOGLE,
                "905551234567",
                null,
                "Alex",
                "ru-tr"
        );

        CurrentCustomer customer = service.resolveCustomer(authenticatedIdentity);

        var identityCaptor = ArgumentCaptor.forClass(CustomerExternalIdentity.class);
        Mockito.verify(identityRepository).save(identityCaptor.capture());
        Mockito.verifyNoInteractions(identityProvider);
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        ExternalIdentityProvider.GOOGLE,
                        identityCaptor.getValue().getProvider()
                ),
                () -> Assertions.assertEquals(
                        "905551234567",
                        identityCaptor.getValue().getExternalSubject()
                ),
                () -> Assertions.assertEquals("ru-tr", identityCaptor.getValue().getLanguageCode()),
                () -> Assertions.assertEquals(77L, customer.customerId()),
                () -> Assertions.assertEquals(88L, customer.externalIdentityId()),
                () -> Assertions.assertEquals(ExternalIdentityProvider.GOOGLE, customer.provider()),
                () -> Assertions.assertEquals("905551234567", customer.externalSubject())
        );
    }

    @Test
    void sameGoogleIdentity_resolvedAgain_sameCustomerReturnedAndMetadataRefreshed() {
        CustomerAccount account = Mockito.mock(CustomerAccount.class);
        CustomerExternalIdentity persistedIdentity = Mockito.mock(CustomerExternalIdentity.class);
        var firstIdentity = new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.GOOGLE,
                "905551234567",
                "old-name",
                "Alex",
                "ru"
        );
        var refreshedIdentity = new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.GOOGLE,
                "905551234567",
                "new-name",
                "Alex Updated",
                "en_US"
        );
        Mockito.when(identityRepository.findByProviderAndExternalSubject(
                ExternalIdentityProvider.GOOGLE,
                "905551234567"
        )).thenReturn(Optional.empty(), Optional.of(persistedIdentity));
        Mockito.when(account.getId()).thenReturn(77L);
        Mockito.when(accountRepository.save(Mockito.any(CustomerAccount.class))).thenReturn(account);
        Mockito.when(accountRepository.findById(77L)).thenReturn(Optional.of(account));
        Mockito.when(identityRepository.save(Mockito.any(CustomerExternalIdentity.class)))
                .thenReturn(persistedIdentity);
        stubIdentity(
                persistedIdentity,
                77L,
                88L,
                ExternalIdentityProvider.GOOGLE,
                "905551234567",
                "new-name",
                "Alex Updated",
                "en-us"
        );

        CurrentCustomer first = service.resolveCustomer(firstIdentity);
        CurrentCustomer second = service.resolveCustomer(refreshedIdentity);

        Mockito.verify(persistedIdentity).refresh(
                "new-name",
                "Alex Updated",
                "en-us",
                null,
                false,
                NOW
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(first.customerId(), second.customerId()),
                () -> Assertions.assertEquals(first.externalIdentityId(), second.externalIdentityId()),
                () -> Assertions.assertEquals("new-name", second.username()),
                () -> Assertions.assertEquals("Alex Updated", second.displayName()),
                () -> Assertions.assertEquals("en-us", second.languageCode())
        );
    }

    @Test
    void currentCustomer_telegramRequestIdentityDelegatedToExplicitResolution() {
        var authenticatedIdentity = new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.TELEGRAM,
                "900001",
                "alex",
                "Alex",
                "ru"
        );
        var expected = new CurrentCustomer(
                77L,
                88L,
                ExternalIdentityProvider.TELEGRAM,
                "900001",
                "alex",
                "Alex",
                "ru"
        );
        CustomerAccountResolutionService mockedResolution = Mockito.mock(
                CustomerAccountResolutionService.class
        );
        service = new CustomerAccountService(
                identityProvider,
                accountRepository,
                mockedResolution,
                requestCache,
                phoneNumberNormalizer
        );
        Mockito.when(requestCache.current()).thenReturn(Optional.empty());
        Mockito.when(identityProvider.currentIdentity()).thenReturn(authenticatedIdentity);
        Mockito.when(mockedResolution.resolve(authenticatedIdentity)).thenReturn(expected);

        CurrentCustomer customer = service.currentCustomer();

        Assertions.assertSame(expected, customer);
        Mockito.verify(identityProvider).currentIdentity();
        Mockito.verify(mockedResolution).resolve(authenticatedIdentity);
        Mockito.verify(requestCache).store(expected);
        Mockito.verifyNoInteractions(accountRepository, identityRepository);
    }

    private static void stubIdentity(
            CustomerExternalIdentity identity,
            long customerId,
            long identityId,
            ExternalIdentityProvider provider,
            String externalSubject,
            String username,
            String displayName,
            String languageCode
    ) {
        Mockito.when(identity.getCustomerId()).thenReturn(customerId);
        Mockito.when(identity.getId()).thenReturn(identityId);
        Mockito.when(identity.getProvider()).thenReturn(provider);
        Mockito.when(identity.getExternalSubject()).thenReturn(externalSubject);
        Mockito.when(identity.getUsername()).thenReturn(username);
        Mockito.when(identity.getDisplayName()).thenReturn(displayName);
        Mockito.when(identity.getLanguageCode()).thenReturn(languageCode);
    }
}
