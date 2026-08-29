package com.cleany.analytics;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.cleany.catalog.PlatformService;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_acquisition")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerAcquisition {

    @Id
    @Column(name = "customer_id")
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 32)
    private AcquisitionChannel channel;

    @Column(name = "campaign_id")
    private Long campaignId;

    @Column(name = "first_touch_at", nullable = false)
    private Instant firstTouchAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "first_touch_service", length = 32)
    private PlatformService firstTouchService;

    @Enumerated(EnumType.STRING)
    @Column(name = "attribution_method", nullable = false, length = 32)
    private AttributionMethod attributionMethod;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public CustomerAcquisition(
            long customerId,
            AcquisitionChannel channel,
            Long campaignId,
            Instant firstTouchAt,
            PlatformService firstTouchService,
            AttributionMethod attributionMethod,
            Instant createdAt
    ) {
        this.customerId = customerId;
        this.channel = Objects.requireNonNull(channel, "channel");
        this.campaignId = campaignId;
        this.firstTouchAt = Objects.requireNonNull(firstTouchAt, "firstTouchAt");
        this.firstTouchService = firstTouchService;
        this.attributionMethod = Objects.requireNonNull(attributionMethod, "attributionMethod");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }
}
