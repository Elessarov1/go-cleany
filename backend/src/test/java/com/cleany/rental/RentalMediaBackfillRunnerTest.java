package com.cleany.rental;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.health.contributor.Status;
import org.springframework.util.unit.DataSize;

class RentalMediaBackfillRunnerTest {

    @Test
    void completedBackfillMakesHealthUp() {
        RentalMediaProperties properties = properties(true);
        RentalMediaBackfillBatchProcessor processor = Mockito.mock(
                RentalMediaBackfillBatchProcessor.class
        );
        Mockito.when(processor.processNextBatch(10)).thenReturn(2, 1, 0);
        RentalMediaBackfillHealthIndicator health = new RentalMediaBackfillHealthIndicator(
                properties
        );
        RentalMediaBackfillRunner runner = new RentalMediaBackfillRunner(
                properties,
                processor,
                health
        );

        Assertions.assertEquals(Status.DOWN, health.health().getStatus());
        runner.run(Mockito.mock(ApplicationArguments.class));

        Assertions.assertAll(
                () -> Assertions.assertEquals(Status.UP, health.health().getStatus()),
                () -> Assertions.assertEquals(3L, health.health().getDetails().get("processed"))
        );
    }

    @Test
    void failureIsPropagatedAndHealthRemainsDown() {
        RentalMediaProperties properties = properties(true);
        RentalMediaBackfillBatchProcessor processor = Mockito.mock(
                RentalMediaBackfillBatchProcessor.class
        );
        var failure = new RentalMediaBackfillException(7, 11, new IllegalArgumentException());
        Mockito.when(processor.processNextBatch(10)).thenThrow(failure);
        RentalMediaBackfillHealthIndicator health = new RentalMediaBackfillHealthIndicator(
                properties
        );
        RentalMediaBackfillRunner runner = new RentalMediaBackfillRunner(
                properties,
                processor,
                health
        );

        Assertions.assertSame(
                failure,
                Assertions.assertThrows(
                        RentalMediaBackfillException.class,
                        () -> runner.run(Mockito.mock(ApplicationArguments.class))
                )
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(Status.DOWN, health.health().getStatus()),
                () -> Assertions.assertEquals(
                        failure.getMessage(),
                        health.health().getDetails().get("failure")
                )
        );
    }

    private static RentalMediaProperties properties(boolean enabled) {
        return new RentalMediaProperties(
                enabled,
                10,
                true,
                DataSize.ofMegabytes(64)
        );
    }
}
