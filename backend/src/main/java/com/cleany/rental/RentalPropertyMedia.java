package com.cleany.rental;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rental_property_media")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalPropertyMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private RentalProperty property;

    @Column(name = "media_asset_id", nullable = false)
    private long mediaAssetId;

    @Column(name = "card_media_asset_id")
    private Long cardMediaAssetId;

    @Column(name = "thumbnail_media_asset_id")
    private Long thumbnailMediaAssetId;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "is_cover", nullable = false)
    private boolean cover;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public RentalPropertyMedia(
            RentalProperty property,
            long mediaAssetId,
            long cardMediaAssetId,
            long thumbnailMediaAssetId,
            int sortOrder,
            boolean cover,
            Instant createdAt
    ) {
        this.property = Objects.requireNonNull(property, "property");
        if (mediaAssetId <= 0) {
            throw new IllegalArgumentException("mediaAssetId must be positive");
        }
        if (sortOrder < 0) {
            throw new IllegalArgumentException("sortOrder must not be negative");
        }
        this.mediaAssetId = mediaAssetId;
        if (cardMediaAssetId <= 0 || thumbnailMediaAssetId <= 0) {
            throw new IllegalArgumentException("Responsive media asset ids must be positive");
        }
        this.cardMediaAssetId = cardMediaAssetId;
        this.thumbnailMediaAssetId = thumbnailMediaAssetId;
        this.sortOrder = sortOrder;
        this.cover = cover;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    long mediaAssetId(RentalMediaVariant variant) {
        return switch (Objects.requireNonNull(variant, "variant")) {
            case FULL -> mediaAssetId;
            case CARD -> cardMediaAssetId == null ? mediaAssetId : cardMediaAssetId;
            case THUMBNAIL -> thumbnailMediaAssetId == null ? mediaAssetId : thumbnailMediaAssetId;
        };
    }

    void attachMissingVariants(Long cardAssetId, Long thumbnailAssetId) {
        if (cardAssetId != null) {
            if (cardAssetId <= 0) {
                throw new IllegalArgumentException("cardAssetId must be positive");
            }
            if (cardMediaAssetId != null) {
                throw new IllegalStateException("Card media variant is already assigned");
            }
            cardMediaAssetId = cardAssetId;
        }
        if (thumbnailAssetId != null) {
            if (thumbnailAssetId <= 0) {
                throw new IllegalArgumentException("thumbnailAssetId must be positive");
            }
            if (thumbnailMediaAssetId != null) {
                throw new IllegalStateException("Thumbnail media variant is already assigned");
            }
            thumbnailMediaAssetId = thumbnailAssetId;
        }
    }

    public void setCover(boolean cover) {
        this.cover = cover;
    }

    public void reorder(int sortOrder) {
        if (sortOrder < 0) {
            throw new IllegalArgumentException("sortOrder must not be negative");
        }
        this.sortOrder = sortOrder;
    }
}
