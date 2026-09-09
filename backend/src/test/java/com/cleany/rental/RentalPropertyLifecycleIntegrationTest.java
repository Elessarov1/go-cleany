package com.cleany.rental;

import java.awt.Color;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProviderReferenceRepository;

class RentalPropertyLifecycleIntegrationTest extends BaseIntegrationTest {

    private static final long ADMIN_ID = 900001L;

    @Autowired
    private RentalPropertyService propertyService;

    @Autowired
    private RentalPropertyMediaService mediaService;

    @Autowired
    private RentalOccupancyService occupancyService;

    @Autowired
    private RentalOccupancyRepository occupancyRepository;

    @Autowired
    private RentalBookingService bookingService;

    @Autowired
    private RentalStayPolicy stayPolicy;

    @Autowired
    private RentalPropertyRepository propertyRepository;

    @Autowired
    private RentalPropertyMediaRepository propertyMediaRepository;

    @Autowired
    private RentalBookingRepository bookingRepository;

    @Autowired
    private MediaAssetRepository mediaAssetRepository;

    @Autowired
    private MediaProviderReferenceRepository providerReferenceRepository;

    @Autowired
    private CustomerAccountService customerAccountService;

    @Autowired
    private CustomerExternalIdentityRepository identityRepository;

    @Autowired
    private CustomerAccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from rental_occupancy");
        bookingRepository.deleteAll();
        propertyMediaRepository.deleteAll();
        propertyRepository.deleteAll();
        providerReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void draftWithoutHistory_deleteRemovesManualOccupancyAndOwnedMedia() {
        RentalPropertyResponse draft = propertyService.createDraft();
        propertyService.update(
                draft.id(),
                RentalTestFixtures.details("draft-to-delete", new BigDecimal("100.00"))
        );
        mediaService.add(
                draft.id(),
                RentalTestImages.jpeg(80, 60, Color.BLUE),
                true
        );
        RentalPropertyResponse withMedia = propertyService.getAdminProperty(draft.id());
        long assetId = withMedia.media().getFirst().mediaAssetId();
        LocalDate start = stayPolicy.today().plusDays(10);
        occupancyService.createManual(
                draft.id(),
                new UpsertRentalOccupancyRequest(
                        start,
                        start.plusDays(7),
                        RentalOccupancyType.MAINTENANCE,
                        "Draft setup"
                ),
                ADMIN_ID
        );

        propertyService.deleteProperty(draft.id());

        Assertions.assertAll(
                () -> Assertions.assertFalse(propertyRepository.existsById(draft.id())),
                () -> Assertions.assertFalse(propertyMediaRepository.existsByProperty_Id(draft.id())),
                () -> Assertions.assertFalse(mediaAssetRepository.existsById(assetId)),
                () -> Assertions.assertTrue(
                        occupancyRepository.findOverlapping(
                                draft.id(),
                                start,
                                start.plusDays(7)
                        ).isEmpty()
                )
        );
    }

    @Test
    void publishedProperty_mustBeUnpublishedBeforeDeletionAndLeavesPublicCatalog() {
        RentalPropertyResponse published = RentalTestFixtures.publishedProperty(
                propertyService,
                mediaService,
                "published-lifecycle",
                new BigDecimal("100.00")
        );

        Assertions.assertThrows(
                RentalPropertyCannotBeDeletedException.class,
                () -> propertyService.deleteProperty(published.id())
        );

        RentalPropertyResponse unpublished = propertyService.unpublish(published.id());

        Assertions.assertAll(
                () -> Assertions.assertEquals(RentalPropertyStatus.DRAFT, unpublished.status()),
                () -> Assertions.assertThrows(
                        RentalPropertyNotFoundException.class,
                        () -> propertyService.getPublishedProperty(published.slug())
                ),
                () -> Assertions.assertDoesNotThrow(() -> propertyService.deleteProperty(published.id()))
        );
    }

    @Test
    void archivedPropertyWithoutBookingHistory_canBeDeleted() {
        RentalPropertyResponse published = RentalTestFixtures.publishedProperty(
                propertyService,
                mediaService,
                "archived-delete",
                new BigDecimal("100.00")
        );
        long assetId = published.media().getFirst().mediaAssetId();

        propertyService.archive(published.id());
        propertyService.deleteProperty(published.id());

        Assertions.assertAll(
                () -> Assertions.assertFalse(propertyRepository.existsById(published.id())),
                () -> Assertions.assertFalse(propertyMediaRepository.existsByProperty_Id(published.id())),
                () -> Assertions.assertFalse(mediaAssetRepository.existsById(assetId))
        );
    }

