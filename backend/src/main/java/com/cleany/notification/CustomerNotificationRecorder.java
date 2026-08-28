package com.cleany.notification;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerNotificationRecorder {

    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean record(long customerId, CustomerNotification notification) {
        Objects.requireNonNull(notification, "notification");
        String targetPath = requireLocalPath(notification.targetPath());
        int inserted = jdbcTemplate.update("""
                insert into customer_notification (
                    customer_id, type, target_path, dedup_key, created_at
                ) values (?, ?, ?, ?, ?)
                on conflict (customer_id, dedup_key) do nothing
                """,
                customerId,
                notification.type().name(),
                targetPath,
                notification.deduplicationKey(),
                clock.instant().atOffset(ZoneOffset.UTC)
        );
        return inserted == 1;
    }

    private static String requireLocalPath(String value) {
        if (value == null || !value.startsWith("/") || value.startsWith("//") || value.contains("://")) {
            throw new IllegalArgumentException("Notification target must be a safe local path");
        }
        return value;
    }
}
