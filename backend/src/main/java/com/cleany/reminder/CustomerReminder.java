package com.cleany.reminder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.cleany.catalog.PlatformService;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_reminder")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 48)
    private CustomerReminderType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_service", nullable = false, length = 32)
    private PlatformService sourceService;

    @Column(name = "source_entity_id", nullable = false)
    private long sourceEntityId;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "cleaning_interval_days")
    private Integer cleaningIntervalDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CustomerReminderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "notified_at")
    private Instant notifiedAt;

    public CustomerReminder(
            long customerId,
            CustomerReminderType type,
            long sourceEntityId,
            LocalDate scheduledDate,
            Integer cleaningIntervalDays,
            CustomerReminderStatus status,
            Instant createdAt
    ) {
        if (customerId <= 0 || sourceEntityId <= 0) {
            throw new IllegalArgumentException("Customer and source entity ids must be positive");
        }
        this.customerId = customerId;
        this.type = Objects.requireNonNull(type, "type");
        this.sourceService = type.sourceService();
        this.sourceEntityId = sourceEntityId;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = createdAt;
        configure(scheduledDate, cleaningIntervalDays, status, createdAt);
    }

    public void configureCleaning(
            LocalDate scheduledDate,
            Integer cleaningIntervalDays,
            CustomerReminderStatus status,
            Instant changedAt
    ) {
        if (type != CustomerReminderType.CLEANING_REPEAT || isFinal()) {
            throw new ReminderFinalStateException(sourceEntityId);
        }
        configure(scheduledDate, cleaningIntervalDays, status, changedAt);
    }

    public void markNotified(Instant at) {
        status = CustomerReminderStatus.NOTIFIED;
        notifiedAt = Objects.requireNonNull(at, "at");
        updatedAt = at;
    }

    public void markSuperseded(Instant at) {
        status = CustomerReminderStatus.SUPERSEDED;
        updatedAt = Objects.requireNonNull(at, "at");
    }

    public void markExpired(Instant at) {
        status = CustomerReminderStatus.EXPIRED;
        updatedAt = Objects.requireNonNull(at, "at");
    }

    public boolean isFinal() {
        return status == CustomerReminderStatus.NOTIFIED
                || status == CustomerReminderStatus.SUPERSEDED
                || status == CustomerReminderStatus.EXPIRED;
    }

    private void configure(
            LocalDate scheduledDate,
            Integer cleaningIntervalDays,
            CustomerReminderStatus status,
            Instant changedAt
    ) {
        CustomerReminderStatus requiredStatus = Objects.requireNonNull(status, "status");
        if (type == CustomerReminderType.CLEANING_REPEAT
                && cleaningIntervalDays != null
                && cleaningIntervalDays != 14
                && cleaningIntervalDays != 30) {
            throw new IllegalArgumentException("Cleaning reminder interval must be 14 or 30 days");
        }
        if (requiredStatus == CustomerReminderStatus.DISABLED && scheduledDate != null
                || requiredStatus != CustomerReminderStatus.DISABLED && scheduledDate == null) {
            throw new IllegalArgumentException("Reminder schedule does not match status");
        }
        this.scheduledDate = scheduledDate;
        this.cleaningIntervalDays = cleaningIntervalDays;
        this.status = requiredStatus;
        this.updatedAt = Objects.requireNonNull(changedAt, "changedAt");
    }
}
