package com.cleany.notification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import com.cleany.action.ActionType;
import com.cleany.catalog.PlatformService;
import com.cleany.pagination.CursorPageResponse;
import com.cleany.pagination.OpaqueCursorPagination;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.customer.CustomerAccountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerNotificationInboxService {

    private final CustomerAccountService customerAccountService;
    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    @Transactional(readOnly = true)
    public CursorPageResponse<CustomerNotificationResponse> current(String cursor, Integer requestedSize) {
        long customerId = customerAccountService.currentCustomer().customerId();
        int size = requestedSize == null ? OpaqueCursorPagination.DEFAULT_SIZE : requestedSize;
        if (size < 1 || size > OpaqueCursorPagination.MAXIMUM_SIZE) {
            throw new com.cleany.pagination.InvalidCursorException();
        }
        OpaqueCursorPagination.CursorKey key = OpaqueCursorPagination.decode(cursor);
        List<CustomerNotificationResponse> content = key == null
                ? jdbcTemplate.query("""
                select id, type, target_path, created_at, read_at,
                       action_type, action_service, action_entity_id, action_context
                  from customer_notification
                 where customer_id = ?
                 order by created_at desc, id desc
                 limit ?
                """, CustomerNotificationInboxService::map, customerId, size + 1)
                : jdbcTemplate.query("""
                select id, type, target_path, created_at, read_at,
                       action_type, action_service, action_entity_id, action_context
                  from customer_notification
                 where customer_id = ?
                   and (created_at < ? or (created_at = ? and id < ?))
                 order by created_at desc, id desc
                 limit ?
                """, CustomerNotificationInboxService::map, customerId,
                        key.createdAt().atOffset(ZoneOffset.UTC), key.createdAt().atOffset(ZoneOffset.UTC),
                        key.id(), size + 1);
        boolean hasMore = content.size() > size;
        List<CustomerNotificationResponse> items = hasMore ? content.subList(0, size) : content;
        CustomerNotificationResponse last = items.isEmpty() ? null : items.getLast();
        String nextCursor = hasMore && last != null
                ? OpaqueCursorPagination.encode(last.createdAt(), last.id()) : null;
        return new CursorPageResponse<>(items, nextCursor, hasMore);
    }

    @Transactional(readOnly = true)
    public CustomerNotificationUnreadCountResponse unreadCount() {
        long customerId = customerAccountService.currentCustomer().customerId();
        long count = jdbcTemplate.queryForObject("""
                select count(*) from customer_notification
                 where customer_id = ? and read_at is null
                """, Long.class, customerId);
        return new CustomerNotificationUnreadCountResponse(count);
    }

    @Transactional
    public void markRead(long notificationId) {
        long customerId = customerAccountService.currentCustomer().customerId();
        int updated = jdbcTemplate.update("""
                update customer_notification
                   set read_at = coalesce(read_at, ?)
                 where id = ? and customer_id = ?
                """, clock.instant().atOffset(ZoneOffset.UTC), notificationId, customerId);
        if (updated == 0) {
            throw new CustomerNotificationNotFoundException(notificationId);
        }
    }

    @Transactional
    public void markAllRead() {
        long customerId = customerAccountService.currentCustomer().customerId();
        jdbcTemplate.update("""
                update customer_notification set read_at = ?
                 where customer_id = ? and read_at is null
                """, clock.instant().atOffset(ZoneOffset.UTC), customerId);
    }

    private static CustomerNotificationResponse map(ResultSet resultSet, int rowNumber) throws SQLException {
        OffsetDateTime readAtValue = resultSet.getObject("read_at", OffsetDateTime.class);
        Instant readAt = readAtValue == null ? null : readAtValue.toInstant();
        String actionType = resultSet.getString("action_type");
        if (actionType == null) {
            throw new IllegalStateException("Notification has no typed action: " + resultSet.getLong("id"));
        }
        String service = resultSet.getString("action_service");
        Long entityId = resultSet.getObject("action_entity_id", Long.class);
        return new CustomerNotificationResponse(
                resultSet.getLong("id"),
                CustomerNotificationType.valueOf(resultSet.getString("type")),
                new StoredActionTarget(
                        ActionType.valueOf(actionType),
                        service == null ? null : PlatformService.valueOf(service),
                        entityId,
                        resultSet.getString("action_context")
                ).toAction(),
                resultSet.getObject("created_at", OffsetDateTime.class).toInstant(),
                readAt
        );
    }
}
