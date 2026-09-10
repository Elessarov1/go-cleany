package com.cleany.authentication;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

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
@Table(name = "external_provider_job")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class ExternalProviderJob {
    private static final List<Duration> RETRIES = List.of(Duration.ofMinutes(1),
            Duration.ofMinutes(5), Duration.ofMinutes(30), Duration.ofHours(2), Duration.ofHours(8));
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 32) private String provider;
    @Column(name = "job_type", nullable = false, length = 64) private String jobType;
    @Column(name = "payload_ciphertext", nullable = false) private String payloadCiphertext;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private ExternalProviderJobStatus status;
    @Column(name = "attempt_count", nullable = false) private int attemptCount;
    @Column(name = "available_at", nullable = false) private Instant availableAt;
    @Column(name = "lease_until") private Instant leaseUntil;
    @Column(name = "last_error_code", length = 64) private String lastErrorCode;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    ExternalProviderJob(String provider, String jobType, String payloadCiphertext, Instant now) {
        this.provider = provider;
        this.jobType = jobType;
        this.payloadCiphertext = payloadCiphertext;
        this.status = ExternalProviderJobStatus.PENDING;
        this.availableAt = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    void claim(Instant now) {
        status = ExternalProviderJobStatus.PROCESSING;
        attemptCount++;
        leaseUntil = now.plusSeconds(120);
        updatedAt = now;
    }

    void delivered(Instant now) {
        status = ExternalProviderJobStatus.DELIVERED;
        leaseUntil = null;
        updatedAt = now;
    }

    void failed(Instant now, String code, boolean retryable) {
        lastErrorCode = code;
        leaseUntil = null;
        updatedAt = now;
        if (!retryable || attemptCount > RETRIES.size()) {
            status = ExternalProviderJobStatus.DEAD;
        } else {
            status = ExternalProviderJobStatus.RETRY;
            availableAt = now.plus(RETRIES.get(attemptCount - 1));
        }
    }
}
