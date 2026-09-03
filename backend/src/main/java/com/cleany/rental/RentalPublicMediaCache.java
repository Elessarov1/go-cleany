package com.cleany.rental;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;

@Component
public class RentalPublicMediaCache {

    static final String METRIC_CACHE_NAME = "rental-public-media";

    private final boolean enabled;
    private final Cache<CacheKey, RentalMediaContent> cache;
    private final ConcurrentHashMap<Long, AtomicLong> generations = new ConcurrentHashMap<>();

    public RentalPublicMediaCache(
            RentalMediaProperties properties,
            MeterRegistry meterRegistry
    ) {
        enabled = properties.cacheEnabled();
        Cache<CacheKey, RentalMediaContent> configuredCache = Caffeine.newBuilder()
                .maximumWeight(properties.cacheMaxSize().toBytes())
                .weigher((CacheKey ignored, RentalMediaContent content) -> content.content().length)
                .recordStats()
                .build();
        cache = CaffeineCacheMetrics.monitor(
                meterRegistry,
                configuredCache,
                METRIC_CACHE_NAME
        );
        Gauge.builder(
                        "loco.rental.media.cache.bytes",
                        this,
                        RentalPublicMediaCache::weightedSize
                )
                .description("Rental public media bytes currently retained in memory")
                .register(meterRegistry);
    }

    public RentalMediaContent get(
            long propertyId,
            long mediaId,
            RentalMediaVariant variant,
            Supplier<RentalMediaContent> loader
    ) {
        if (!enabled) {
            return loader.get();
        }
        while (true) {
            long generation = generation(propertyId).get();
            CacheKey key = new CacheKey(propertyId, mediaId, variant, generation);
            RentalMediaContent content = cache.get(key, ignored -> loader.get());
            if (generation(propertyId).get() == generation) {
                return content;
            }
            cache.invalidate(key);
        }
    }

    public void invalidateProperty(long propertyId) {
        generation(propertyId).incrementAndGet();
        cache.asMap().keySet().removeIf(key -> key.propertyId() == propertyId);
        cache.cleanUp();
    }

    public void clear() {
        cache.invalidateAll();
        cache.cleanUp();
        generations.clear();
    }

    public CacheStats stats() {
        return cache.stats();
    }

    public long estimatedSize() {
        return cache.estimatedSize();
    }

    public long weightedSize() {
        return cache.policy()
                .eviction()
                .map(eviction -> eviction.weightedSize().orElse(0L))
                .orElse(0L);
    }

    void cleanUp() {
        cache.cleanUp();
    }

    private AtomicLong generation(long propertyId) {
        return generations.computeIfAbsent(propertyId, ignored -> new AtomicLong());
    }

    private record CacheKey(
            long propertyId,
            long mediaId,
            RentalMediaVariant variant,
            long generation
    ) {
    }
}