    @Test
    void archivedPropertyWithBookingHistory_cannotBeDeleted() {
        RentalPropertyResponse published = RentalTestFixtures.publishedProperty(
                propertyService,
                mediaService,
                "protected-history",
                new BigDecimal("100.00")
        );
        LocalDate checkIn = stayPolicy.today().plusDays(10);
        RentalBookingResponse booking = bookingService.create(
                RentalTestFixtures.customer(customerAccountService, "lifecycle-customer"),
                new CreateRentalBookingRequest(
                        published.id(),
                        RentalTermType.DATE_RANGE,
                        checkIn,
                        checkIn.plusDays(7),
                        null,
                        2,
                        "+90 555 123 45 67",
                        null,
                        new BigDecimal("800.00"),
                        "TRY",
                        null
                )
        );

        propertyService.archive(published.id());

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        RentalPropertyCannotBeDeletedException.class,
                        () -> propertyService.deleteProperty(published.id())
                ),
                () -> Assertions.assertTrue(propertyRepository.existsById(published.id())),
                () -> Assertions.assertTrue(bookingRepository.existsById(booking.id())),
                () -> Assertions.assertThrows(
                        RentalPropertyNotFoundException.class,
                        () -> propertyService.getPublishedProperty(published.slug())
                )
        );
    }

    @Test
    void oneDayManualOccupancy_blocksOnlyItsInclusiveDate() {
        RentalPropertyResponse property = RentalTestFixtures.publishedProperty(
                propertyService,
                mediaService,
                "single-day-block",
                new BigDecimal("100.00")
        );
        LocalDate blockedDate = stayPolicy.today().plusDays(12);

        RentalOccupancyResponse occupancy = occupancyService.createManual(
                property.id(),
                new UpsertRentalOccupancyRequest(
                        blockedDate,
                        blockedDate,
                        RentalOccupancyType.OWNER_BLOCK,
                        "One day"
                ),
                ADMIN_ID
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(blockedDate, occupancy.startDate()),
                () -> Assertions.assertEquals(blockedDate, occupancy.endDate()),
                () -> Assertions.assertTrue(occupancyRepository.overlaps(
                        property.id(),
                        blockedDate,
                        blockedDate
                )),
                () -> Assertions.assertFalse(occupancyRepository.overlaps(
                        property.id(),
                        blockedDate.plusDays(1),
                        blockedDate.plusDays(1)
                ))
        );
    }

    @Test
    void globalDisplayOrder_controlsAdminAndPublishedListsAndCompactsAfterDelete() {
        RentalPropertyResponse first = RentalTestFixtures.publishedProperty(
                propertyService,
                mediaService,
                "order-first",
                new BigDecimal("100.00")
        );
        RentalPropertyResponse second = RentalTestFixtures.publishedProperty(
                propertyService,
                mediaService,
                "order-second",
                new BigDecimal("110.00")
        );
        RentalPropertyResponse archived = RentalTestFixtures.publishedProperty(
                propertyService,
                mediaService,
                "order-archived",
                new BigDecimal("120.00")
        );
        propertyService.archive(archived.id());
        RentalPropertyResponse draft = propertyService.createDraft();
        RentalPropertyResponse numberedDraft = propertyService.update(
                draft.id(),
                RentalTestFixtures.details("order-draft", new BigDecimal("130.00"))
        );

        List<RentalPropertyResponse> reordered = propertyService.reorder(List.of(
                draft.id(),
                second.id(),
                archived.id(),
                first.id()
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        List.of(draft.id(), second.id(), archived.id(), first.id()),
                        reordered.stream().map(RentalPropertyResponse::id).toList()
                ),
                () -> Assertions.assertEquals(
                        List.of(0, 1, 2, 3),
                        reordered.stream().map(RentalPropertyResponse::displayOrder).toList()
                ),
                () -> Assertions.assertEquals("12A", numberedDraft.apartmentNumber()),
                () -> Assertions.assertThrows(
                        InvalidRentalPropertyOrderException.class,
                        () -> propertyService.reorder(List.of(first.id()))
                ),
                () -> Assertions.assertThrows(
                        InvalidRentalPropertyOrderException.class,
                        () -> propertyService.reorder(List.of(
                                first.id(), first.id(), archived.id(), draft.id()
                        ))
                ),
                () -> Assertions.assertThrows(
                        InvalidRentalPropertyOrderException.class,
                        () -> propertyService.reorder(List.of(
                                first.id(), second.id(), archived.id(), Long.MAX_VALUE
                        ))
                )
        );

        propertyService.deleteProperty(draft.id());

        Assertions.assertEquals(
                List.of(0, 1, 2),
                propertyService.getAdminProperties().stream()
                        .map(RentalPropertyResponse::displayOrder)
                        .toList()
        );
    }
}
