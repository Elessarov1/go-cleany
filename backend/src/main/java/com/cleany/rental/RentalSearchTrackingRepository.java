package com.cleany.rental;

import java.time.Instant;
import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RentalSearchTrackingRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean exists(UUID executionId) {
        Boolean exists = jdbcTemplate.queryForObject(
                "select exists (select 1 from rental_search_execution where id = ?)",
                Boolean.class,
                executionId
        );
        return Boolean.TRUE.equals(exists);
    }

    public boolean isCompatible(UUID executionId, RentalTermType termType) {
        Boolean exists = jdbcTemplate.queryForObject(
                """
                select exists (
                    select 1
                      from rental_search_execution
                     where id = ?
                       and mode in (?, 'BROWSE_ALL')
                )
                """,
                Boolean.class,
                executionId,
                termType.name()
        );
        return Boolean.TRUE.equals(exists);
    }

    public void insertExecution(
            UUID executionId,
            RentalSearchMode mode,
            int resultCount,
            long apiDurationMs,
            UUID previousSearchId,
            Instant createdAt
    ) {
        jdbcTemplate.update(
                """
                insert into rental_search_execution (
                    id, mode, result_count, api_duration_ms, previous_search_id, created_at
                ) values (?, ?, ?, ?, ?, ?)
                on conflict (id) do nothing
                """,
                executionId,
                mode.name(),
                resultCount,
                apiDurationMs,
                previousSearchId,
                Timestamp.from(createdAt)
        );
    }

    public void insertEvent(
            UUID executionId,
            RentalSearchEventType eventType,
            Long durationMs,
            Instant occurredAt
    ) {
        jdbcTemplate.update(
                """
                insert into rental_search_event (
                    search_execution_id, event_type, duration_ms, occurred_at
                )
                select ?, ?, ?, ?
                 where exists (
                     select 1 from rental_search_execution where id = ?
                 )
                on conflict (search_execution_id, event_type) do nothing
                """,
                executionId,
                eventType.name(),
                durationMs,
                Timestamp.from(occurredAt),
                executionId
        );
    }
}
