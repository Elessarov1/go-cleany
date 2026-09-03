package com.cleany.reminder;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cleany.observability.SchedulerJobTelemetry;
import com.cleany.observability.SchedulerRunSummary;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(
        prefix = "smart-reminders",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class SmartReminderJob {

    private static final String JOB_NAME = "smart-reminders";

    private final SmartReminderService reminderService;
    private final SchedulerJobTelemetry telemetry;
    private final Clock clock;

    @Scheduled(cron = "${smart-reminders.cron}", zone = "${smart-reminders.zone-id}")
    public void run() {
        Instant startedAt = clock.instant();
        long startedNanos = System.nanoTime();
        try {
            SmartReminderProcessingResult result = reminderService.process();
            telemetry.completed(
                    JOB_NAME,
                    startedAt,
                    elapsed(startedNanos),
                    new SchedulerRunSummary(
                            result.candidates(),
                            result.processed(),
                            result.skipped(),
                            result.failed()
                    )
            );
        } catch (RuntimeException exception) {
            telemetry.failed(
                    JOB_NAME,
                    startedAt,
                    elapsed(startedNanos),
                    SchedulerRunSummary.failedRun(),
                    exception
            );
            throw exception;
        }
    }

    private static Duration elapsed(long startedNanos) {
        return Duration.ofNanos(System.nanoTime() - startedNanos);
    }
}
