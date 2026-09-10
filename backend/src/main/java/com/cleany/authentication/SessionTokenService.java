package com.cleany.authentication;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountStatus;
import com.cleany.customer.CustomerExternalIdentity;
import com.cleany.customer.CustomerExternalIdentityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionTokenService {
    private static final String ACCESS_PREFIX = "lp_at_";
    private static final String REFRESH_PREFIX = "lp_rt_";

    private final CustomerSessionRepository sessionRepository;
    private final UsedRefreshTokenRepository usedRefreshTokenRepository;
    private final CustomerExternalIdentityRepository identityRepository;
    private final CustomerAccountRepository accountRepository;
    private final SessionTokenCrypto crypto;
    private final NativeAuthProperties properties;
    private final Clock clock;

    @Transactional
    public SessionTokensResponse issue(CurrentCustomer customer, NativeClientType clientType) {
        requireEnabled();
        Instant now = clock.instant();
        String access = crypto.newToken(ACCESS_PREFIX);
        String refresh = crypto.newToken(REFRESH_PREFIX);
        Instant accessExpiry = now.plus(properties.accessTtl());
        Instant refreshExpiry = now.plus(properties.refreshTtl());
        Instant absoluteExpiry = now.plus(properties.absoluteTtl());
        CustomerSession session = sessionRepository.save(new CustomerSession(
                UUID.randomUUID(), customer.customerId(), customer.externalIdentityId(), clientType,
                crypto.hash(access), crypto.hash(refresh), now, accessExpiry,
                refreshExpiry, absoluteExpiry
        ));
        return response(session.getId(), access, accessExpiry, refresh,
                refreshExpiry.isBefore(absoluteExpiry) ? refreshExpiry : absoluteExpiry, absoluteExpiry);
    }

    @Transactional
    AuthenticatedSession authenticate(String accessToken) {
        Instant now = clock.instant();
        CustomerSession session = sessionRepository.findByAccessTokenHash(crypto.hash(accessToken))
                .orElseThrow(() -> invalid("invalid_access_token", "Access token is invalid"));
        if (!session.accessValid(now) || session.getAuthenticationIdentityId() == null) {
            throw invalid("access_token_expired", "Access token is expired or revoked");
        }
        CustomerExternalIdentity identity = identityRepository.findById(session.getAuthenticationIdentityId())
                .orElseThrow(() -> invalid("session_identity_unavailable", "Session identity is unavailable"));
        if (identity.getCustomerId() != session.getCustomerId()) {
            session.revoke(now, "IDENTITY_ACCOUNT_CHANGED");
            throw invalid("session_identity_changed", "Session identity no longer belongs to this account");
        }
        var account = accountRepository.findById(session.getCustomerId())
                .orElseThrow(() -> invalid("session_account_unavailable", "Session account is unavailable"));
        if (account.getStatus() != CustomerAccountStatus.ACTIVE) {
            throw invalid("session_account_deleted", "Session account is deleted");
        }
        session.touch(now);
        return new AuthenticatedSession(session.getId(), identity(identity));
    }

    @Transactional
    public SessionTokensResponse refresh(String refreshToken, String idempotencyKey) {
        requireEnabled();
        requireToken(refreshToken, REFRESH_PREFIX, "refresh token");
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw invalid("refresh_idempotency_key_required", "Idempotency-Key is required");
        }
        String refreshHash = crypto.hash(refreshToken);
        String attemptHash = crypto.hash(idempotencyKey.trim());
        Instant now = clock.instant();
        CustomerSession session = sessionRepository.findByRefreshHashForUpdate(refreshHash).orElse(null);
        if (session == null) {
            UsedRefreshToken used = usedRefreshTokenRepository.findById(refreshHash).orElse(null);
            if (used != null) {
                CustomerSession usedSession = sessionRepository.findByIdForUpdate(used.getSessionId())
                        .orElseThrow(() -> invalid("refresh_token_reused", "Refresh token was already used"));
                if (usedSession.retryMatches(refreshHash, attemptHash, now)) {
                    return decode(usedSession.getRefreshResponseCiphertext());
                }
                usedSession.revoke(now, "REFRESH_REUSE_DETECTED");
                throw invalid("refresh_token_reused", "Refresh token was already used");
            }
            throw invalid("invalid_refresh_token", "Refresh token is invalid");
        }
        if (session.retryMatches(refreshHash, attemptHash, now)) {
            return decode(session.getRefreshResponseCiphertext());
        }
        if (!session.currentRefreshMatches(refreshHash)) {
            session.revoke(now, "REFRESH_REUSE_DETECTED");
            throw invalid("refresh_token_reused", "Refresh token was already used");
        }
        if (!session.refreshValid(now)) {
            session.revoke(now, "REFRESH_EXPIRED_OR_REUSED");
            throw invalid("refresh_token_reused", "Refresh token is expired, revoked, or reused");
        }

        String access = crypto.newToken(ACCESS_PREFIX);
        String refresh = crypto.newToken(REFRESH_PREFIX);
        Instant accessExpiry = now.plus(properties.accessTtl());
        Instant refreshExpiry = now.plus(properties.refreshTtl());
        Instant absoluteExpiry = sessionAbsoluteExpiry(session, refreshExpiry);
        SessionTokensResponse response = response(session.getId(), access, accessExpiry, refresh,
                absoluteExpiry, session.getAbsoluteExpiresAt());
        usedRefreshTokenRepository.save(new UsedRefreshToken(
                session.getRefreshTokenHash(), session.getId(), now));
        session.rotate(crypto.hash(access), crypto.hash(refresh), attemptHash,
                crypto.encrypt(encode(response)), now, accessExpiry, refreshExpiry,
                now.plus(properties.refreshRetryTtl()));
        return response;
    }

    @Transactional
    public void revoke(UUID sessionId, long customerId, String reason) {
        sessionRepository.findById(sessionId)
                .filter(session -> session.getCustomerId() == customerId)
                .ifPresent(session -> session.revoke(clock.instant(), reason));
    }

    @Transactional
    public void revokeAll(long customerId, String reason) {
        sessionRepository.revokeAll(customerId, clock.instant(), reason);
    }

    @Transactional
    public void revokeByIdentity(long identityId, String reason) {
        sessionRepository.revokeByIdentity(identityId, clock.instant(), reason);
    }

    private Instant sessionAbsoluteExpiry(CustomerSession session, Instant candidate) {
        return candidate.isBefore(session.getAbsoluteExpiresAt())
                ? candidate
                : session.getAbsoluteExpiresAt();
    }

    private AuthenticatedCustomerIdentity identity(CustomerExternalIdentity identity) {
        return new AuthenticatedCustomerIdentity(identity.getProvider(), identity.getIssuer(),
                identity.getExternalSubject(), identity.getUsername(), identity.getDisplayName(),
                identity.getLanguageCode(), identity.getEmail(), identity.isEmailVerified(),
                identity.isWriteAccessAllowed());
    }

    private static SessionTokensResponse response(UUID id, String access, Instant accessExpiry,
                                                  String refresh, Instant refreshExpiry,
                                                  Instant absoluteExpiry) {
        return new SessionTokensResponse(id, "Bearer", access, accessExpiry, refresh, refreshExpiry,
                absoluteExpiry);
    }

    private String encode(SessionTokensResponse response) {
        return String.join("\n", response.sessionId().toString(), response.accessToken(),
                response.accessExpiresAt().toString(), response.refreshToken(),
                response.refreshExpiresAt().toString(), response.absoluteExpiresAt().toString());
    }

    private SessionTokensResponse decode(String encoded) {
        String[] parts = crypto.decrypt(encoded).split("\\n", -1);
        if (parts.length != 6) throw new IllegalStateException("Stored refresh response is invalid");
        return response(UUID.fromString(parts[0]), parts[1], Instant.parse(parts[2]),
                parts[3], Instant.parse(parts[4]), Instant.parse(parts[5]));
    }

    private void requireEnabled() {
        if (!properties.enabled()) throw invalid("native_auth_unavailable", "Native authentication is unavailable");
    }

    private static void requireToken(String token, String prefix, String name) {
        if (token == null || !token.startsWith(prefix) || token.length() < prefix.length() + 32) {
            throw invalid("invalid_" + name.replace(' ', '_'), name + " is invalid");
        }
    }

    private static SessionTokenException invalid(String code, String message) {
        return new SessionTokenException(code, message);
    }

    record AuthenticatedSession(UUID sessionId, AuthenticatedCustomerIdentity identity) {
    }
}
