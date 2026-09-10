package com.cleany.notification;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.cleany.action.ActionTargetWebPathResolver;

@Service
@RequiredArgsConstructor
public class CustomerNotificationRecorder {

    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;
    private final ObjectMapper objectMapper;

    @Transactional
    public boolean record(long customerId, CustomerNotification notification) {
        return recordId(customerId, notification) != null;
    }

    @Transactional
    public Long recordId(long customerId, CustomerNotification notification) {
        Objects.requireNonNull(notification, "notification");
        String targetPath = requireLocalPath(ActionTargetWebPathResolver.resolve(notification.action()));
        StoredActionTarget action = StoredActionTarget.from(notification.action());
        String payload;
        try {
            payload = objectMapper.writeValueAsString(notification);
        } catch (JacksonException exception) {
            throw new IllegalArgumentException("Notification payload cannot be serialized", exception);
        }
        var ids = jdbcTemplate.queryForList("""
                insert into customer_notification (
                    customer_id, type, target_path, dedup_key, created_at,
                    action_type, action_service, action_entity_id, action_context, payload_json
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (customer_id, dedup_key) do nothing
                returning id
                """,
                Long.class,
                customerId,
                notification.type().name(),
                targetPath,
                notification.deduplicationKey(),
                clock.instant().atOffset(ZoneOffset.UTC),
                action.type().name(),
                action.service() == null ? null : action.service().name(),
                action.entityId(),
                action.context(),
                payload
        );
        return ids.isEmpty() ? null : ids.getFirst();
    }

    private static String requireLocalPath(String value) {
        if (value == null || !value.startsWith("/") || value.startsWith("//") || value.contains("://")) {
            throw new IllegalArgumentException("Notification target must be a safe local path");
        }
        return value;
    }
}
