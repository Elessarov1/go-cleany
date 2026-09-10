package com.cleany.authentication;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "used_refresh_token")
class UsedRefreshToken {
    @Id @Column(name = "token_hash", length = 64) private String tokenHash;
    @Column(name = "session_id", nullable = false) private UUID sessionId;
    @Column(name = "used_at", nullable = false) private Instant usedAt;

    protected UsedRefreshToken() {
    }

    UsedRefreshToken(String tokenHash, UUID sessionId, Instant usedAt) {
        this.tokenHash = Objects.requireNonNull(tokenHash);
        this.sessionId = Objects.requireNonNull(sessionId);
        this.usedAt = Objects.requireNonNull(usedAt);
    }

    UUID getSessionId() { return sessionId; }
}
