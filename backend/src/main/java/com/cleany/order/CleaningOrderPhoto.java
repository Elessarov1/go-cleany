package com.cleany.order;

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

@Entity
@Table(name = "cleaning_order_photo")
public class CleaningOrderPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private CleaningOrder order;

    @Column(name = "media_asset_id", nullable = false)
    private long mediaAssetId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CleaningOrderPhoto() {
    }

    public CleaningOrderPhoto(
            CleaningOrder order,
            long mediaAssetId,
            Instant createdAt
    ) {
        this.order = Objects.requireNonNull(order);
        if (mediaAssetId <= 0) {
            throw new IllegalArgumentException("mediaAssetId must be positive");
        }
        this.mediaAssetId = mediaAssetId;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public Long getId() {
        return id;
    }

    public CleaningOrder getOrder() {
        return order;
    }

    public long getMediaAssetId() {
        return mediaAssetId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
