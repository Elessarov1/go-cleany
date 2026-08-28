package com.cleany.notification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

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
    public CustomerNotificationPageResponse current(int page, int size) {
        long customerId = customerAccountService.currentCustomer().customerId();
        long total = jdbcTemplate.queryForObject(
                "select count(*) from customer_notification where customer_id = ?",
                Long.class,
                customerId
        );
        List<CustomerNotificationResponse> content = jdbcTemplate.query("""
                select id, type, target_path, created_at, read_at
                  from customer_notification
                 where customer_id = ?
                 order by created_at desc, id desc
                 limit ? offset ?
                """, CustomerNotificationInboxService::map, customerId, size, (long) page * size);
        int totalPages = total == 0 ? 0 : Math.toIntExact((total + size - 1) / size);
        return new CustomerNotificationPageResponse(content, page, size, total, totalPages);
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
        return new CustomerNotificationResponse(
                resultSet.getLong("id"),
                CustomerNotificationType.valueOf(resultSet.getString("type")),
                resultSet.getString("target_path"),
                resultSet.getObject("created_at", OffsetDateTime.class).toInstant(),
                readAt
        );
    }
}
