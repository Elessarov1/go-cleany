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
        Mockito.when(service.cleanup(expectedCutoff)).thenReturn(
                new DataRetentionCleanupResult(expectedCutoff, 0, 0, 0, 0)
        );
        var job = job(service);

        job.run();

        Mockito.verify(service).cleanup(expectedCutoff);
    }

    @Test
    void cleanupFailure_doesNotEscapeScheduledJob() {
        DataRetentionCleanupService service = Mockito.mock(DataRetentionCleanupService.class);
        Mockito.when(service.cleanup(Mockito.any(Instant.class)))
                .thenThrow(new IllegalStateException("database unavailable"));
        var job = job(service);

        Assertions.assertDoesNotThrow(job::run);
    }

    private static DataRetentionCleanupJob job(DataRetentionCleanupService service) {
        return new DataRetentionCleanupJob(
                new DataRetentionProperties(true, 7, "0 30 3 * * *"),
                service,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
    }
}
