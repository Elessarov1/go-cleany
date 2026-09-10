package com.cleany.communication;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

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
@Table(name = "operational_telegram_delivery")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class OperationalTelegramDelivery {
    private static final List<Duration> RETRY_DELAYS = List.of(
            Duration.ofMinutes(1), Duration.ofMinutes(5), Duration.ofMinutes(30),
            Duration.ofHours(2), Duration.ofHours(8));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deduplication_key", nullable = false, length = 160)
    private String deduplicationKey;

    @Column(name = "chat_id", nullable = false)
    private long chatId;

    @Column(name = "message_text", nullable = false)
    private String messageText;

    @Column(name = "keyboard_json", nullable = false)
    private String keyboardJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private NotificationDeliveryStatus status;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "available_at", nullable = false)
    private Instant availableAt;

    @Column(name = "lease_until")
    private Instant leaseUntil;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_error_code", length = 64)
    private String lastErrorCode;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    void claim(Instant now, Duration lease) {
        status = NotificationDeliveryStatus.PROCESSING;
        attemptCount++;
        leaseUntil = now.plus(lease);
        updatedAt = now;
    }

    void delivered(Instant now) {
        status = NotificationDeliveryStatus.DELIVERED;
        leaseUntil = null;
        lastErrorCode = null;
        updatedAt = now;
    }

    void failed(Instant now, String code, Duration retryAfter, boolean retryable) {
        leaseUntil = null;
        lastErrorCode = code;
        updatedAt = now;
        if (!retryable || attemptCount > RETRY_DELAYS.size() || !now.isBefore(expiresAt)) {
            status = NotificationDeliveryStatus.DEAD;
            return;
        }
        Duration policy = RETRY_DELAYS.get(attemptCount - 1);
        Duration delay = retryAfter != null && retryAfter.compareTo(policy) > 0 ? retryAfter : policy;
        availableAt = now.plus(delay);
        status = availableAt.isBefore(expiresAt)
                ? NotificationDeliveryStatus.RETRY
                : NotificationDeliveryStatus.DEAD;
    }
}
