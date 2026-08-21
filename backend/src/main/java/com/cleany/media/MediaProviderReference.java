package com.cleany.media;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "media_provider_reference",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_media_provider_reference_external_id",
                        columnNames = {"provider", "external_id"}
                ),
                @UniqueConstraint(
                        name = "uq_media_provider_reference_external_unique",
                        columnNames = {"provider", "external_unique_id"}
                )
        },
        indexes = @Index(
                name = "idx_media_provider_reference_asset",
                columnList = "media_asset_id"
        )
)
public class MediaProviderReference {

    private static final int MAX_EXTERNAL_ID_LENGTH = 512;
    private static final int MAX_EXTERNAL_UNIQUE_ID_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "media_asset_id", nullable = false)
    private MediaAsset mediaAsset;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 32)
    private MediaProvider provider;

    @Column(name = "external_id", nullable = false, length = MAX_EXTERNAL_ID_LENGTH)
    private String externalId;

    @Column(name = "external_unique_id", length = MAX_EXTERNAL_UNIQUE_ID_LENGTH)
    private String externalUniqueId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected MediaProviderReference() {
    }

    public MediaProviderReference(
            MediaAsset mediaAsset,
            MediaProvider provider,
            String externalId,
            String externalUniqueId,
            Instant createdAt
    ) {
        this.mediaAsset = Objects.requireNonNull(mediaAsset, "mediaAsset");
        this.provider = Objects.requireNonNull(provider, "provider");
        this.externalId = normalizeRequired(externalId, MAX_EXTERNAL_ID_LENGTH, "externalId");
        this.externalUniqueId = normalizeOptional(
                externalUniqueId,
                MAX_EXTERNAL_UNIQUE_ID_LENGTH,
                "externalUniqueId"
        );
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public Long getId() {
        return id;
    }

    public MediaAsset getMediaAsset() {
        return mediaAsset;
    }

    public Long getMediaAssetId() {
        return mediaAsset.getId();
    }

    public MediaProvider getProvider() {
        return provider;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getExternalUniqueId() {
        return externalUniqueId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private static String normalizeRequired(String value, int maxLength, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(name + " is too long");
        }
        return normalized;
    }

    private static String normalizeOptional(String value, int maxLength, String name) {
        if (value == null) {
            return null;
        }
        return normalizeRequired(value, maxLength, name);
    }
}
