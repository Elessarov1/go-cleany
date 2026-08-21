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

import com.cleany.media.MediaAsset;

@Entity
@Table(name = "cleaning_order_issue_photo")
public class CleaningOrderIssuePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issue_report_id", nullable = false)
    private CleaningOrderIssueReport issueReport;

    @Column(name = "media_asset_id", nullable = false)
    private long mediaAssetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "media_asset_id",
            insertable = false,
            updatable = false
    )
    private MediaAsset mediaAsset;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CleaningOrderIssuePhoto() {
    }

    public CleaningOrderIssuePhoto(
            CleaningOrderIssueReport issueReport,
            long mediaAssetId,
            Instant createdAt
    ) {
        this.issueReport = Objects.requireNonNull(issueReport);
        if (mediaAssetId <= 0) {
            throw new IllegalArgumentException("mediaAssetId must be positive");
        }
        this.mediaAssetId = mediaAssetId;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public Long getId() {
        return id;
    }

    public CleaningOrderIssueReport getIssueReport() {
        return issueReport;
    }

    public long getMediaAssetId() {
        return mediaAssetId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
