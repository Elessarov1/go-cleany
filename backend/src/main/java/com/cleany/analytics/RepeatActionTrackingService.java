package com.cleany.analytics;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.catalog.PlatformService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RepeatActionTrackingService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Clock clock;

    @Transactional
    public void record(
            long customerId,
            PlatformService service,
            long sourceEntityId,
            RepeatActionEventType eventType
    ) {
        if (customerId <= 0 || sourceEntityId <= 0) {
            throw new IllegalArgumentException("Repeat action customer and source ids must be positive");
        }
        if (service != PlatformService.CLEANING && service != PlatformService.TRANSFER) {
            throw new IllegalArgumentException("Repeat action service must support repeat flows");
        }
        jdbcTemplate.update("""
                insert into repeat_action_event(
                    customer_id,
                    service,
                    source_entity_id,
                    event_type,
                    occurred_at
                ) values (
                    :customerId,
                    :service,
                    :sourceEntityId,
                    :eventType,
                    :occurredAt
                )
                on conflict (customer_id, service, source_entity_id, event_type) do nothing
                """, Map.of(
                "customerId", customerId,
                "service", service.name(),
                "sourceEntityId", sourceEntityId,
                "eventType", eventType.name(),
                "occurredAt", OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC)
        ));
    }
}
