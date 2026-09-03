package com.cleany.observability;

public record SchedulerRunSummary(
        long candidates,
        long processed,
        long skipped,
        long failed
) {

    public SchedulerRunSummary {
        if (candidates < 0 || processed < 0 || skipped < 0 || failed < 0) {
            throw new IllegalArgumentException("Scheduler counters must not be negative");
        }
    }

    public static SchedulerRunSummary failedRun() {
        return new SchedulerRunSummary(0, 0, 0, 1);
    }
}
