package com.cleany.authentication;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
class NativeLoginTransactionService {
    private final AuthenticationChallengeRepository challengeRepository;
    private final NativeIdentityTokenVerifier tokenVerifier;
    private final TransactionTemplate transactionTemplate;
    private final CustomerAccountService accountService;
    private final SessionTokenService sessionTokenService;
    private final AppleIdentityCredentialRepository appleCredentialRepository;
    private final SessionTokenCrypto crypto;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    PreparedLogin prepare(ExternalIdentityProvider provider, NativeProviderLoginRequest request) {
        ChallengeSnapshot snapshot = transactionTemplate.execute(status -> {
            var now = clock.instant();
            AuthenticationChallenge challenge = challengeRepository.findByIdForUpdate(request.challengeId())
                    .orElseThrow(() -> error("auth_challenge_invalid", "Authentication challenge is invalid"));
            challenge.requireUsable(AuthenticationChallengePurpose.LOGIN, provider, now);
            return new ChallengeSnapshot(challenge.getId(), challenge.getNonceHash(),
                    challenge.getStatus() == AuthenticationChallengeStatus.PROVED
                            ? read(challenge.getProvedProfile()) : null);
        });
        if (snapshot == null) throw new IllegalStateException("Challenge transaction returned no result");
        if (snapshot.provedIdentity() != null) {
            return new PreparedLogin(snapshot.id(), snapshot.provedIdentity(), snapshot.nonceHash());
        }

        // JWT key discovery and signature validation may call the provider; keep it outside DB transactions.
        AuthenticatedCustomerIdentity identity = tokenVerifier.verify(provider, request.identityToken(),
                snapshot.nonceHash());
        return transactionTemplate.execute(status -> {
            var now = clock.instant();
            AuthenticationChallenge challenge = challengeRepository.findByIdForUpdate(snapshot.id())
                    .orElseThrow(() -> error("auth_challenge_invalid", "Authentication challenge is invalid"));
            challenge.requireUsable(AuthenticationChallengePurpose.LOGIN, provider, now);
            if (challenge.getStatus() == AuthenticationChallengeStatus.PROVED) {
                AuthenticatedCustomerIdentity existing = read(challenge.getProvedProfile());
                if (!existing.externalSubject().equals(identity.externalSubject())) {
                    throw error("auth_challenge_replayed", "Authentication challenge already has another proof");
                }
                return new PreparedLogin(challenge.getId(), existing, challenge.getNonceHash());
            }
            challenge.prove(identity, write(identity), now);
            return new PreparedLogin(challenge.getId(), identity, challenge.getNonceHash());
        });
    }

    void verifyAppleExchange(PreparedLogin prepared, AppleTokenClient.AppleTokens tokens) {
        if (tokens == null || tokens.id_token() == null || tokens.id_token().isBlank()) {
            throw error("apple_token_exchange_invalid", "Apple token exchange returned no identity token");
        }
        AuthenticatedCustomerIdentity exchanged = tokenVerifier.verify(
                ExternalIdentityProvider.APPLE, tokens.id_token(), prepared.nonceHash());
        if (!exchanged.externalSubject().equals(prepared.identity().externalSubject())) {
            throw error("apple_token_exchange_identity_mismatch",
                    "Apple authorization code belongs to another identity");
        }
    }

    @Transactional
    SessionTokensResponse finish(PreparedLogin prepared, NativeClientType clientType,
                                 AppleTokenClient.AppleTokens appleTokens) {
        var now = clock.instant();
        AuthenticationChallenge challenge = challengeRepository.findByIdForUpdate(prepared.challengeId())
                .orElseThrow(() -> error("auth_challenge_invalid", "Authentication challenge is invalid"));
        challenge.requireUsable(AuthenticationChallengePurpose.LOGIN, prepared.identity().provider(), now);
        if (challenge.getStatus() != AuthenticationChallengeStatus.PROVED
                || !prepared.identity().externalSubject().equals(challenge.getProvedSubject())) {
            throw error("auth_challenge_mismatch", "Authentication challenge proof changed");
        }
        var customer = accountService.resolveCustomer(prepared.identity());
        if (prepared.identity().provider() == ExternalIdentityProvider.APPLE) {
            if (appleTokens == null) throw error("apple_token_exchange_required", "Apple token exchange is required");
            String refreshToken = appleTokens.refresh_token();
            AppleIdentityCredential credential = appleCredentialRepository
                    .findById(customer.externalIdentityId()).orElse(null);
            if (refreshToken == null || refreshToken.isBlank()) {
                if (credential == null) {
                    throw error("apple_refresh_token_missing",
                            "Apple token exchange returned no revocable credential");
                }
            } else {
                if (credential == null) {
                    credential = new AppleIdentityCredential(customer.externalIdentityId(),
                            crypto.encrypt(refreshToken), now);
                } else {
                    credential.update(crypto.encrypt(refreshToken), now);
                }
                appleCredentialRepository.save(credential);
            }
        }
        challenge.consume(now);
        return sessionTokenService.issue(customer, clientType);
    }

    private String write(AuthenticatedCustomerIdentity identity) {
        try {
            return objectMapper.writeValueAsString(identity);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not store native login proof", exception);
        }
    }

    private AuthenticatedCustomerIdentity read(String value) {
        try {
            return objectMapper.readValue(value, AuthenticatedCustomerIdentity.class);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Stored native login proof is invalid", exception);
        }
    }

    private static NativeAuthenticationException error(String code, String message) {
        return new NativeAuthenticationException(code, message);
    }

    record PreparedLogin(UUID challengeId, AuthenticatedCustomerIdentity identity, String nonceHash) {
    }

    private record ChallengeSnapshot(UUID id, String nonceHash,
                                     AuthenticatedCustomerIdentity provedIdentity) {
    }
}
