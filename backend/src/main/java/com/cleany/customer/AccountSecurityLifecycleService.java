package com.cleany.customer;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.cleany.authentication.AccountProofService;
import com.cleany.authentication.SessionTokenService;
import com.cleany.authentication.AppleCredentialLifecycleService;
import com.cleany.authentication.SessionTokenCrypto;
import com.cleany.authorization.CustomerRoleRepository;
import com.cleany.authorization.PlatformRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountSecurityLifecycleService {
    private final CustomerAccountService accountService;
    private final CustomerAccountRepository accountRepository;
    private final CustomerExternalIdentityRepository identityRepository;
    private final CustomerRoleRepository roleRepository;
    private final List<AccountDeletionParticipant> deletionParticipants;
    private final AccountProofService proofService;
    private final SessionTokenService sessionTokenService;
    private final AppleCredentialLifecycleService appleCredentialLifecycleService;
    private final SessionTokenCrypto tokenCrypto;
    private final SecurityAuditService auditService;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;
    private final Clock clock;

    @Transactional
    public IdentityLinkAttemptResponse createLink(CreateIdentityLinkRequest request) {
        CurrentCustomer current = accountService.currentCustomer();
        if (identityRepository.findByCustomerIdAndProvider(current.customerId(), request.provider()).isPresent()) {
            throw conflict("identity_provider_already_linked", "This provider is already linked");
        }
        var result = proofService.createLink(current, request.provider());
        return new IdentityLinkAttemptResponse(result.id(), result.provider(), result.nonce(),
                result.reauthenticationNonce(), result.telegramDeepLink(), result.expiresAt());
    }

    public void verifyLink(UUID id, VerifyIdentityLinkRequest request) {
        proofService.verifyLink(id, accountService.currentCustomer(), request.identityToken(),
                request.authorizationCode());
    }

    public AccountIdentitiesResponse confirmLink(UUID id, ConfirmIdentityLinkRequest request) {
        CurrentCustomer current = accountService.currentCustomer();
        var prepared = proofService.prepareLinkConfirmation(id, current,
                request.identityToken(), request.telegramInitData());
        return transactionTemplate.execute(status -> completeLink(current, prepared));
    }

    private AccountIdentitiesResponse completeLink(CurrentCustomer current,
                                                    AccountProofService.PreparedLinkConfirmation prepared) {
        AuthenticatedCustomerIdentity verified = prepared.identity();
        accountRepository.findByIdForUpdate(current.customerId())
                .orElseThrow(() -> conflict("account_not_found", "Account no longer exists"));
        if (identityRepository.findByCustomerIdAndProvider(current.customerId(), verified.provider()).isPresent()
                || identityRepository.findByProviderAndIssuerAndExternalSubject(
                        verified.provider(), verified.issuer(), verified.externalSubject()).isPresent()) {
            throw conflict("identity_already_linked", "Identity is already linked to an account");
        }
        try {
            proofService.consumePreparedLink(prepared, current);
            CustomerExternalIdentity linked = identityRepository.saveAndFlush(new CustomerExternalIdentity(
                    current.customerId(), verified.provider(), verified.issuer(), verified.externalSubject(),
                    verified.username(), verified.displayName(), verified.languageCode(), verified.email(),
                    verified.emailVerified(), clock.instant()));
            if (verified.provider() == ExternalIdentityProvider.TELEGRAM && verified.allowsWriteToPm()) {
                linked.allowWriteAccess(clock.instant());
            }
            if (verified.provider() == ExternalIdentityProvider.APPLE) {
                if (prepared.credentialCiphertext() == null) {
                    throw conflict("apple_token_exchange_required", "Apple credential is unavailable");
                }
                appleCredentialLifecycleService.retainLinkedCredential(
                        linked.getId(), prepared.credentialCiphertext());
            }
            auditService.success(current.customerId(), "IDENTITY_LINKED", verified.provider(), linked.getId());
        } catch (DataIntegrityViolationException exception) {
            throw conflict("identity_already_linked", "Identity is already linked to an account");
        }
        return identities(current.customerId());
    }

    @Transactional
    public ReauthenticationChallengeResponse createReauthentication() {
        var result = proofService.createReauthentication(accountService.currentCustomer());
        return new ReauthenticationChallengeResponse(result.id(), result.provider(), result.nonce(),
                result.expiresAt());
    }

    public AccountIdentitiesResponse unlink(long identityId, SensitiveAccountActionRequest request) {
        CurrentCustomer current = accountService.currentCustomer();
        var prepared = proofService.prepareReauthentication(request.challengeId(), current, request.identityToken(),
                request.telegramInitData());
        return transactionTemplate.execute(status -> unlinkVerified(identityId, current, prepared));
    }

    private AccountIdentitiesResponse unlinkVerified(
            long identityId, CurrentCustomer current,
            AccountProofService.PreparedReauthentication prepared
    ) {
        proofService.consumePreparedReauthentication(prepared, current);
        accountRepository.findByIdForUpdate(current.customerId())
                .orElseThrow(() -> conflict("account_not_found", "Account no longer exists"));
        List<CustomerExternalIdentity> identities = identityRepository
                .findAllByCustomerIdOrderByProvider(current.customerId());
        CustomerExternalIdentity identity = identities.stream()
                .filter(candidate -> candidate.getId() == identityId)
                .findFirst()
                .orElseThrow(() -> conflict("identity_not_found", "Identity is not linked to this account"));
        if (identities.size() <= 1) {
            throw conflict("last_login_method_cannot_be_removed", "The last login method cannot be removed");
        }
        sessionTokenService.revokeByIdentity(identityId, "IDENTITY_UNLINKED");
        appleCredentialLifecycleService.scheduleIdentityRevocation(identityId);
        jdbcTemplate.update("delete from communication_endpoint where customer_id = ? and session_id in "
                + "(select id from customer_session where authentication_identity_id = ?)",
                current.customerId(), identityId);
        if (identity.getProvider() == ExternalIdentityProvider.TELEGRAM) {
            jdbcTemplate.update("delete from communication_endpoint "
                            + "where customer_id = ? and type = 'TELEGRAM' and address_hash = ?",
                    current.customerId(), tokenCrypto.hash(identity.getExternalSubject()));
        }
        identityRepository.delete(identity);
        auditService.success(current.customerId(), "IDENTITY_UNLINKED", identity.getProvider(), identityId);
        return identities(current.customerId());
    }

    @Transactional
    public AccountDeletionRequestResponse createDeletionRequest() {
        CurrentCustomer current = accountService.currentCustomer();
        if (roleRepository.existsByCustomerIdAndRole(current.customerId(), PlatformRole.ADMIN)) {
            throw conflict("admin_account_self_deletion_forbidden",
                    "Administrator accounts cannot be deleted through self-service");
        }
        var result = proofService.createReauthentication(current);
        return new AccountDeletionRequestResponse(result.id(), result.provider(), result.nonce(),
                result.expiresAt());
    }

    public void deleteAccount(UUID requestId, SensitiveAccountActionRequest request) {
        if (!requestId.equals(request.challengeId())) {
            throw conflict("account_deletion_request_mismatch", "Account deletion request does not match proof");
        }
        CurrentCustomer current = accountService.currentCustomer();
        var prepared = proofService.prepareReauthentication(requestId, current, request.identityToken(),
                request.telegramInitData());
        transactionTemplate.executeWithoutResult(status -> deleteVerified(current, prepared));
    }

    private void deleteVerified(CurrentCustomer current,
                                AccountProofService.PreparedReauthentication prepared) {
        CustomerAccount account = accountRepository.findByIdForUpdate(current.customerId())
                .orElseThrow(() -> conflict("account_not_found", "Account no longer exists"));
        if (account.getStatus() == CustomerAccountStatus.DELETED) return;
        if (roleRepository.existsByCustomerIdAndRole(current.customerId(), PlatformRole.ADMIN)) {
            throw conflict("admin_account_self_deletion_forbidden",
                    "Administrator accounts cannot be deleted through self-service");
        }
        proofService.consumePreparedReauthentication(prepared, current);
        deletionParticipants.forEach(participant -> participant.verifyCancellable(current.customerId()));
        deletionParticipants.forEach(participant -> participant.cancel(current.customerId()));

        long customerId = current.customerId();
        entityManager.flush();
        anonymizeRetainedOperations(customerId);
        appleCredentialLifecycleService.scheduleCustomerRevocation(customerId);
        sessionTokenService.revokeAll(customerId, "ACCOUNT_DELETED");
        jdbcTemplate.update("delete from notification_delivery where endpoint_id in "
                + "(select id from communication_endpoint where customer_id = ?)", customerId);
        jdbcTemplate.update("delete from communication_endpoint where customer_id = ?", customerId);
        jdbcTemplate.update("delete from customer_notification_preference where customer_id = ?", customerId);
        jdbcTemplate.update("delete from customer_notification where customer_id = ?", customerId);
        jdbcTemplate.update("delete from customer_identity_link_request where target_customer_id = ?", customerId);
        jdbcTemplate.update("delete from authentication_challenge where customer_id = ?", customerId);
        jdbcTemplate.update("delete from idempotency_record where customer_id = ?", customerId);
        jdbcTemplate.update("delete from customer_session where customer_id = ?", customerId);
        roleRepository.deleteAllByCustomerId(customerId);
        identityRepository.deleteAll(identityRepository.findAllByCustomerIdOrderByProvider(customerId));
        account.anonymize(clock.instant());
        auditService.success(customerId, "ACCOUNT_DELETED", current.provider(), current.externalIdentityId());
    }

    private void anonymizeRetainedOperations(long customerId) {
        jdbcTemplate.update("""
                update cleaning_order
                   set customer_name = 'Deleted customer', phone = 'deleted', address = 'deleted',
                       customer_comment = null
                 where customer_id = ?
                """, customerId);
        jdbcTemplate.update("""
                update rental_booking
                   set customer_name = 'Deleted customer', phone = 'deleted', comment = null
                 where customer_id = ?
                """, customerId);
        jdbcTemplate.update("""
                update transfer_booking
                   set customer_name_snapshot = 'Deleted customer', customer_phone_snapshot = 'deleted',
                       address = 'deleted', flight_number = null, comment = null
                 where customer_id = ?
                """, customerId);
        jdbcTemplate.update("update support_case set description = null where customer_id = ?", customerId);
        jdbcTemplate.update("update transaction_feedback set comment = null where customer_id = ?", customerId);
    }

    private AccountIdentitiesResponse identities(long customerId) {
        return new AccountIdentitiesResponse(identityRepository.findAllByCustomerIdOrderByProvider(customerId)
                .stream().map(identity -> new AccountIdentityResponse(identity.getId(), identity.getProvider(),
                        identity.getIssuer(), true,
                        identity.getProvider() == ExternalIdentityProvider.TELEGRAM
                                ? identity.getUsername() : null,
                        identity.isWriteAccessAllowed())).toList());
    }

    private static AccountSecurityException conflict(String code, String message) {
        return new AccountSecurityException(code, message);
    }
}
