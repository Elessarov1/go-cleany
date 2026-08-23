package com.cleany.retention;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DataRetentionCleanupJobTest {

    private static final Instant NOW = Instant.parse("2026-08-21T12:00:00Z");

    @Test
    void configuredRetentionDays_determineCutoff() {
        DataRetentionCleanupService service = Mockito.mock(DataRetentionCleanupService.class);
        Instant expectedCutoff = Instant.parse("2026-08-14T12:00:00Z");
        Mockito.when(service.cleanupBatch(expectedCutoff, 100)).thenReturn(
                result(expectedCutoff, false)
        );
        var job = job(service);

        job.run();

        Mockito.verify(service).cleanupBatch(expectedCutoff, 100);
    }

    @Test
    void moreWork_runsAnotherBatchUntilQueueIsDrained() {
        DataRetentionCleanupService service = Mockito.mock(DataRetentionCleanupService.class);
        Mockito.when(service.cleanupBatch(Mockito.any(Instant.class), Mockito.eq(100)))
                .thenReturn(result(NOW, true), result(NOW, true), result(NOW, false));
        var job = job(service);

        job.run();

        Mockito.verify(service, Mockito.times(3))
                .cleanupBatch(NOW.minusSeconds(7L * 24 * 60 * 60), 100);
    }

    @Test
    void maximumBatchCount_stopsCurrentRunAndNextRunContinues() {
        DataRetentionCleanupService service = Mockito.mock(DataRetentionCleanupService.class);
        Mockito.when(service.cleanupBatch(Mockito.any(Instant.class), Mockito.eq(25)))
                .thenReturn(result(NOW, true), result(NOW, true), result(NOW, false));
        var job = new DataRetentionCleanupJob(
                new DataRetentionProperties(true, 7, "0 30 3 * * *", 25, 2),
                service,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );

        job.run();

        Mockito.verify(service, Mockito.times(2))
                .cleanupBatch(NOW.minusSeconds(7L * 24 * 60 * 60), 25);

        job.run();

        Mockito.verify(service, Mockito.times(3))
                .cleanupBatch(NOW.minusSeconds(7L * 24 * 60 * 60), 25);
    }

    @Test
    void cleanupFailure_doesNotEscapeScheduledJob() {
        DataRetentionCleanupService service = Mockito.mock(DataRetentionCleanupService.class);
        Mockito.when(service.cleanupBatch(Mockito.any(Instant.class), Mockito.anyInt()))
                .thenThrow(new IllegalStateException("database unavailable"));
        var job = job(service);

        Assertions.assertDoesNotThrow(job::run);
    }

    private static DataRetentionCleanupJob job(DataRetentionCleanupService service) {
        return new DataRetentionCleanupJob(
                new DataRetentionProperties(true, 7, "0 30 3 * * *", 100, 10),
                service,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
    }

    private static DataRetentionCleanupResult result(Instant cutoff, boolean hasMoreWork) {
        return new DataRetentionCleanupResult(cutoff, 0, 0, 0, 0, 0, hasMoreWork);
    }
}
