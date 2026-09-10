package com.cleany.communication;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_notification_preference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class CustomerNotificationPreference {
    @Id @Column(name = "customer_id") private long customerId;
    @Column(name = "telegram_enabled", nullable = false) private boolean telegramEnabled;
    @Column(name = "push_enabled", nullable = false) private boolean pushEnabled;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    CustomerNotificationPreference(long customerId, Instant now) {
        this.customerId = customerId;
        this.telegramEnabled = true;
        this.pushEnabled = true;
        this.updatedAt = now;
    }

    void update(Boolean telegram, Boolean push, Instant now) {
        if (telegram != null) telegramEnabled = telegram;
        if (push != null) pushEnabled = push;
        updatedAt = now;
    }
}
