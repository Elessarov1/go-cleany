package com.cleany.support;

import static com.cleany.common.text.TextValues.normalizeOptional;
import static com.cleany.common.text.TextValues.requireNonBlank;

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
import jakarta.persistence.Version;

import com.cleany.catalog.PlatformService;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "support_case")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupportCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PlatformService service;

    @Column(name = "source_entity_id", nullable = false)
    private long sourceEntityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SupportCaseCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SupportCaseStatus status;

    @Column(length = 2000)
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "resolved_by_customer_id")
    private Long resolvedByCustomerId;

    @Column(name = "resolution_comment", length = 2000)
    private String resolutionComment;

    @Version
    @Column(nullable = false)
    private long version;

    public SupportCase(
            long customerId,
            PlatformService service,
            long sourceEntityId,
            SupportCaseCategory category,
            String description,
            Instant createdAt
    ) {
        if (customerId <= 0 || sourceEntityId <= 0) {
            throw new IllegalArgumentException("Customer and source ids must be positive");
        }
        this.customerId = customerId;
        this.service = Objects.requireNonNull(service, "service");
        this.sourceEntityId = sourceEntityId;
        this.category = Objects.requireNonNull(category, "category");
        this.status = SupportCaseStatus.OPEN;
        this.description = normalizeOptional(description);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public void resolve(long adminCustomerId, String comment, Instant instant) {
        if (status != SupportCaseStatus.OPEN) {
            throw new SupportCaseStateException(id == null ? 0 : id, status);
        }
        if (adminCustomerId <= 0) {
            throw new IllegalArgumentException("Admin customer id must be positive");
        }
        status = SupportCaseStatus.RESOLVED;
        resolutionComment = requireNonBlank(
                comment,
                2000,
                detail -> new InvalidSupportRequestException("Resolution comment " + detail)
        );
        resolvedAt = Objects.requireNonNull(instant, "instant");
        resolvedByCustomerId = adminCustomerId;
    }
}
