package com.cleany.idempotency;

import java.time.Instant;

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
@Table(name = "idempotency_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class IdempotencyRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "customer_id", nullable = false) private long customerId;
    @Column(nullable = false, length = 64) private String operation;
    @Column(name = "key_hash", nullable = false, length = 64) private String keyHash;
    @Column(name = "payload_hash", nullable = false, length = 64) private String payloadHash;
    @Column(name = "resource_type", length = 32) private String resourceType;
    @Column(name = "resource_id") private Long resourceId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private IdempotencyStatus status;
    @Column(name = "response_status") private Integer responseStatus;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "expires_at", nullable = false) private Instant expiresAt;

    IdempotencyRecord(long customerId, String operation, String keyHash, String payloadHash,
                      Instant createdAt, Instant expiresAt) {
        this.customerId = customerId;
        this.operation = operation;
        this.keyHash = keyHash;
        this.payloadHash = payloadHash;
        this.status = IdempotencyStatus.PENDING;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    void complete(String resourceType, long resourceId, int responseStatus) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.responseStatus = responseStatus;
        this.status = IdempotencyStatus.COMPLETED;
    }
}
