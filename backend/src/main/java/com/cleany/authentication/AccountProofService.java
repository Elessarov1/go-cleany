package com.cleany.authentication;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.telegram.TelegramInitDataValidator;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AccountProofService {
    private final AuthenticationChallengeRepository challengeRepository;
    private final NativeIdentityTokenVerifier tokenVerifier;
    private final AppleTokenClient appleTokenClient;
    private final TelegramInitDataValidator telegramValidator;
    private final SessionTokenCrypto crypto;
    private final NativeAuthProperties properties;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;
    private final Clock clock;

    @Transactional
    public LinkAttempt createLink(CurrentCustomer current, ExternalIdentityProvider provider) {
        if (provider == null || provider == current.provider()) {
            throw error("identity_provider_already_linked", "Choose a different identity provider");
        }
        String targetNonce = crypto.newToken(provider == ExternalIdentityProvider.TELEGRAM ? "" : "lp_nonce_");
        String reauthenticationNonce = crypto.newToken("lp_reauth_");
        var now = clock.instant();
        var challenge = challengeRepository.save(new AuthenticationChallenge(
                UUID.randomUUID(), AuthenticationChallengePurpose.LINK, provider,
                current.customerId(), null, crypto.hash(targetNonce),
                crypto.hash(reauthenticationNonce), now, now.plus(properties.challengeTtl())
        ));
        String telegramDeepLink = provider == ExternalIdentityProvider.TELEGRAM
                ? telegramDeepLink("link_" + targetNonce)
                : null;
        return new LinkAttempt(challenge.getId(), provider,
                provider == ExternalIdentityProvider.TELEGRAM ? null : targetNonce,
                reauthenticationNonce, telegramDeepLink, challenge.getExpiresAt());
    }

    public void verifyLink(UUID challengeId, CurrentCustomer current, String identityToken,
                           String authorizationCode) {
        LinkVerificationSnapshot snapshot = transactionTemplate.execute(status -> {
            var challenge = owned(challengeId, current.customerId());
            challenge.requireUsable(AuthenticationChallengePurpose.LINK, challenge.getProvider(), clock.instant());
            return new LinkVerificationSnapshot(challenge.getProvider(), challenge.getNonceHash(),
                    challenge.getStatus(), challenge.getProvedSubject());
        });
        if (snapshot == null) throw new IllegalStateException("Link verification transaction returned no result");
        if (snapshot.status() == AuthenticationChallengeStatus.PROVED) return;
        if (snapshot.provider() == ExternalIdentityProvider.TELEGRAM) {
            throw error("identity_link_waiting_for_telegram", "Confirm the link in the Telegram bot first");
        }
        if (snapshot.provider() == ExternalIdentityProvider.APPLE
                && (authorizationCode == null || authorizationCode.isBlank())) {
            throw error("apple_authorization_code_required", "Apple authorization code is required");
        }

        // Provider key discovery and Apple code exchange are external I/O and must not hold a DB transaction.
        AuthenticatedCustomerIdentity identity = tokenVerifier.verify(
                snapshot.provider(), identityToken, snapshot.nonceHash(),
                properties.sensitiveProofTtl());
        AppleTokenClient.AppleTokens appleTokens = snapshot.provider() == ExternalIdentityProvider.APPLE
                ? appleTokenClient.exchange(authorizationCode) : null;
        if (appleTokens != null) {
            if (appleTokens.id_token() == null || appleTokens.id_token().isBlank()) {
                throw error("apple_token_exchange_invalid", "Apple token exchange returned no identity token");
            }
            AuthenticatedCustomerIdentity exchanged = tokenVerifier.verify(
                    ExternalIdentityProvider.APPLE, appleTokens.id_token(), snapshot.nonceHash());
            if (!exchanged.externalSubject().equals(identity.externalSubject())) {
                throw error("apple_token_exchange_identity_mismatch",
                        "Apple authorization code belongs to another identity");
            }
        }
        transactionTemplate.executeWithoutResult(status -> {
            var challenge = owned(challengeId, current.customerId());
            challenge.requireUsable(AuthenticationChallengePurpose.LINK, identity.provider(), clock.instant());
            if (challenge.getStatus() == AuthenticationChallengeStatus.PROVED) {
                if (!identity.externalSubject().equals(challenge.getProvedSubject())) {
                    throw error("auth_challenge_replayed", "Authentication challenge already has another proof");
                }
                return;
            }
            challenge.prove(identity, write(identity), clock.instant());
            if (appleTokens != null) {
                if (appleTokens.refresh_token() == null || appleTokens.refresh_token().isBlank()) {
                    throw error("apple_refresh_token_missing",
                            "Apple token exchange returned no revocable credential");
                }
                challenge.attachProvedCredential(crypto.encrypt(appleTokens.refresh_token()));
            }
        });
    }

    public PreparedLinkConfirmation prepareLinkConfirmation(UUID challengeId, CurrentCustomer current,
                                                            String currentIdentityToken,
                                                            String telegramInitData) {
        PreparedLinkConfirmation prepared = transactionTemplate.execute(status -> {
            var challenge = owned(challengeId, current.customerId());
            requireFreshTargetProof(challenge);
            return new PreparedLinkConfirmation(challengeId, read(challenge.getProvedProfile()),
                    challenge.getProvedCredentialCiphertext(), challenge.getVerifierHash());
        });
        if (prepared == null) throw new IllegalStateException("Link confirmation transaction returned no result");
        verifyCurrent(current, prepared.verifierHash(), currentIdentityToken, telegramInitData);
        return prepared;
    }

    @Transactional
    public void consumePreparedLink(PreparedLinkConfirmation prepared, CurrentCustomer current) {
        var challenge = owned(prepared.challengeId(), current.customerId());
        requireFreshTargetProof(challenge);
        if (!prepared.identity().provider().equals(challenge.getProvider())
                || !prepared.identity().externalSubject().equals(challenge.getProvedSubject())) {
            throw error("auth_challenge_mismatch", "Authentication challenge proof changed");
        }
        challenge.consume(clock.instant());
    }

    @Transactional
    public ReauthenticationAttempt createReauthentication(CurrentCustomer current) {
        String nonce = crypto.newToken("lp_reauth_");
        var now = clock.instant();
        var challenge = challengeRepository.save(new AuthenticationChallenge(
                UUID.randomUUID(), AuthenticationChallengePurpose.DELETE_REAUTH, current.provider(),
                current.customerId(), null, crypto.hash(nonce), null, now,
                now.plus(properties.sensitiveProofTtl())
        ));
        return new ReauthenticationAttempt(challenge.getId(), current.provider(),
                current.provider() == ExternalIdentityProvider.TELEGRAM ? null : nonce,
                challenge.getExpiresAt());
    }

    public PreparedReauthentication prepareReauthentication(UUID challengeId, CurrentCustomer current,
                                                             String identityToken, String telegramInitData) {
        PreparedReauthentication prepared = transactionTemplate.execute(status -> {
            var challenge = owned(challengeId, current.customerId());
            challenge.requireUsable(AuthenticationChallengePurpose.DELETE_REAUTH,
                    current.provider(), clock.instant());
            return new PreparedReauthentication(challengeId, challenge.getNonceHash());
        });
        if (prepared == null) throw new IllegalStateException("Reauthentication transaction returned no result");
        verifyCurrent(current, prepared.nonceHash(), identityToken, telegramInitData);
        return prepared;
    }

    @Transactional
    public void consumePreparedReauthentication(PreparedReauthentication prepared, CurrentCustomer current) {
        var challenge = owned(prepared.challengeId(), current.customerId());
        challenge.requireUsable(AuthenticationChallengePurpose.DELETE_REAUTH,
                current.provider(), clock.instant());
        if (!challenge.getNonceHash().equals(prepared.nonceHash())) {
            throw error("auth_challenge_mismatch", "Authentication challenge changed");
        }
        challenge.consume(clock.instant());
    }

    private void verifyCurrent(CurrentCustomer current, String nonceHash, String identityToken,
                               String telegramInitData) {
        if (current.provider() == ExternalIdentityProvider.GOOGLE
                && (identityToken == null || identityToken.isBlank())
                && verifyFreshBrowserOidc(current)) {
            return;
        }
        AuthenticatedCustomerIdentity proof = current.provider() == ExternalIdentityProvider.TELEGRAM
                ? telegramValidator.validate(telegramInitData, properties.sensitiveProofTtl()).authenticatedIdentity()
                : tokenVerifier.verify(current.provider(), identityToken, nonceHash,
                        properties.sensitiveProofTtl());
        if (proof.provider() != current.provider()
                || !proof.externalSubject().equals(current.externalSubject())) {
            throw error("reauthentication_identity_mismatch", "Proof belongs to a different identity");
        }
    }

    private boolean verifyFreshBrowserOidc(CurrentCustomer current) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser user)) return false;
        var issuedAt = user.getIdToken().getIssuedAt();
        var now = clock.instant();
        return current.externalSubject().equals(user.getSubject())
                && issuedAt != null
                && !issuedAt.isBefore(now.minus(properties.sensitiveProofTtl()))
                && !issuedAt.isAfter(now.plusSeconds(30));
    }

    private AuthenticationChallenge owned(UUID id, long customerId) {
        AuthenticationChallenge challenge = challengeRepository.findByIdForUpdate(id)
                .orElseThrow(() -> error("account_challenge_invalid", "Account challenge is invalid"));
        if (challenge.getCustomerId() == null || challenge.getCustomerId() != customerId) {
            throw error("account_challenge_invalid", "Account challenge is invalid");
        }
        return challenge;
    }

    private void requireFreshTargetProof(AuthenticationChallenge challenge) {
        var now = clock.instant();
        challenge.requireUsable(AuthenticationChallengePurpose.LINK, challenge.getProvider(), now);
        if (challenge.getStatus() != AuthenticationChallengeStatus.PROVED
                || challenge.getProvedAt() == null
                || challenge.getProvedAt().isBefore(now.minus(properties.sensitiveProofTtl()))) {
            throw error("identity_link_target_not_verified", "The identity to link is not freshly verified");
        }
    }

    private String telegramDeepLink(String parameter) {
        if (properties.telegramBotUsername() == null) {
            throw error("telegram_link_unavailable", "Telegram linking is unavailable");
        }
        return UriComponentsBuilder.fromUriString("https://t.me/" + properties.telegramBotUsername())
                .queryParam("start", parameter).build(true).toUriString();
    }

    private String write(AuthenticatedCustomerIdentity identity) {
        try {
            return objectMapper.writeValueAsString(identity);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not persist identity proof", exception);
        }
    }

    private AuthenticatedCustomerIdentity read(String value) {
        try {
            return objectMapper.readValue(value, AuthenticatedCustomerIdentity.class);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Stored identity proof is invalid", exception);
        }
    }

    private static NativeAuthenticationException error(String code, String message) {
        return new NativeAuthenticationException(code, message);
    }

    public record LinkAttempt(UUID id, ExternalIdentityProvider provider, String nonce,
                              String reauthenticationNonce, String telegramDeepLink,
                              java.time.Instant expiresAt) {
    }

    public record ReauthenticationAttempt(UUID id, ExternalIdentityProvider provider, String nonce,
                                          java.time.Instant expiresAt) {
    }

    public record PreparedLinkConfirmation(UUID challengeId, AuthenticatedCustomerIdentity identity,
                                           String credentialCiphertext, String verifierHash) {
    }

    public record PreparedReauthentication(UUID challengeId, String nonceHash) {
    }

    private record LinkVerificationSnapshot(ExternalIdentityProvider provider, String nonceHash,
                                            AuthenticationChallengeStatus status, String provedSubject) {
    }
}
