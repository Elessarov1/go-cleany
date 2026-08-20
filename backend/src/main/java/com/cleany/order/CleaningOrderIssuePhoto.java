package com.cleany.order;

import java.time.Instant;
import java.util.Arrays;
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
@Table(name = "cleaning_order_issue_photo")
public class CleaningOrderIssuePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "issue_report_id", nullable = false)
    private CleaningOrderIssueReport issueReport;

    @Column(name = "telegram_file_id", nullable = false, length = 512)
    private String telegramFileId;

    @Column(name = "telegram_file_unique_id", nullable = false, length = 255)
    private String telegramFileUniqueId;

    @Column(name = "content", nullable = false, columnDefinition = "bytea")
    private byte[] content;

    @Column(name = "content_type", nullable = false, length = 64)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "sha256", nullable = false, length = 64)
    private String sha256;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CleaningOrderIssuePhoto() {
    }

    public CleaningOrderIssuePhoto(
            CleaningOrderIssueReport issueReport,
            String telegramFileId,
            String telegramFileUniqueId,
            byte[] content,
            String contentType,
            String sha256,
            Instant createdAt
    ) {
        this.issueReport = Objects.requireNonNull(issueReport);
        this.telegramFileId = Objects.requireNonNull(telegramFileId);
        this.telegramFileUniqueId = Objects.requireNonNull(telegramFileUniqueId);
        this.content = Arrays.copyOf(Objects.requireNonNull(content), content.length);
        this.contentType = Objects.requireNonNull(contentType);
        this.sizeBytes = content.length;
        this.sha256 = Objects.requireNonNull(sha256);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public Long getId() {
        return id;
    }

    public CleaningOrderIssueReport getIssueReport() {
        return issueReport;
    }

    public String getTelegramFileId() {
        return telegramFileId;
    }

    public String getTelegramFileUniqueId() {
        return telegramFileUniqueId;
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length);
    }

    public String getContentType() {
        return contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getSha256() {
        return sha256;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
