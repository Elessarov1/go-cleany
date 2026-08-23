package com.cleany.rental;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rental_admin_notification_preference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalAdminNotificationPreference {

    @Id
    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "telegram_enabled", nullable = false)
    private boolean telegramEnabled;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public RentalAdminNotificationPreference(
            long adminId,
            boolean telegramEnabled,
            Instant updatedAt
    ) {
        if (adminId <= 0) {
            throw new IllegalArgumentException("adminId must be positive");
        }
        this.adminId = adminId;
        update(telegramEnabled, updatedAt);
    }

    public void update(boolean telegramEnabled, Instant updatedAt) {
        this.telegramEnabled = telegramEnabled;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }
}
