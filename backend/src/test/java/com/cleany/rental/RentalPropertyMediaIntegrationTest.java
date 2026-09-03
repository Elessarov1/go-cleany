package com.cleany.rental;

import java.math.BigDecimal;
import java.awt.Color;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaOrphanCleanupService;
import com.cleany.media.MediaProviderReferenceRepository;
import com.cleany.media.MediaStorage;

class RentalPropertyMediaIntegrationTest extends BaseIntegrationTest {

    private static final byte[] FIRST_JPEG = RentalTestImages.jpeg(64, 48, Color.BLUE);
    private static final byte[] SECOND_JPEG = RentalTestImages.jpeg(80, 60, Color.GREEN);

    @Autowired
    private RentalPropertyService propertyService;

    @Autowired
    private RentalPropertyMediaService propertyMediaService;

    @Autowired
    private RentalPropertyMediaRepository propertyMediaRepository;

    @Autowired
    private RentalPropertyRepository propertyRepository;

    @Autowired
    private MediaOrphanCleanupService orphanCleanupService;

    @Autowired
    private MediaStorage mediaStorage;

    @Autowired
    private MediaAssetRepository mediaAssetRepository;

    @Autowired
    private MediaProviderReferenceRepository providerReferenceRepository;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        propertyMediaRepository.deleteAll();
        propertyRepository.deleteAll();
        providerReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
    }

    @Test
    void draftMedia_publishArchiveRetentionAndExplicitDelete_preserveRequiredInvariants() {
        RentalPropertyResponse draft = propertyService.createDraft();
        Assertions.assertThrows(
                RentalPropertyCannotBePublishedException.class,
                () -> propertyService.publish(draft.id())
        );
        propertyService.update(
                draft.id(),
                RentalPropertyTest.completeDetails(new BigDecimal("2500.00"))
        );

        propertyMediaService.add(draft.id(), FIRST_JPEG, false);
        propertyMediaService.add(draft.id(), SECOND_JPEG, true);
        RentalPropertyResponse withMedia = propertyService.getAdminProperty(draft.id());
        RentalPropertyMediaResponse first = withMedia.media().getFirst();
        RentalPropertyMediaResponse second = withMedia.media().get(1);
        Assertions.assertAll(
                () -> Assertions.assertFalse(first.cover()),
                () -> Assertions.assertTrue(second.cover()),
                () -> Assertions.assertEquals(
                        "image/jpeg",
                        mediaStorage.get(first.mediaAssetId()).contentType()
                ),
                () -> Assertions.assertEquals(6, mediaAssetRepository.count()),
                () -> Assertions.assertTrue(first.cardUrl().contains("/card?v=")),
                () -> Assertions.assertTrue(first.thumbnailUrl().contains("/thumbnail?v=")),
                () -> Assertions.assertEquals(
                        64,
                        RentalTestImages.read(
                                mediaStorage.get(first.mediaAssetId()).content()
                        ).getWidth()
                ),
                () -> Assertions.assertEquals(
                        60,
                        RentalTestImages.read(
                                mediaStorage.get(second.mediaAssetId()).content()
                        ).getHeight()
                )
        );

        propertyMediaService.reorder(draft.id(), List.of(second.id(), first.id()));
        RentalPropertyResponse published = propertyService.publish(draft.id());
        Assertions.assertAll(
                () -> Assertions.assertEquals(RentalPropertyStatus.PUBLISHED, published.status()),
                () -> Assertions.assertEquals(second.id(), published.media().getFirst().id()),
                () -> Assertions.assertTrue(published.media().getFirst().cover()),
                () -> Assertions.assertEquals(1, propertyService.getPublishedProperties().size()),
                () -> Assertions.assertEquals(
                        draft.id(),
                        propertyService.getPublishedProperty("orange-residence").id()
                )
        );

        propertyMediaService.setCover(draft.id(), first.id());
        RentalPropertyResponse coverChanged = propertyService.getAdminProperty(draft.id());
        RentalPropertyResponse publicCoverChanged = propertyService.getPublishedProperty(
                "orange-residence"
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        propertyMediaRepository
                                .findAllByProperty_IdOrderBySortOrderAscIdAsc(draft.id())
                                .stream()
                                .filter(RentalPropertyMedia::isCover)
                                .count()
                ),
                () -> Assertions.assertTrue(coverChanged.media().stream()
                        .anyMatch(media -> media.id() == first.id() && media.cover())),
                () -> Assertions.assertTrue(publicCoverChanged.media().stream()
                        .anyMatch(media -> media.id() == first.id() && media.cover()))
        );

        propertyMediaService.remove(draft.id(), second.id());
        RentalPropertyResponse afterCoverRemoval = propertyService.getAdminProperty(draft.id());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, afterCoverRemoval.media().size()),
                () -> Assertions.assertTrue(afterCoverRemoval.media().getFirst().cover()),
                () -> Assertions.assertFalse(mediaAssetRepository.existsById(second.mediaAssetId())),
                () -> Assertions.assertEquals(3, mediaAssetRepository.count())
        );
        Assertions.assertThrows(
                RentalPropertyCannotBePublishedException.class,
                () -> propertyMediaService.remove(draft.id(), first.id())
        );

        propertyService.archive(draft.id());
        Assertions.assertTrue(propertyService.getPublishedProperties().isEmpty());
        Assertions.assertThrows(
                RentalPropertyMediaNotFoundException.class,
                () -> propertyMediaService.getPublicContent(draft.id(), first.id())
        );

        int deletedOrphans = orphanCleanupService.deleteUnreferencedBatch(100);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, deletedOrphans),
                () -> Assertions.assertTrue(mediaAssetRepository.existsById(first.mediaAssetId())),
                () -> Assertions.assertEquals(
                        "image/jpeg",
                        propertyMediaService.getAdminContent(draft.id(), first.id()).contentType()
                )
        );

        propertyMediaService.remove(draft.id(), first.id());
        Assertions.assertFalse(mediaAssetRepository.existsById(first.mediaAssetId()));
    }
}
