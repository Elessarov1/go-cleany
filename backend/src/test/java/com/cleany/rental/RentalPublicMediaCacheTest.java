package com.cleany.rental;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

class RentalPublicMediaCacheTest {

    @Test
    void invalidationDuringLoadCannotPublishStaleGeneration() throws Exception {
        RentalPublicMediaCache cache = cache();
        CountDownLatch firstLoadStarted = new CountDownLatch(1);
        CountDownLatch continueFirstLoad = new CountDownLatch(1);
        AtomicInteger loads = new AtomicInteger();
        try (var executor = Executors.newSingleThreadExecutor()) {
            var result = executor.submit(() -> cache.get(
                    7,
                    11,
                    RentalMediaVariant.FULL,
                    () -> {
                        int currentLoad = loads.incrementAndGet();
                        if (currentLoad == 1) {
                            firstLoadStarted.countDown();
                            await(continueFirstLoad);
                            return content(101, "old");
                        }
                        return content(102, "new");
                    }
            ));

            Assertions.assertTrue(firstLoadStarted.await(5, TimeUnit.SECONDS));
            cache.invalidateProperty(7);
            continueFirstLoad.countDown();

            Assertions.assertAll(
                    () -> Assertions.assertEquals("new", new String(
                            result.get(5, TimeUnit.SECONDS).content(),
                            StandardCharsets.UTF_8
                    )),
                    () -> Assertions.assertEquals(2, loads.get()),
                    () -> Assertions.assertEquals(1, cache.estimatedSize())
            );
        }
    }

    @Test
    void byteWeightBoundsRetainedWorkingSet() {
        RentalPublicMediaCache cache = new RentalPublicMediaCache(
                new RentalMediaProperties(true, DataSize.ofBytes(5)),
                new SimpleMeterRegistry()
        );

        cache.get(1, 1, RentalMediaVariant.FULL, () -> content(1, "1234"));
        cache.get(2, 2, RentalMediaVariant.FULL, () -> content(2, "5678"));
        cache.cleanUp();

        Assertions.assertAll(
                () -> Assertions.assertTrue(cache.weightedSize() <= 5),
                () -> Assertions.assertTrue(cache.estimatedSize() <= 1),
                () -> Assertions.assertTrue(cache.stats().evictionCount() >= 1)
        );
    }

    private static RentalPublicMediaCache cache() {
        return new RentalPublicMediaCache(
                new RentalMediaProperties(true, DataSize.ofMegabytes(64)),
                new SimpleMeterRegistry()
        );
    }

    private static RentalMediaContent content(long assetId, String value) {
        return new RentalMediaContent(
                assetId,
                "image/jpeg",
                value.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Timed out while waiting for cache test latch");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Cache test was interrupted", exception);
        }
    }
}
