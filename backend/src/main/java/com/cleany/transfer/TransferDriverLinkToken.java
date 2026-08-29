package com.cleany.transfer;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transfer_driver_link_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferDriverLinkToken {

    @Id
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private TransferDriver driver;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    public TransferDriverLinkToken(
            String tokenHash,
            TransferDriver driver,
            Instant createdAt,
            Instant expiresAt
    ) {
        this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash");
        this.driver = Objects.requireNonNull(driver, "driver");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        if (!expiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("Driver link token expiry must be after creation");
        }
    }

    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }

    public void consume(Instant consumedAt) {
        if (this.consumedAt != null) {
            throw new TransferDriverLinkException("Driver link has already been used");
        }
        this.consumedAt = Objects.requireNonNull(consumedAt, "consumedAt");
    }
}
