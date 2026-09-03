package com.cleany.observability;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SchedulerJobTelemetry {

    private static final Logger log = LoggerFactory.getLogger(SchedulerJobTelemetry.class);
    private static final List<String> JOBS = List.of(
            "smart-reminders",
            "data-retention",
            "rental-cleaning-benefit"
    );
    private static final List<String> OUTCOMES = List.of("success", "partial_failure", "failure");

    private final MeterRegistry meterRegistry;

    @PostConstruct
    void registerMeters() {
        for (String job : JOBS) {
            for (String outcome : OUTCOMES) {
                Tags tags = Tags.of("job", job, "outcome", outcome);
                meterRegistry.counter("loco.scheduler.runs", tags);
                meterRegistry.timer("loco.scheduler.duration", tags);
                meterRegistry.counter("loco.scheduler.candidates", tags);
                meterRegistry.counter("loco.scheduler.processed", tags);
                meterRegistry.counter("loco.scheduler.failures", tags);
            }
        }
    }

    public void completed(
            String job,
            Instant startedAt,
            Duration duration,
            SchedulerRunSummary summary
    ) {
        String outcome = summary.failed() == 0 ? "success" : "partial_failure";
        record(job, outcome, duration, summary);
        log.info(
                "scheduler_run job={} outcome={} startedAt={} endedAt={} durationMs={} "
                        + "candidates={} processed={} skipped={} failed={}",
                job,
                outcome,
                startedAt,
                startedAt.plus(duration),
                duration.toMillis(),
                summary.candidates(),
                summary.processed(),
                summary.skipped(),
                summary.failed()
        );
    }

    public void failed(
            String job,
            Instant startedAt,
            Duration duration,
            SchedulerRunSummary summary,
            RuntimeException exception
    ) {
        record(job, "failure", duration, summary);
        log.error(
                "scheduler_run job={} outcome=failure startedAt={} endedAt={} durationMs={} "
                        + "candidates={} processed={} skipped={} failed={}",
                job,
                startedAt,
                startedAt.plus(duration),
                duration.toMillis(),
                summary.candidates(),
                summary.processed(),
                summary.skipped(),
                summary.failed(),
                exception
        );
    }

    private void record(
            String job,
            String outcome,
            Duration duration,
            SchedulerRunSummary summary
    ) {
        Objects.requireNonNull(job, "job");
        Objects.requireNonNull(duration, "duration");
        Objects.requireNonNull(summary, "summary");
        Tags tags = Tags.of("job", job, "outcome", outcome);
        meterRegistry.counter("loco.scheduler.runs", tags).increment();
        meterRegistry.timer("loco.scheduler.duration", tags).record(duration);
        increment("loco.scheduler.candidates", tags, summary.candidates());
        increment("loco.scheduler.processed", tags, summary.processed());
        increment("loco.scheduler.failures", tags, summary.failed());
    }

    private void increment(String name, Tags tags, long amount) {
        var counter = meterRegistry.counter(name, tags);
        if (amount > 0) {
            counter.increment(amount);
        }
    }
}
