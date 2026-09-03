package com.cleany.rental;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
class RentalMediaBackfillHealthIndicator implements HealthIndicator {

    private final AtomicReference<State> state;
    private volatile long processed;
    private volatile String failure;

    RentalMediaBackfillHealthIndicator(RentalMediaProperties properties) {
        state = new AtomicReference<>(
                properties.backfillEnabled() ? State.PENDING : State.DISABLED
        );
    }

    void markRunning() {
        state.set(State.RUNNING);
    }

    void markCompleted(long processed) {
        this.processed = processed;
        state.set(State.COMPLETED);
    }

    void markFailed(RuntimeException exception) {
        failure = exception.getMessage();
        state.set(State.FAILED);
    }

    @Override
    public Health health() {
        State current = state.get();
        Health.Builder builder = switch (current) {
            case DISABLED, COMPLETED -> Health.up();
            case PENDING, RUNNING, FAILED -> Health.down();
        };
        builder.withDetail("state", current.name());
        if (current == State.COMPLETED) {
            builder.withDetail("processed", processed);
        }
        if (failure != null) {
            builder.withDetail("failure", failure);
        }
        return builder.build();
    }

    private enum State {
        DISABLED,
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED
    }
}
