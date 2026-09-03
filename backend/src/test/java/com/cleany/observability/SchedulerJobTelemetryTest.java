package com.cleany.observability;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

class SchedulerJobTelemetryTest {

    @Test
    void completedRun_recordsTaggedCountersAndDuration() {
        var registry = new SimpleMeterRegistry();
        var telemetry = new SchedulerJobTelemetry(registry);

        telemetry.completed(
                "smart-reminders",
                Instant.parse("2026-09-03T06:00:00Z"),
                Duration.ofMillis(125),
                new SchedulerRunSummary(7, 4, 3, 0)
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(1.0, value(registry, "loco.scheduler.runs")),
                () -> Assertions.assertEquals(7.0, value(registry, "loco.scheduler.candidates")),
                () -> Assertions.assertEquals(4.0, value(registry, "loco.scheduler.processed")),
                () -> Assertions.assertEquals(0.0, value(registry, "loco.scheduler.failures")),
                () -> Assertions.assertEquals(
                        125.0,
                        registry.get("loco.scheduler.duration")
                                .tag("job", "smart-reminders")
                                .tag("outcome", "success")
                                .timer()
                                .totalTime(TimeUnit.MILLISECONDS)
                )
        );
    }

    @Test
    void failedRun_usesFailureOutcome() {
        var registry = new SimpleMeterRegistry();
        var telemetry = new SchedulerJobTelemetry(registry);

        telemetry.failed(
                "data-retention",
                Instant.parse("2026-09-03T00:30:00Z"),
                Duration.ofMillis(10),
                SchedulerRunSummary.failedRun(),
                new IllegalStateException("database unavailable")
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(1.0, registry.get("loco.scheduler.runs")
                        .tag("job", "data-retention")
                        .tag("outcome", "failure")
                        .counter()
                        .count()),
                () -> Assertions.assertEquals(1.0, registry.get("loco.scheduler.failures")
                        .tag("job", "data-retention")
                        .tag("outcome", "failure")
                        .counter()
                        .count())
        );
    }

    private static double value(SimpleMeterRegistry registry, String name) {
        return registry.get(name)
                .tag("job", "smart-reminders")
                .tag("outcome", "success")
                .counter()
                .count();
    }
}
