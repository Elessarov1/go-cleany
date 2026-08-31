package com.cleany.support;

import static com.cleany.common.text.TextValues.normalizeOptional;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.cleany.catalog.PlatformService;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction_feedback")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionFeedback {

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
    private FeedbackOutcome outcome;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private SupportCaseCategory category;

    @Column(length = 2000)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_case_id")
    private SupportCase supportCase;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public TransactionFeedback(
            long customerId,
            PlatformService service,
            long sourceEntityId,
            FeedbackOutcome outcome,
            SupportCaseCategory category,
            String comment,
            SupportCase supportCase,
            Instant createdAt
    ) {
        this.customerId = customerId;
        this.service = Objects.requireNonNull(service, "service");
        this.sourceEntityId = sourceEntityId;
        this.outcome = Objects.requireNonNull(outcome, "outcome");
        this.category = category;
        this.comment = normalizeOptional(comment);
        this.supportCase = supportCase;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }
}
