package com.cleany.rental;

import java.math.BigDecimal;

import jakarta.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProviderReferenceRepository;

import io.micrometer.core.instrument.MeterRegistry;

@TestPropertySource(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class RentalPublicMediaCacheIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RentalPropertyService propertyService;

    @Autowired
    private RentalPropertyMediaService mediaService;

    @Autowired
    private RentalPublicMediaCache mediaCache;

    @Autowired
    private RentalPropertyMediaRepository mediaRepository;

    @Autowired
    private RentalPropertyRepository propertyRepository;

    @Autowired
    private MediaAssetRepository mediaAssetRepository;

    @Autowired
    private MediaProviderReferenceRepository providerReferenceRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        mediaCache.clear();
        mediaRepository.deleteAll();
        propertyRepository.deleteAll();
        providerReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
    }

    @Test
    void secondPublicReadComesFromMemoryWithoutAnotherDatabaseStatement() {
        RentalPropertyResponse property = publishedProperty("cache-first-read");
        long mediaId = property.media().getFirst().id();
        var statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
        long missesBefore = mediaCache.stats().missCount();
        long hitsBefore = mediaCache.stats().hitCount();

        RentalMediaContent first = mediaService.getPublicContent(
                property.id(),
                mediaId,
                RentalMediaVariant.THUMBNAIL
        );
        long firstReadStatements = statistics.getPrepareStatementCount();
        RentalMediaContent second = mediaService.getPublicContent(
                property.id(),
                mediaId,
                RentalMediaVariant.THUMBNAIL
        );

        Assertions.assertAll(
                () -> Assertions.assertArrayEquals(first.content(), second.content()),
                () -> Assertions.assertTrue(firstReadStatements >= 2),
                () -> Assertions.assertEquals(
                        firstReadStatements,
                        statistics.getPrepareStatementCount()
                ),
                () -> Assertions.assertEquals(missesBefore + 1, mediaCache.stats().missCount()),
                () -> Assertions.assertEquals(hitsBefore + 1, mediaCache.stats().hitCount()),
                () -> Assertions.assertFalse(meterRegistry.find("cache.gets")
                        .tag("cache", RentalPublicMediaCache.METRIC_CACHE_NAME)
                        .meters()
                        .isEmpty())
        );
    }

    @Test
    void propertyMutationInvalidatesOnlyThatProperty() {
        RentalPropertyResponse first = publishedProperty("cache-invalidation-first");
        RentalPropertyResponse second = publishedProperty("cache-invalidation-second");
        long firstMediaId = first.media().getFirst().id();
        long secondMediaId = second.media().getFirst().id();
        mediaService.getPublicContent(first.id(), firstMediaId, RentalMediaVariant.CARD);
        mediaService.getPublicContent(second.id(), secondMediaId, RentalMediaVariant.CARD);
        Assertions.assertEquals(2, mediaCache.estimatedSize());

        mediaService.setCover(first.id(), firstMediaId);

        Assertions.assertEquals(1, mediaCache.estimatedSize());
        long missesBefore = mediaCache.stats().missCount();
        long hitsBefore = mediaCache.stats().hitCount();
        mediaService.getPublicContent(first.id(), firstMediaId, RentalMediaVariant.CARD);
        mediaService.getPublicContent(second.id(), secondMediaId, RentalMediaVariant.CARD);
        Assertions.assertAll(
                () -> Assertions.assertEquals(missesBefore + 1, mediaCache.stats().missCount()),
                () -> Assertions.assertEquals(hitsBefore + 1, mediaCache.stats().hitCount())
        );
    }

    @Test
    void archivedAndDeletedPropertyCannotBeServedFromOldCacheEntry() {
        RentalPropertyResponse property = publishedProperty("cache-archive");
        long mediaId = property.media().getFirst().id();
        mediaService.getPublicContent(property.id(), mediaId, RentalMediaVariant.FULL);
        Assertions.assertEquals(1, mediaCache.estimatedSize());

        propertyService.archive(property.id());

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, mediaCache.estimatedSize()),
                () -> Assertions.assertThrows(
                        RentalPropertyMediaNotFoundException.class,
                        () -> mediaService.getPublicContent(
                                property.id(),
                                mediaId,
                                RentalMediaVariant.FULL
                        )
                )
        );

        propertyService.deleteProperty(property.id());
        Assertions.assertThrows(
                RentalPropertyMediaNotFoundException.class,
                () -> mediaService.getPublicContent(
                        property.id(),
                        mediaId,
                        RentalMediaVariant.FULL
                )
        );
    }

    private RentalPropertyResponse publishedProperty(String slug) {
        return RentalTestFixtures.publishedProperty(
                propertyService,
                mediaService,
                slug,
                new BigDecimal("100.00")
        );
    }
}
