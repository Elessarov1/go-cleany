package com.cleany.rental;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalSearchTrackingService {

    private static final Logger log = LoggerFactory.getLogger(RentalSearchTrackingService.class);

    private final RentalSearchTrackingRepository repository;
    private final Clock clock;

    private UUID recordExecution(
            RentalSearchMode mode,
            int resultCount,
            long apiDurationMs,
            UUID previousSearchId,
            Instant calculatedAt
    ) {
        UUID executionId = UUID.randomUUID();
        UUID existingPrevious = previousSearchId != null && repository.exists(previousSearchId)
                ? previousSearchId
                : null;
        repository.insertExecution(
                executionId,
                mode,
                resultCount,
                Math.max(0, apiDurationMs),
                existingPrevious,
                calculatedAt
        );
        return executionId;
    }

    public UUID recordExecutionSafely(
            RentalSearchMode mode,
            int resultCount,
            long apiDurationMs,
            UUID previousSearchId,
            Instant calculatedAt
    ) {
        try {
            return recordExecution(
                    mode,
                    resultCount,
                    apiDurationMs,
                    previousSearchId,
                    calculatedAt
            );
        } catch (RuntimeException exception) {
            UUID untrackedId = UUID.randomUUID();
            log.warn("Rental search execution tracking failed executionId={}", untrackedId, exception);
            return untrackedId;
        }
    }

    public void recordOpenedSafely(UUID executionId) {
        recordEventSafely(executionId, RentalSearchEventType.PROPERTY_OPENED, null);
    }

    public void recordFirstCardSafely(UUID executionId, long durationMs) {
        if (durationMs < 0 || durationMs > 600_000) {
            return;
        }
        recordEventSafely(executionId, RentalSearchEventType.FIRST_CARD_RENDERED, durationMs);
    }

    public void recordBookingConflictSafely(UUID executionId) {
        recordEventSafely(executionId, RentalSearchEventType.BOOKING_CONFLICT, null);
    }

    public UUID compatibleExecution(UUID executionId, RentalTermType termType) {
        if (executionId == null || !repository.isCompatible(executionId, termType)) {
            return null;
        }
        return executionId;
    }

    private void recordEventSafely(
            UUID executionId,
            RentalSearchEventType eventType,
            Long durationMs
    ) {
        if (executionId == null) {
            return;
        }
        try {
            repository.insertEvent(executionId, eventType, durationMs, clock.instant());
        } catch (RuntimeException exception) {
            log.warn(
                    "Rental search event tracking failed executionId={} eventType={}",
                    executionId,
                    eventType,
                    exception
            );
        }
    }
}
