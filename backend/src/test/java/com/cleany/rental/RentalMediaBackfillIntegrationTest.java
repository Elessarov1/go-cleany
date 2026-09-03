package com.cleany.rental;

import java.awt.Color;
import java.math.BigDecimal;
import java.sql.Timestamp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProviderReferenceRepository;
import com.cleany.media.MediaStorage;
import com.cleany.media.MediaUpload;

class RentalMediaBackfillIntegrationTest extends BaseIntegrationTest {

    private static final byte[] LARGE_JPEG = RentalTestImages.jpeg(1200, 900, Color.BLUE);

    @Autowired
    private RentalPropertyService propertyService;

    @Autowired
    private RentalPropertyMediaService propertyMediaService;

    @Autowired
    private RentalPublicMediaCache mediaCache;

    @Autowired
    private RentalPropertyMediaRepository propertyMediaRepository;

    @Autowired
    private RentalPropertyRepository propertyRepository;

    @Autowired
    private RentalMediaBackfillBatchProcessor batchProcessor;

    @Autowired
    private MediaStorage mediaStorage;

    @Autowired
    private MediaAssetRepository mediaAssetRepository;

    @Autowired
    private MediaProviderReferenceRepository providerReferenceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        propertyMediaRepository.deleteAll();
        propertyRepository.deleteAll();
        providerReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
    }

    @Test
    void backfillCreatesBothMissingVariantsAndIsIdempotent() {
        long propertyId = propertyService.createDraft().id();
        long mediaId = insertMedia(propertyId, LARGE_JPEG, null, null);
        long assetsBefore = mediaAssetRepository.count();

        int processed = batchProcessor.processNextBatch(10);
        RentalPropertyMedia media = propertyMediaRepository.findById(mediaId).orElseThrow();
        long assetsAfterFirstRun = mediaAssetRepository.count();
        int secondRun = batchProcessor.processNextBatch(10);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, processed),
                () -> Assertions.assertNotNull(media.getCardMediaAssetId()),
                () -> Assertions.assertNotNull(media.getThumbnailMediaAssetId()),
                () -> Assertions.assertEquals(assetsBefore + 2, assetsAfterFirstRun),
                () -> Assertions.assertEquals(0, secondRun),
                () -> Assertions.assertEquals(assetsAfterFirstRun, mediaAssetRepository.count()),
                () -> Assertions.assertEquals(
                        960,
                        RentalTestImages.read(
                                mediaStorage.get(media.getCardMediaAssetId()).content()
                        ).getWidth()
                ),
                () -> Assertions.assertEquals(
                        320,
                        RentalTestImages.read(
                                mediaStorage.get(media.getThumbnailMediaAssetId()).content()
                        ).getWidth()
                ),
                () -> Assertions.assertEquals(
                        2,
                        jdbcTemplate.queryForObject(
                                """
                                select count(*)
                                  from media_asset asset
                                 where asset.id in (?, ?)
                                """,
                                Integer.class,
                                media.getCardMediaAssetId(),
                                media.getThumbnailMediaAssetId()
                        )
                )
        );
    }

    @Test
    void backfillCreatesOnlyMissingVariantAndChangesVersionedUrl() {
        long propertyId = propertyService.createDraft().id();
        long existingCardId = mediaStorage.store(new MediaUpload(
                RentalTestImages.jpeg(960, 720, Color.GREEN),
                "image/jpeg"
        )).mediaId();
        long mediaId = insertMedia(propertyId, LARGE_JPEG, existingCardId, null);
        propertyService.update(
                propertyId,
                RentalTestFixtures.details("legacy-backfill", new BigDecimal("100.00"))
        );
        propertyService.publish(propertyId);
        String before = propertyService.getAdminProperty(propertyId)
                .media()
                .getFirst()
                .thumbnailUrl();
        RentalMediaContent fallback = propertyMediaService.getPublicContent(
                propertyId,
                mediaId,
                RentalMediaVariant.THUMBNAIL
        );
        Assertions.assertEquals(1, mediaCache.estimatedSize());
        long assetsBefore = mediaAssetRepository.count();

        Assertions.assertEquals(1, batchProcessor.processNextBatch(10));

        RentalPropertyMedia media = propertyMediaRepository.findById(mediaId).orElseThrow();
        Assertions.assertEquals(0, mediaCache.estimatedSize());
        RentalMediaContent responsive = propertyMediaService.getPublicContent(
                propertyId,
                mediaId,
                RentalMediaVariant.THUMBNAIL
        );
        String after = propertyService.getAdminProperty(propertyId)
                .media()
                .getFirst()
                .thumbnailUrl();
        Assertions.assertAll(
                () -> Assertions.assertEquals(existingCardId, media.getCardMediaAssetId()),
                () -> Assertions.assertNotNull(media.getThumbnailMediaAssetId()),
                () -> Assertions.assertEquals(assetsBefore + 1, mediaAssetRepository.count()),
                () -> Assertions.assertEquals(media.getMediaAssetId(), fallback.mediaAssetId()),
                () -> Assertions.assertEquals(
                        media.getThumbnailMediaAssetId().longValue(),
                        responsive.mediaAssetId()
                ),
                () -> Assertions.assertTrue(before.endsWith("?v=" + media.getMediaAssetId())),
                () -> Assertions.assertTrue(
                        after.endsWith("?v=" + media.getThumbnailMediaAssetId())
                ),
                () -> Assertions.assertNotEquals(before, after)
        );
    }

    @Test
    void corruptLegacyImageFailsWithPropertyAndMediaIdentity() {
        long propertyId = propertyService.createDraft().id();
        long mediaId = insertMedia(propertyId, new byte[] {1, 2, 3, 4}, null, null);

        RentalMediaBackfillException exception = Assertions.assertThrows(
                RentalMediaBackfillException.class,
                () -> batchProcessor.processNextBatch(10)
        );

        Assertions.assertAll(
                () -> Assertions.assertTrue(exception.getMessage().contains(
                        "property " + propertyId
                )),
                () -> Assertions.assertTrue(exception.getMessage().contains("media " + mediaId)),
                () -> Assertions.assertNull(propertyMediaRepository.findById(mediaId)
                        .orElseThrow()
                        .getCardMediaAssetId()),
                () -> Assertions.assertEquals(1, mediaAssetRepository.count())
        );
    }

    @Test
    void backfillHonorsConfiguredBatchBoundary() {
        long propertyId = propertyService.createDraft().id();
        byte[] image = RentalTestImages.jpeg(64, 48, Color.ORANGE);
        for (int index = 0; index < 11; index++) {
            insertMedia(propertyId, image, null, null);
        }

        int firstBatch = batchProcessor.processNextBatch(10);
        int secondBatch = batchProcessor.processNextBatch(10);
        int completed = batchProcessor.processNextBatch(10);

        Assertions.assertAll(
                () -> Assertions.assertEquals(10, firstBatch),
                () -> Assertions.assertEquals(1, secondBatch),
                () -> Assertions.assertEquals(0, completed),
                () -> Assertions.assertEquals(
                        0,
                        jdbcTemplate.queryForObject(
                                """
                                select count(*)
                                  from rental_property_media
                                 where card_media_asset_id is null
                                    or thumbnail_media_asset_id is null
                                """,
                                Integer.class
                        )
                )
        );
    }

    private long insertMedia(
            long propertyId,
            byte[] content,
            Long cardAssetId,
            Long thumbnailAssetId
    ) {
        long fullAssetId = mediaStorage.store(new MediaUpload(content, "image/jpeg")).mediaId();
        int sortOrder = jdbcTemplate.queryForObject(
                "select count(*) from rental_property_media where property_id = ?",
                Integer.class,
                propertyId
        );
        return jdbcTemplate.queryForObject(
                """
                insert into rental_property_media (
                    property_id,
                    media_asset_id,
                    card_media_asset_id,
                    thumbnail_media_asset_id,
                    sort_order,
                    is_cover,
                    created_at
                ) values (?, ?, ?, ?, ?, ?, ?)
                returning id
                """,
                Long.class,
                propertyId,
                fullAssetId,
                cardAssetId,
                thumbnailAssetId,
                sortOrder,
                sortOrder == 0,
                Timestamp.from(java.time.Instant.parse("2026-09-04T00:00:00Z"))
        );
    }
}
