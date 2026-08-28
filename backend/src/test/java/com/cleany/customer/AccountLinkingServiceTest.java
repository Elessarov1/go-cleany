package com.cleany.customer;

import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountLinkingServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-02T10:00:00Z");

    @Mock
    private CustomerAccountService customerAccountService;

    @Mock
    private CustomerIdentityLinkRequestRepository requestRepository;

    @Mock
    private CustomerExternalIdentityRepository identityRepository;

    @Mock
    private CustomerAccountMergeService mergeService;

    private AccountLinkingService service() {
        return new AccountLinkingService(
                customerAccountService,
                requestRepository,
                identityRepository,
                mergeService,
                new AccountLinkingProperties("https://t.me/loco_bot/loco_app", Duration.ofMinutes(10)),
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void initiateCreatesHashedShortLivedTokenAndInvalidatesPreviousRequest() {
        Mockito.when(customerAccountService.currentCustomer()).thenReturn(customer(10L, ExternalIdentityProvider.GOOGLE));
        Mockito.when(identityRepository.findByCustomerIdAndProvider(10L, ExternalIdentityProvider.TELEGRAM))
                .thenReturn(Optional.empty());

        AccountLinkInitiatedResponse response = service().initiateTelegramLink();

        String rawToken = queryParameter(response.deepLink(), "startapp");
        ArgumentCaptor<CustomerIdentityLinkRequest> request =
                ArgumentCaptor.forClass(CustomerIdentityLinkRequest.class);
        Mockito.verify(requestRepository).consumeOutstanding(10L, ExternalIdentityProvider.TELEGRAM, NOW);
        Mockito.verify(requestRepository).save(request.capture());
        Assertions.assertAll(
                () -> Assertions.assertNotNull(rawToken),
                () -> Assertions.assertTrue(rawToken.length() >= 43),
                () -> Assertions.assertEquals(64, request.getValue().getTokenHash().length()),
                () -> Assertions.assertFalse(request.getValue().getTokenHash().contains(rawToken)),
                () -> Assertions.assertEquals(NOW.plus(Duration.ofMinutes(10)), response.expiresAt()),
                () -> Assertions.assertEquals(response.expiresAt(), request.getValue().getExpiresAt())
        );
    }

    @Test
    void confirmRequiresTelegramAndConsumesRequestAfterMerge() {
        CustomerIdentityLinkRequest request = request(NOW.plusSeconds(60));
        CustomerExternalIdentity telegram = Mockito.mock(CustomerExternalIdentity.class);
        CustomerExternalIdentity google = identity(10L, ExternalIdentityProvider.GOOGLE, null, false);
        CustomerExternalIdentity linkedTelegram = identity(10L, ExternalIdentityProvider.TELEGRAM, "alex", true);
        Mockito.when(customerAccountService.currentCustomer()).thenReturn(customer(20L, ExternalIdentityProvider.TELEGRAM));
        Mockito.when(requestRepository.findByTokenHashForUpdate(Mockito.anyString())).thenReturn(Optional.of(request));
        Mockito.when(identityRepository.findByProviderAndExternalSubjectForUpdate(
                ExternalIdentityProvider.TELEGRAM,
                "subject-20"
        )).thenReturn(Optional.of(telegram));
        Mockito.when(telegram.getCustomerId()).thenReturn(20L);
        Mockito.when(mergeService.mergeInto(10L, 20L)).thenReturn(10L);
        Mockito.when(identityRepository.findAllByCustomerIdOrderByProvider(10L))
                .thenReturn(List.of(google, linkedTelegram));

        AccountIdentitiesResponse result = service().confirmTelegramLink("opaque-token");

        Assertions.assertAll(
                () -> Assertions.assertEquals(NOW, request.getConsumedAt()),
                () -> Assertions.assertEquals(2, result.identities().size()),
                () -> Assertions.assertTrue(result.identities().stream().anyMatch(
                        identity -> identity.provider() == ExternalIdentityProvider.TELEGRAM
                                && identity.writeAccessAllowed()
                ))
        );
        Mockito.verify(mergeService).mergeInto(10L, 20L);
    }

    @Test
    void expiredConsumedInvalidAndWrongProviderRequestsAreRejected() {
        Mockito.when(customerAccountService.currentCustomer()).thenReturn(customer(20L, ExternalIdentityProvider.TELEGRAM));
        Mockito.when(requestRepository.findByTokenHashForUpdate(Mockito.anyString()))
                .thenReturn(Optional.of(request(NOW)));
        Assertions.assertThrows(AccountLinkTokenExpiredException.class,
                () -> service().confirmTelegramLink("expired"));

        CustomerIdentityLinkRequest consumed = request(NOW.plusSeconds(60));
        consumed.consume(NOW.minusSeconds(1));
        Mockito.when(requestRepository.findByTokenHashForUpdate(Mockito.anyString()))
                .thenReturn(Optional.of(consumed));
        Assertions.assertThrows(AccountLinkTokenConsumedException.class,
                () -> service().confirmTelegramLink("consumed"));

        Mockito.when(requestRepository.findByTokenHashForUpdate(Mockito.anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(AccountLinkTokenInvalidException.class,
                () -> service().confirmTelegramLink("unknown"));

        Mockito.when(requestRepository.findByTokenHashForUpdate(Mockito.anyString()))
                .thenReturn(Optional.of(request(NOW.plusSeconds(60))));
        Mockito.when(customerAccountService.currentCustomer()).thenReturn(customer(10L, ExternalIdentityProvider.GOOGLE));
        Assertions.assertThrows(AccountLinkProviderException.class,
                () -> service().confirmTelegramLink("provider"));
    }

    @Test
    void initiationRejectsTelegramSessionAndAlreadyLinkedTarget() {
        Mockito.when(customerAccountService.currentCustomer()).thenReturn(customer(20L, ExternalIdentityProvider.TELEGRAM));
        Assertions.assertThrows(AccountLinkProviderException.class, () -> service().initiateTelegramLink());

        Mockito.when(customerAccountService.currentCustomer()).thenReturn(customer(10L, ExternalIdentityProvider.GOOGLE));
        Mockito.when(identityRepository.findByCustomerIdAndProvider(10L, ExternalIdentityProvider.TELEGRAM))
                .thenReturn(Optional.of(Mockito.mock(CustomerExternalIdentity.class)));
        Assertions.assertThrows(AccountLinkConflictException.class, () -> service().initiateTelegramLink());
    }

    private static CurrentCustomer customer(long customerId, ExternalIdentityProvider provider) {
        return new CurrentCustomer(
                customerId,
                customerId + 100,
                provider,
                "subject-" + customerId,
                null,
                "Customer",
                "ru"
        );
    }

    private static CustomerIdentityLinkRequest request(Instant expiresAt) {
        return new CustomerIdentityLinkRequest(
                "a".repeat(64),
                10L,
                ExternalIdentityProvider.TELEGRAM,
                NOW.minusSeconds(60),
                expiresAt
        );
    }

    private static CustomerExternalIdentity identity(
            long customerId,
            ExternalIdentityProvider provider,
            String username,
            boolean writeAccessAllowed
    ) {
        CustomerExternalIdentity identity = Mockito.mock(CustomerExternalIdentity.class);
        Mockito.when(identity.getProvider()).thenReturn(provider);
        if (provider == ExternalIdentityProvider.TELEGRAM) {
            Mockito.when(identity.getUsername()).thenReturn(username);
            Mockito.when(identity.isWriteAccessAllowed()).thenReturn(writeAccessAllowed);
        }
        return identity;
    }

    private static String queryParameter(String uri, String name) {
        String query = URI.create(uri).getQuery();
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2 && parts[0].equals(name)) return parts[1];
        }
        return null;
    }
}
