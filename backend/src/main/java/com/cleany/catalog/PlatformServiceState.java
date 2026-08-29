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

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    public PlatformServiceState(
            PlatformService service,
            PlatformServiceStatus status,
            Instant updatedAt,
            int displayOrder
    ) {
        this.service = Objects.requireNonNull(service, "service");
        this.status = Objects.requireNonNull(status, "status");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
        this.displayOrder = requireValidDisplayOrder(displayOrder);
    }

    public void updateConfiguration(
            PlatformServiceStatus status,
            Integer displayOrder,
            long updatedByCustomerId,
            Instant updatedAt
    ) {
        if (updatedByCustomerId <= 0) {
            throw new IllegalArgumentException("updatedByCustomerId must be positive");
        }
        if (status == null && displayOrder == null) {
            throw new IllegalArgumentException("At least one platform service setting must be provided");
        }
        if (status != null) {
            this.status = status;
        }
        if (displayOrder != null) {
            this.displayOrder = requireValidDisplayOrder(displayOrder);
        }
        this.updatedByCustomerId = updatedByCustomerId;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    private static int requireValidDisplayOrder(int displayOrder) {
        if (displayOrder < 0 || displayOrder > 9999) {
            throw new IllegalArgumentException("displayOrder must be between 0 and 9999");
        }
        return displayOrder;
    }
}
