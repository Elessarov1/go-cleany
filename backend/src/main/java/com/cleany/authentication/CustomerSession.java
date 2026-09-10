package com.cleany.authentication;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_session")
public class CustomerSession {
    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Column(name = "authentication_identity_id")
    private Long authenticationIdentityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", nullable = false, length = 16)
    private NativeClientType clientType;

    @Column(name = "access_token_hash", nullable = false, length = 64)
    private String accessTokenHash;

    @Column(name = "refresh_token_hash", nullable = false, length = 64)
    private String refreshTokenHash;

    @Column(name = "previous_refresh_token_hash", length = 64)
    private String previousRefreshTokenHash;

    @Column(name = "refresh_attempt_hash", length = 64)
    private String refreshAttemptHash;

    @Column(name = "refresh_response_ciphertext")
    private String refreshResponseCiphertext;

    @Column(name = "refresh_retry_expires_at")
    private Instant refreshRetryExpiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "authenticated_at", nullable = false)
    private Instant authenticatedAt;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    @Column(name = "access_expires_at", nullable = false)
    private Instant accessExpiresAt;

    @Column(name = "refresh_expires_at", nullable = false)
    private Instant refreshExpiresAt;

    @Column(name = "absolute_expires_at", nullable = false)
    private Instant absoluteExpiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revoked_reason", length = 64)
    private String revokedReason;

    protected CustomerSession() {
    }

    CustomerSession(UUID id, long customerId, long identityId, NativeClientType clientType,
                    String accessHash, String refreshHash, Instant now,
                    Instant accessExpiresAt, Instant refreshExpiresAt, Instant absoluteExpiresAt) {
        this.id = Objects.requireNonNull(id);
        this.customerId = customerId;
        this.authenticationIdentityId = identityId;
        this.clientType = Objects.requireNonNull(clientType);
        this.accessTokenHash = Objects.requireNonNull(accessHash);
        this.refreshTokenHash = Objects.requireNonNull(refreshHash);
        this.createdAt = Objects.requireNonNull(now);
        this.authenticatedAt = now;
        this.lastSeenAt = now;
        this.accessExpiresAt = Objects.requireNonNull(accessExpiresAt);
        this.refreshExpiresAt = Objects.requireNonNull(refreshExpiresAt);
        this.absoluteExpiresAt = Objects.requireNonNull(absoluteExpiresAt);
    }

    boolean accessValid(Instant now) {
        return active(now) && now.isBefore(accessExpiresAt);
    }

    boolean refreshValid(Instant now) {
        return active(now) && now.isBefore(refreshExpiresAt);
    }

    boolean active(Instant now) {
        return revokedAt == null && now.isBefore(absoluteExpiresAt);
    }

    void rotate(String accessHash, String refreshHash, String attemptHash, String encryptedResponse,
                Instant now, Instant accessExpiry, Instant refreshExpiry, Instant retryExpiry) {
        previousRefreshTokenHash = refreshTokenHash;
        refreshTokenHash = refreshHash;
        accessTokenHash = accessHash;
        refreshAttemptHash = attemptHash;
        refreshResponseCiphertext = encryptedResponse;
        refreshRetryExpiresAt = retryExpiry;
        accessExpiresAt = accessExpiry;
        refreshExpiresAt = refreshExpiry.isBefore(absoluteExpiresAt) ? refreshExpiry : absoluteExpiresAt;
        lastSeenAt = now;
    }

    boolean retryMatches(String previousHash, String attemptHash, Instant now) {
        return Objects.equals(previousRefreshTokenHash, previousHash)
                && Objects.equals(refreshAttemptHash, attemptHash)
                && refreshRetryExpiresAt != null && now.isBefore(refreshRetryExpiresAt)
                && refreshResponseCiphertext != null;
    }

    boolean currentRefreshMatches(String refreshHash) {
        return Objects.equals(refreshTokenHash, refreshHash);
    }

    void touch(Instant now) {
        if (now.isAfter(lastSeenAt.plusSeconds(300))) lastSeenAt = now;
    }

    void revoke(Instant now, String reason) {
        if (revokedAt == null) {
            revokedAt = now;
            revokedReason = reason;
        }
    }

    public UUID getId() { return id; }
    public long getCustomerId() { return customerId; }
    public Long getAuthenticationIdentityId() { return authenticationIdentityId; }
    public String getRefreshResponseCiphertext() { return refreshResponseCiphertext; }
    public String getRefreshTokenHash() { return refreshTokenHash; }
    public Instant getAbsoluteExpiresAt() { return absoluteExpiresAt; }
}
