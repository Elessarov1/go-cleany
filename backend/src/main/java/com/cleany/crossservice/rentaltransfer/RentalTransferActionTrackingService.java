package com.cleany.crossservice.rentaltransfer;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalTransferActionTrackingService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Clock clock;

    @Transactional
    public void record(
            long customerId,
            long rentalBookingId,
            RentalTransferContextType context,
            RentalTransferActionEventType eventType
    ) {
        if (customerId <= 0 || rentalBookingId <= 0) {
            throw new IllegalArgumentException("Customer and rental booking ids must be positive");
        }
        jdbcTemplate.update("""
                insert into rental_transfer_action_event(
                    customer_id,
                    rental_booking_id,
                    context_type,
                    event_type,
                    occurred_at
                ) values (
                    :customerId,
                    :rentalBookingId,
                    :contextType,
                    :eventType,
                    :occurredAt
                )
                on conflict (customer_id, rental_booking_id, context_type, event_type) do nothing
                """, Map.of(
                "customerId", customerId,
                "rentalBookingId", rentalBookingId,
                "contextType", context.name(),
                "eventType", eventType.name(),
                "occurredAt", OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC)
        ));
    }
}
