package com.cleany.order;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cleaning_order_issue_report")
public class CleaningOrderIssueReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private CleaningOrder order;

    @Column(name = "cleaner_telegram_user_id", nullable = false)
    private long cleanerTelegramUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 40)
    private OnsiteIssueReason reason;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "input_active", nullable = false)
    private boolean inputActive;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "resolved_by")
    private Long resolvedBy;

    @Column(name = "resolution_comment", length = 1000)
    private String resolutionComment;

    protected CleaningOrderIssueReport() {
    }

    public CleaningOrderIssueReport(
            CleaningOrder order,
            long cleanerTelegramUserId,
            OnsiteIssueReason reason,
            Instant createdAt
    ) {
        this.order = Objects.requireNonNull(order);
        this.cleanerTelegramUserId = cleanerTelegramUserId;
        this.reason = Objects.requireNonNull(reason);
        this.inputActive = true;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    void selectReason(OnsiteIssueReason reason) {
        requireDraft();
        this.reason = Objects.requireNonNull(reason);
        inputActive = true;
    }

    void updateComment(String comment) {
        requireDraft();
        this.comment = Objects.requireNonNull(comment);
    }

    void deactivateInput() {
        inputActive = false;
    }

    void submit(Instant submittedAt) {
        requireDraft();
        this.submittedAt = Objects.requireNonNull(submittedAt);
        inputActive = false;
    }

    void resolve(long adminTelegramUserId, String comment, Instant resolvedAt) {
        if (submittedAt == null || this.resolvedAt != null) {
            throw new IllegalStateException("Only an unresolved submitted onsite issue can be resolved");
        }
        this.resolvedBy = adminTelegramUserId;
        this.resolutionComment = Objects.requireNonNull(comment);
        this.resolvedAt = Objects.requireNonNull(resolvedAt);
    }

    private void requireDraft() {
        if (submittedAt != null) {
            throw new IllegalStateException("Submitted onsite issue report cannot be edited");
        }
    }

    public Long getId() {
        return id;
    }

    public CleaningOrder getOrder() {
        return order;
    }

    public long getCleanerTelegramUserId() {
        return cleanerTelegramUserId;
    }

    public OnsiteIssueReason getReason() {
        return reason;
    }

    public String getComment() {
        return comment;
    }

    public boolean isInputActive() {
        return inputActive;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public Long getResolvedBy() {
        return resolvedBy;
    }

    public String getResolutionComment() {
        return resolutionComment;
    }
}
