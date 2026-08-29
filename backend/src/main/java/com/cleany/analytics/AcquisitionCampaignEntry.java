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
@Table(name = "acquisition_campaign_entry")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AcquisitionCampaignEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "campaign_id", nullable = false, updatable = false)
    private Long campaignId;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 32, updatable = false)
    private AcquisitionPlatform platform;

    public AcquisitionCampaignEntry(long campaignId, Instant occurredAt, AcquisitionPlatform platform) {
        this.campaignId = campaignId;
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.platform = Objects.requireNonNull(platform, "platform");
    }
}
