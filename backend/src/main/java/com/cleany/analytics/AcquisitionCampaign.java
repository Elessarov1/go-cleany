package com.cleany.analytics;

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
@Table(name = "acquisition_campaign")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AcquisitionCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_code", nullable = false, length = 60, updatable = false)
    private String publicCode;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 32)
    private AcquisitionChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "medium", nullable = false, length = 32)
    private AcquisitionMedium medium;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_service", nullable = false, length = 32)
    private AcquisitionTargetService targetService;

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "disabled_at")
    private Instant disabledAt;

    public AcquisitionCampaign(
            String publicCode,
            String name,
            AcquisitionChannel channel,
            AcquisitionMedium medium,
            AcquisitionTargetService targetService,
            Long partnerId,
            Instant createdAt
    ) {
        this.publicCode = Objects.requireNonNull(publicCode, "publicCode");
        this.name = Objects.requireNonNull(name, "name");
        this.channel = Objects.requireNonNull(channel, "channel");
        this.medium = Objects.requireNonNull(medium, "medium");
        this.targetService = Objects.requireNonNull(targetService, "targetService");
        this.partnerId = partnerId;
        this.active = true;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public void update(
            String name,
            AcquisitionChannel channel,
            AcquisitionMedium medium,
            AcquisitionTargetService targetService,
            Long partnerId,
            boolean active,
            Instant changedAt
    ) {
        this.name = Objects.requireNonNull(name, "name");
        this.channel = Objects.requireNonNull(channel, "channel");
        this.medium = Objects.requireNonNull(medium, "medium");
        this.targetService = Objects.requireNonNull(targetService, "targetService");
        this.partnerId = partnerId;
        if (this.active != active) {
            this.active = active;
            this.disabledAt = active ? null : Objects.requireNonNull(changedAt, "changedAt");
        }
    }
}
