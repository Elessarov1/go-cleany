package com.cleany.catalog;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "platform_service_state")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlatformServiceState {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "service", nullable = false, length = 32)
    private PlatformService service;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private PlatformServiceStatus status;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by_customer_id")
    private Long updatedByCustomerId;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    public PlatformServiceState(
            PlatformService service,
            PlatformServiceStatus status,
            Instant updatedAt
    ) {
        this.service = Objects.requireNonNull(service, "service");
        this.status = Objects.requireNonNull(status, "status");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public void changeStatus(
            PlatformServiceStatus status,
            long updatedByCustomerId,
            Instant updatedAt
    ) {
        if (updatedByCustomerId <= 0) {
            throw new IllegalArgumentException("updatedByCustomerId must be positive");
        }
        this.status = Objects.requireNonNull(status, "status");
        this.updatedByCustomerId = updatedByCustomerId;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }
}
