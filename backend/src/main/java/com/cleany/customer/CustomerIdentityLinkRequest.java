package com.cleany.customer;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_identity_link_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerIdentityLinkRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "target_customer_id", nullable = false)
    private long targetCustomerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 32)
    private ExternalIdentityProvider provider;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    public CustomerIdentityLinkRequest(
            String tokenHash,
            long targetCustomerId,
            ExternalIdentityProvider provider,
            Instant createdAt,
            Instant expiresAt
    ) {
        this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash");
        if (targetCustomerId <= 0) {
            throw new IllegalArgumentException("targetCustomerId must be positive");
        }
        this.targetCustomerId = targetCustomerId;
        this.provider = Objects.requireNonNull(provider, "provider");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
    }

    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }

    public void consume(Instant now) {
        if (consumedAt != null) {
            throw new AccountLinkTokenConsumedException();
        }
        consumedAt = Objects.requireNonNull(now, "now");
    }
}
