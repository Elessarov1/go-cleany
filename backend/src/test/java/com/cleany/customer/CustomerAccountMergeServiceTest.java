package com.cleany.customer;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class CustomerAccountMergeServiceTest {

    @Mock
    private CustomerAccountRepository accountRepository;

    @Mock
    private CustomerExternalIdentityRepository identityRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private EntityManager entityManager;

    @Test
    void mergeKeepsCanonicalTargetCopiesProfileAndReassignsEveryCurrentOwnershipReference() {
        Instant targetCreatedAt = Instant.parse("2026-09-02T10:00:00Z");
        Instant sourceCreatedAt = Instant.parse("2026-08-01T10:00:00Z");
        CustomerAccount target = account(10L, targetCreatedAt, null);
        CustomerAccount source = account(20L, sourceCreatedAt, "+905551234567");
        CustomerExternalIdentity google = identity(ExternalIdentityProvider.GOOGLE, "google-sub");
        CustomerExternalIdentity telegram = identity(ExternalIdentityProvider.TELEGRAM, "900001");
        Mockito.when(accountRepository.findAllByIdForUpdate(List.of(10L, 20L)))
                .thenReturn(List.of(target, source));
        Mockito.when(identityRepository.findAllByCustomerIdOrderByProvider(10L))
                .thenReturn(List.of(google));
        Mockito.when(identityRepository.findAllByCustomerIdOrderByProvider(20L))
                .thenReturn(List.of(telegram));

        long result = service().mergeInto(10L, 20L);

        Assertions.assertEquals(10L, result);
        Mockito.verify(target).mergeProfile(sourceCreatedAt, "+905551234567");
        Mockito.verify(accountRepository).delete(source);
        Mockito.verify(entityManager).flush();

        String statements = Mockito.mockingDetails(jdbcTemplate).getInvocations().stream()
                .map(invocation -> invocation.getArgument(0, String.class))
                .collect(java.util.stream.Collectors.joining("\n"));
        Assertions.assertAll(
                () -> Assertions.assertTrue(statements.contains("customer_role")),
                () -> Assertions.assertTrue(statements.contains("rental_admin_notification_preference")),
                () -> Assertions.assertTrue(statements.contains("update support_case set customer_id")),
                () -> Assertions.assertTrue(statements.contains("resolved_by_customer_id")),
                () -> Assertions.assertTrue(statements.contains("update transaction_feedback")),
                () -> Assertions.assertTrue(statements.contains("update cleaning_order set customer_id")),
                () -> Assertions.assertTrue(statements.contains("referrer_customer_id")),
                () -> Assertions.assertTrue(statements.contains("update rental_booking")),
                () -> Assertions.assertTrue(statements.contains("update transfer_booking")),
                () -> Assertions.assertTrue(statements.contains("update rental_cleaning_benefit")),
                () -> Assertions.assertTrue(statements.contains("update referral_reward")),
                () -> Assertions.assertTrue(statements.contains("update referral_code")),
                () -> Assertions.assertTrue(statements.contains("update platform_service_state")),
                () -> Assertions.assertTrue(statements.contains("update customer_identity_link_request")),
                () -> Assertions.assertTrue(statements.contains("update customer_external_identity"))
        );
    }

    @Test
    void conflictingPhonesStopMergeBeforeAnyOwnershipChange() {
        CustomerAccount target = account(10L, Instant.parse("2026-08-01T00:00:00Z"), "+905551111111");
        CustomerAccount source = account(20L, Instant.parse("2026-08-02T00:00:00Z"), "+905552222222");
        CustomerExternalIdentity google = identity(ExternalIdentityProvider.GOOGLE, "google-sub");
        CustomerExternalIdentity telegram = identity(ExternalIdentityProvider.TELEGRAM, "900001");
        prepareAccounts(target, source);
        Mockito.when(identityRepository.findAllByCustomerIdOrderByProvider(10L))
                .thenReturn(List.of(google));
        Mockito.when(identityRepository.findAllByCustomerIdOrderByProvider(20L))
                .thenReturn(List.of(telegram));

        Assertions.assertThrows(AccountLinkConflictException.class, () -> service().mergeInto(10L, 20L));

        Mockito.verifyNoInteractions(jdbcTemplate);
        Mockito.verify(accountRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void conflictingIdentityOfSameProviderStopsMerge() {
        CustomerAccount target = account(10L, Instant.parse("2026-08-01T00:00:00Z"), null);
        CustomerAccount source = account(20L, Instant.parse("2026-08-02T00:00:00Z"), null);
        CustomerExternalIdentity googleA = identity(ExternalIdentityProvider.GOOGLE, "google-a");
        CustomerExternalIdentity googleB = identity(ExternalIdentityProvider.GOOGLE, "google-b");
        prepareAccounts(target, source);
        Mockito.when(identityRepository.findAllByCustomerIdOrderByProvider(10L))
                .thenReturn(List.of(googleA));
        Mockito.when(identityRepository.findAllByCustomerIdOrderByProvider(20L))
                .thenReturn(List.of(googleB));

        Assertions.assertThrows(AccountLinkConflictException.class, () -> service().mergeInto(10L, 20L));

        Mockito.verifyNoInteractions(jdbcTemplate);
    }

    @Test
    void sameAccountIsIdempotent() {
        Assertions.assertEquals(10L, service().mergeInto(10L, 10L));
        Mockito.verifyNoInteractions(accountRepository, identityRepository, jdbcTemplate, entityManager);
    }

    private void prepareAccounts(CustomerAccount target, CustomerAccount source) {
        Mockito.when(accountRepository.findAllByIdForUpdate(List.of(10L, 20L)))
                .thenReturn(List.of(target, source));
    }

    private CustomerAccountMergeService service() {
        return new CustomerAccountMergeService(
                accountRepository,
                identityRepository,
                jdbcTemplate,
                entityManager
        );
    }

    private static CustomerAccount account(long id, Instant createdAt, String phone) {
        CustomerAccount account = Mockito.mock(CustomerAccount.class);
        Mockito.lenient().when(account.getId()).thenReturn(id);
        Mockito.lenient().when(account.getCreatedAt()).thenReturn(createdAt);
        Mockito.lenient().when(account.getPhone()).thenReturn(phone);
        return account;
    }

    private static CustomerExternalIdentity identity(
            ExternalIdentityProvider provider,
            String subject
    ) {
        CustomerExternalIdentity identity = Mockito.mock(CustomerExternalIdentity.class);
        Mockito.when(identity.getProvider()).thenReturn(provider);
        Mockito.when(identity.getExternalSubject()).thenReturn(subject);
        return identity;
    }
}
