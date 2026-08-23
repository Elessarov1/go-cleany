package com.cleany.rental;

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
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProviderReferenceRepository;

class RentalBookingIntegrationTest extends BaseIntegrationTest {

    private static final long ADMIN_ACTOR_ID = 900001L;

    @Autowired
    private RentalBookingService bookingService;

    @Autowired
    private AdminRentalBookingService adminBookingService;

    @Autowired
    private RentalOccupancyService occupancyService;

    @Autowired
    private RentalPropertyService propertyService;

    @Autowired
    private RentalPropertyMediaService propertyMediaService;

    @Autowired
    private RentalStayPolicy stayPolicy;

    @Autowired
    private RentalBookingRepository bookingRepository;

    @Autowired
    private RentalPropertyMediaRepository propertyMediaRepository;

    @Autowired
    private RentalPropertyRepository propertyRepository;

    @Autowired
    private CustomerAccountService customerAccountService;

    @Autowired
    private CustomerExternalIdentityRepository identityRepository;

    @Autowired
    private CustomerAccountRepository accountRepository;

    @Autowired
    private MediaProviderReferenceRepository providerReferenceRepository;

    @Autowired
    private MediaAssetRepository mediaAssetRepository;

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
    void publishedProperty_bookingStoresStableFinancialSnapshotAfterPriceChange() {
        CurrentCustomer customer = RentalTestFixtures.customer(customerAccountService, "910001");
        RentalPropertyResponse property = publishedProperty("financial-snapshot", "100.00");
        LocalDate checkIn = stayPolicy.today().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(30);

        RentalBookingQuoteResponse quote = bookingService.quote(
                new RentalBookingQuoteRequest(property.id(), checkIn, checkOut)
        );
        RentalBookingResponse booking = bookingService.create(
                customer,
                request(property.id(), checkIn, checkOut)
        );

        propertyService.update(
                property.id(),
                RentalTestFixtures.details("financial-snapshot", new BigDecimal("200.00"))
        );
        RentalBooking persisted = bookingRepository.findById(booking.id()).orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertEquals(RentalBookingStatus.CONFIRMED, booking.status()),
                () -> Assertions.assertEquals(30, booking.durationDays()),
                () -> Assertions.assertEquals("100.00", quote.baseDailyPrice().toPlainString()),
                () -> Assertions.assertEquals("300.00", quote.discountAmount().toPlainString()),
                () -> Assertions.assertEquals("2700.00", quote.totalPrice().toPlainString()),
                () -> Assertions.assertEquals("100.00", persisted.getBaseDailyPriceSnapshot().toPlainString()),
                () -> Assertions.assertEquals("2700.00", persisted.getTotalPrice().toPlainString()),
                () -> Assertions.assertTrue(
                        occupancyService.publicAvailability(
                                property.id(),
                                checkIn,
                                checkOut
                        ).unavailableRanges().size() == 1
                )
        );
    }

    @Test
    void draftAndArchivedProperties_cannotBeQuotedOrBooked() {
        RentalPropertyResponse draft = propertyService.createDraft();
        propertyService.update(
                draft.id(),
                RentalTestFixtures.details("unpublished-property", new BigDecimal("100.00"))
        );
        LocalDate checkIn = stayPolicy.today().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(7);

        Assertions.assertThrows(
                RentalPropertyNotAvailableException.class,
                () -> bookingService.quote(new RentalBookingQuoteRequest(draft.id(), checkIn, checkOut))
        );

        propertyMediaService.add(draft.id(), new byte[]{
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x04, (byte) 0xD9
        }, true);
        propertyService.publish(draft.id());
        propertyService.archive(draft.id());

        Assertions.assertThrows(
                RentalPropertyNotAvailableException.class,
                () -> bookingService.quote(new RentalBookingQuoteRequest(draft.id(), checkIn, checkOut))
        );
    }

    @Test
    void everyOccupancyType_blocksOverlapAndAdjacentBookingIsAllowed() {
        RentalPropertyResponse property = publishedProperty("occupancy-types", "100.00");
        LocalDate start = stayPolicy.today().plusDays(10);
        LocalDate end = start.plusDays(7);

        for (RentalOccupancyType type : RentalOccupancyType.values()) {
            if (type == RentalOccupancyType.BOOKING) {
                continue;
            }
            RentalOccupancyResponse occupancy = occupancyService.createManual(
                    property.id(),
                    new UpsertRentalOccupancyRequest(start, end, type, "test"),
                    ADMIN_ACTOR_ID
            );
            Assertions.assertThrows(
                    RentalDatesNotAvailableException.class,
                    () -> bookingService.quote(
                            new RentalBookingQuoteRequest(property.id(), start, end)
                    ),
                    type.name()
            );
            RentalOccupancyType competingType = type == RentalOccupancyType.MAINTENANCE
                    ? RentalOccupancyType.OWNER_BLOCK
                    : RentalOccupancyType.MAINTENANCE;
            Assertions.assertThrows(
                    RentalDatesNotAvailableException.class,
                    () -> occupancyService.createManual(
                            property.id(),
                            new UpsertRentalOccupancyRequest(
                                    start.plusDays(1),
                                    end.plusDays(1),
                                    competingType,
                                    "overlap"
                            ),
                            ADMIN_ACTOR_ID
                    ),
                    type.name() + " vs " + competingType.name()
            );
            occupancyService.deleteManual(property.id(), occupancy.id());
        }

        CurrentCustomer firstCustomer = RentalTestFixtures.customer(customerAccountService, "910002");
        CurrentCustomer secondCustomer = RentalTestFixtures.customer(customerAccountService, "910003");
        RentalBookingResponse first = bookingService.create(
                firstCustomer,
                request(property.id(), start, end)
        );

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        RentalDatesNotAvailableException.class,
                        () -> bookingService.quote(
                                new RentalBookingQuoteRequest(property.id(), start.plusDays(1), end.plusDays(1))
                        )
                ),
                () -> Assertions.assertThrows(
                        RentalDatesNotAvailableException.class,
                        () -> occupancyService.createManual(
                                property.id(),
                                new UpsertRentalOccupancyRequest(
                                        start.plusDays(1),
                                        end.plusDays(1),
                                        RentalOccupancyType.MAINTENANCE,
                                        "booking overlap"
                                ),
                                ADMIN_ACTOR_ID
                        )
                ),
                () -> Assertions.assertDoesNotThrow(() -> bookingService.create(
                        secondCustomer,
                        request(property.id(), end, end.plusDays(7))
                )),
                () -> Assertions.assertEquals(
                        RentalBookingStatus.CONFIRMED,
                        bookingRepository.findById(first.id()).orElseThrow().getStatus()
                )
        );
    }

    @Test
    void customerActiveBookingLimit_enforced() {
        CurrentCustomer customer = RentalTestFixtures.customer(customerAccountService, "910004");
        RentalPropertyResponse property = publishedProperty("booking-limit", "100.00");
        LocalDate firstCheckIn = stayPolicy.today().plusDays(1);

        for (int index = 0; index < 3; index++) {
            LocalDate checkIn = firstCheckIn.plusDays(index * 7L);
            bookingService.create(customer, request(property.id(), checkIn, checkIn.plusDays(7)));
        }
        LocalDate fourthCheckIn = firstCheckIn.plusDays(21);

        Assertions.assertThrows(
                RentalActiveBookingLimitExceededException.class,
                () -> bookingService.create(
                        customer,
                        request(property.id(), fourthCheckIn, fourthCheckIn.plusDays(7))
                )
        );
        Assertions.assertEquals(3, bookingRepository.findAll().size());
    }

    @Test
    void customerCancellation_releasesOccupancyAndKeepsBookingHistory() {
        CurrentCustomer customer = RentalTestFixtures.customer(customerAccountService, "910005");
        RentalPropertyResponse property = publishedProperty("customer-cancel", "100.00");
        LocalDate checkIn = stayPolicy.today().plusDays(3);
        LocalDate checkOut = checkIn.plusDays(7);
        RentalBookingResponse booking = bookingService.create(
                customer,
                request(property.id(), checkIn, checkOut)
        );

        RentalBookingResponse cancelled = bookingService.cancel(customer, booking.id());

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        RentalBookingStatus.CANCELLED_BY_CUSTOMER,
                        cancelled.status()
                ),
                () -> Assertions.assertTrue(bookingRepository.existsById(booking.id())),
                () -> Assertions.assertTrue(
                        occupancyService.publicAvailability(
                                property.id(), checkIn, checkOut
                        ).unavailableRanges().isEmpty()
                ),
                () -> Assertions.assertDoesNotThrow(() -> bookingService.quote(
                        new RentalBookingQuoteRequest(property.id(), checkIn, checkOut)
                ))
        );
    }

    @Test
    void adminCancellation_canReleaseOrReplaceBookingWithOwnerBlock() {
        CurrentCustomer customer = RentalTestFixtures.customer(customerAccountService, "910006");
        RentalPropertyResponse property = publishedProperty("admin-cancel", "100.00");
        LocalDate firstStart = stayPolicy.today().plusDays(10);
        LocalDate firstEnd = firstStart.plusDays(7);
        RentalBookingResponse released = bookingService.create(
                customer,
                request(property.id(), firstStart, firstEnd)
        );

        adminBookingService.cancel(
                ADMIN_ACTOR_ID,
                released.id(),
                new AdminCancelRentalBookingRequest("Changed plans", false)
        );

        LocalDate secondStart = firstEnd;
        LocalDate secondEnd = secondStart.plusDays(7);
        RentalBookingResponse retained = bookingService.create(
                customer,
                request(property.id(), secondStart, secondEnd)
        );
        adminBookingService.cancel(
                ADMIN_ACTOR_ID,
                retained.id(),
                new AdminCancelRentalBookingRequest("Owner requested a block", true)
        );

        List<RentalOccupancyResponse> occupancies = occupancyService.adminOccupancies(
                property.id(),
                firstStart,
                secondEnd
        );
        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(() -> bookingService.quote(
                        new RentalBookingQuoteRequest(property.id(), firstStart, firstEnd)
                )),
                () -> Assertions.assertThrows(
                        RentalDatesNotAvailableException.class,
                        () -> bookingService.quote(
                                new RentalBookingQuoteRequest(property.id(), secondStart, secondEnd)
                        )
                ),
                () -> Assertions.assertEquals(1, occupancies.size()),
                () -> Assertions.assertEquals(RentalOccupancyType.OWNER_BLOCK, occupancies.getFirst().type()),
                () -> Assertions.assertNull(occupancies.getFirst().bookingId())
        );
    }

    private RentalPropertyResponse publishedProperty(String slug, String dailyPrice) {
        return RentalTestFixtures.publishedProperty(
                propertyService,
                propertyMediaService,
                slug,
                new BigDecimal(dailyPrice)
        );
    }

    private static CreateRentalBookingRequest request(
            long propertyId,
            LocalDate checkIn,
            LocalDate checkOut
    ) {
        return new CreateRentalBookingRequest(
                propertyId,
                checkIn,
                checkOut,
                2,
                "+90 555 123 45 67",
                "Late arrival"
        );
    }
}
