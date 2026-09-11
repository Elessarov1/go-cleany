package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.catalog.PlatformServiceNotAvailableException;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProviderReferenceRepository;
import com.cleany.pagination.InvalidCursorException;

class RentalSearchIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RentalSearchService searchService;

    @Autowired
    private RentalSearchController searchController;

    @Autowired
    private RentalSearchTrackingService trackingService;

    @Autowired
    private RentalBookingService bookingService;

    @Autowired
    private RentalPropertyService propertyService;

    @Autowired
    private RentalPropertyMediaService mediaService;

    @Autowired
    private RentalOccupancyRepository occupancyRepository;

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
        jdbcTemplate.update("delete from rental_search_event");
        jdbcTemplate.update("delete from rental_occupancy");
        bookingRepository.deleteAll();
        jdbcTemplate.update("delete from rental_search_execution");
        propertyMediaRepository.deleteAll();
        propertyRepository.deleteAll();
        providerReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
        jdbcTemplate.update("""
                update platform_service_state
                   set status = 'ENABLED', updated_by_customer_id = null
                 where service = 'RENTAL'
                """);
        clearPlatformServiceStateCache();
    }

    @Test
    void anonymousSearchFiltersWholeInclusivePeriodAndPreservesAdminOrder() {
        RentalPropertyResponse first = published("search-first", "100.00");
        RentalPropertyResponse second = published("search-second", "200.00");
        propertyService.reorder(List.of(second.id(), first.id()));
        LocalDate checkIn = stayPolicy.today().plusDays(10);
        LocalDate checkOut = checkIn.plusDays(7);
        occupancyRepository.create(
                second.id(),
                checkOut,
                checkOut.plusDays(2),
                RentalOccupancyType.MAINTENANCE,
                null,
                "Last occupied day overlap",
                java.time.Instant.now(),
                null
        );
        long accountsBefore = accountRepository.count();

        RentalSearchResponse available = searchService.search(
                RentalTermType.DATE_RANGE,
                checkIn,
                checkOut,
                null,
                2,
                null,
                null,
                null
        );
        RentalSearchResponse browseAll = searchService.search(
                null, null, null, null, null, null, null, null
        );
        RentalSearchResponse atCapacity = searchService.search(
                RentalTermType.DATE_RANGE, checkIn, checkOut, null, 5, null, null, null
        );
        RentalSearchResponse overCapacity = searchService.search(
                RentalTermType.DATE_RANGE, checkIn, checkOut, null, 6, null, null, null
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(List.of(first.id()), available.properties().stream()
                        .map(RentalSearchPropertyResponse::id).toList()),
                () -> Assertions.assertEquals(8, available.criteria().durationDays()),
                () -> Assertions.assertEquals("800.00", available.properties().getFirst()
                        .price().totalPrice().toPlainString()),
                () -> Assertions.assertTrue(available.properties().getFirst().coverUrl()
                        .contains("/card?v=")),
                () -> Assertions.assertEquals(List.of(second.id(), first.id()), browseAll.properties()
                        .stream().map(RentalSearchPropertyResponse::id).toList()),
                () -> Assertions.assertNull(browseAll.properties().getFirst().price()),
                () -> Assertions.assertEquals(List.of(first.id()), atCapacity.properties().stream()
                        .map(RentalSearchPropertyResponse::id).toList()),
                () -> Assertions.assertTrue(overCapacity.properties().isEmpty()),
                () -> Assertions.assertEquals(accountsBefore, accountRepository.count())
        );
    }

    @Test
    void cursorPaginationReturnsStablePagesAndOneExecutionPerSearch() {
        List<Long> expectedIds = IntStream.rangeClosed(1, 45)
                .mapToObj(index -> published("cursor-" + index, "100.00").id())
                .toList();

        RentalSearchResponse first = browse(null, null, null);
        RentalSearchResponse second = browse(first.nextCursor(), null, null);
        RentalSearchResponse third = browse(second.nextCursor(), null, null);
        List<Long> actualIds = java.util.stream.Stream.of(first, second, third)
                .flatMap(page -> page.properties().stream())
                .map(RentalSearchPropertyResponse::id)
                .toList();

        Assertions.assertAll(
                () -> Assertions.assertEquals(20, first.properties().size()),
                () -> Assertions.assertEquals(20, second.properties().size()),
                () -> Assertions.assertEquals(5, third.properties().size()),
                () -> Assertions.assertTrue(first.hasMore()),
                () -> Assertions.assertTrue(second.hasMore()),
                () -> Assertions.assertFalse(third.hasMore()),
                () -> Assertions.assertNull(third.nextCursor()),
                () -> Assertions.assertEquals(expectedIds, actualIds),
                () -> Assertions.assertEquals(first.searchExecutionId(), second.searchExecutionId()),
                () -> Assertions.assertEquals(first.searchExecutionId(), third.searchExecutionId()),
                () -> Assertions.assertEquals(1, jdbcTemplate.queryForObject(
                        "select count(*) from rental_search_execution",
                        Integer.class
                )),
                () -> Assertions.assertEquals(20, jdbcTemplate.queryForObject(
                        "select result_count from rental_search_execution where id = ?",
                        Integer.class,
                        first.searchExecutionId()
                ))
        );

        LocalDate checkIn = stayPolicy.today().plusDays(10);
        LocalDate checkOut = checkIn.plusDays(7);
        RentalSearchResponse dateFirst = searchService.search(
                RentalTermType.DATE_RANGE,
                checkIn,
                checkOut,
                null,
                2,
                null,
                20,
                first.searchExecutionId()
        );
        RentalSearchResponse dateSecond = searchService.search(
                RentalTermType.DATE_RANGE,
                checkIn,
                checkOut,
                null,
                2,
                dateFirst.nextCursor(),
                20,
                null
        );
        RentalSearchResponse monthlyFirst = searchService.search(
                RentalTermType.MONTHLY,
                checkIn,
                null,
                1,
                2,
                null,
                20,
                dateFirst.searchExecutionId()
        );
        RentalSearchResponse monthlySecond = searchService.search(
                RentalTermType.MONTHLY,
                checkIn,
                null,
                1,
                2,
                monthlyFirst.nextCursor(),
                20,
                null
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(20, dateSecond.properties().size()),
                () -> Assertions.assertEquals(dateFirst.searchExecutionId(), dateSecond.searchExecutionId()),
                () -> Assertions.assertEquals(20, monthlySecond.properties().size()),
                () -> Assertions.assertEquals(
                        monthlyFirst.searchExecutionId(),
                        monthlySecond.searchExecutionId()
                ),
                () -> Assertions.assertEquals(first.searchExecutionId(), jdbcTemplate.queryForObject(
                        "select previous_search_id from rental_search_execution where id = ?",
                        UUID.class,
                        dateFirst.searchExecutionId()
                )),
                () -> Assertions.assertEquals(dateFirst.searchExecutionId(), jdbcTemplate.queryForObject(
                        "select previous_search_id from rental_search_execution where id = ?",
                        UUID.class,
                        monthlyFirst.searchExecutionId()
                )),
                () -> Assertions.assertEquals(3, jdbcTemplate.queryForObject(
                        "select count(*) from rental_search_execution",
                        Integer.class
                ))
        );
    }

    @Test
    void invalidCursorSizeCriteriaAndPreviousSearchCombinationAreRejected() {
        published("invalid-cursor-first", "100.00");
        published("invalid-cursor-second", "100.00");
        RentalSearchResponse first = browse(null, 1, null);
        LocalDate checkIn = stayPolicy.today().plusDays(10);
        LocalDate checkOut = checkIn.plusDays(7);

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        InvalidCursorException.class,
                        () -> browse("not-a-cursor", 20, null)
                ),
                () -> Assertions.assertThrows(
                        InvalidCursorException.class,
                        () -> browse(null, 0, null)
                ),
                () -> Assertions.assertThrows(
                        InvalidCursorException.class,
                        () -> browse(null, 21, null)
                ),
                () -> Assertions.assertThrows(
                        InvalidCursorException.class,
                        () -> browse(first.nextCursor(), 1, first.searchExecutionId())
                ),
                () -> Assertions.assertThrows(
                        InvalidCursorException.class,
                        () -> searchService.search(
                                RentalTermType.DATE_RANGE,
                                checkIn,
                                checkOut,
                                null,
                                2,
                                first.nextCursor(),
                                1,
                                null
                        )
                )
        );
    }

    @Test
    void monthlySearchAndPublicQuoteUseTheSamePriceProjection() {
        RentalPropertyResponse property = published("monthly-search", "100.00");
        LocalDate checkIn = stayPolicy.today().plusDays(20);
        long accountsBefore = accountRepository.count();

        RentalSearchPropertyResponse searchPrice = searchService.search(
                RentalTermType.MONTHLY,
                checkIn,
                null,
                2,
                3,
                null,
                null,
                null
        ).properties().getFirst();
        RentalQuoteResponse quote = bookingService.quote(
                property.id(),
                RentalTermType.MONTHLY,
                checkIn,
                null,
                2,
                3
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals("3000.00", searchPrice.price()
                        .baseMonthlyPrice().toPlainString()),
                () -> Assertions.assertEquals("2700.00", searchPrice.price()
                        .monthlyPrice().toPlainString()),
                () -> Assertions.assertEquals(searchPrice.price(), quote.price()),
                () -> Assertions.assertEquals(searchPrice.price().durationDays(), quote.criteria().durationDays()),
                () -> Assertions.assertTrue(searchPrice.price().longTermDiscountApplied()),
                () -> Assertions.assertEquals(accountsBefore, accountRepository.count())
        );
    }

    @Test
    void changedPriceRejectsBeforeSavingAndCanBeConfirmedWithARefresh() {
        var customer = RentalTestFixtures.customer(customerAccountService, "search-price-customer");
        RentalPropertyResponse property = published("price-change", "100.00");
        LocalDate checkIn = stayPolicy.today().plusDays(10);
        LocalDate checkOut = checkIn.plusDays(7);
        RentalQuoteResponse oldQuote = bookingService.quote(
                property.id(), RentalTermType.DATE_RANGE, checkIn, checkOut, null, 2
        );
        propertyService.update(
                property.id(),
                RentalTestFixtures.details("price-change", new BigDecimal("120.00"))
        );
        CreateRentalBookingRequest stale = request(
                property.id(),
                checkIn,
                checkOut,
                oldQuote.price().totalPrice(),
                oldQuote.price().currency(),
                null
        );

        Assertions.assertThrows(
                RentalPriceChangedException.class,
                () -> bookingService.create(customer, stale)
        );
        Assertions.assertEquals(0, bookingRepository.count());

        RentalQuoteResponse refreshed = bookingService.quote(
                property.id(), RentalTermType.DATE_RANGE, checkIn, checkOut, null, 2
        );
        RentalBookingResponse created = bookingService.create(customer, request(
                property.id(),
                checkIn,
                checkOut,
                refreshed.price().totalPrice(),
                refreshed.price().currency(),
                null
        ));
        Assertions.assertEquals("960.00", created.totalPrice().toPlainString());
    }

    @Test
    void trackingIsIdempotentAndUnknownIdsAreIgnored() {
        published("tracking", "100.00");
        RentalSearchResponse response = searchController.search(
                null, null, null, null, null, null, null, null
        ).getBody();
        Assertions.assertNotNull(response);
        UUID executionId = response.searchExecutionId();

        trackingService.recordOpenedSafely(executionId);
        trackingService.recordOpenedSafely(executionId);
        trackingService.recordFirstCardSafely(executionId, 125);
        trackingService.recordFirstCardSafely(executionId, 240);
        trackingService.recordBookingConflictSafely(executionId);
        RentalSearchResponse followUp = searchController.search(
                null, null, null, null, null, null, null, executionId
        ).getBody();
        Assertions.assertNotNull(followUp);
        UUID unknown = UUID.randomUUID();
        trackingService.recordOpenedSafely(unknown);
        trackingService.recordBookingConflictSafely(unknown);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, jdbcTemplate.queryForObject(
                        "select count(*) from rental_search_execution where id = ?",
                        Integer.class,
                        executionId
                )),
                () -> Assertions.assertEquals(3, jdbcTemplate.queryForObject(
                        "select count(*) from rental_search_event where search_execution_id = ?",
                        Integer.class,
                        executionId
                )),
                () -> Assertions.assertEquals(125L, jdbcTemplate.queryForObject(
                        """
                        select duration_ms from rental_search_event
                         where search_execution_id = ? and event_type = 'FIRST_CARD_RENDERED'
                        """,
                        Long.class,
                        executionId
                )),
                () -> Assertions.assertEquals(executionId, jdbcTemplate.queryForObject(
                        "select previous_search_id from rental_search_execution where id = ?",
                        UUID.class,
                        followUp.searchExecutionId()
                ))
        );
    }

    @Test
    void invalidCombinationsAndDisabledServiceAreRejected() {
        LocalDate checkIn = stayPolicy.today().plusDays(10);
        Assertions.assertThrows(
                InvalidRentalBookingException.class,
                () -> searchService.search(
                        null, checkIn, null, null, null, null, null, null
                )
        );
        jdbcTemplate.update("""
                update platform_service_state set status = 'DISABLED', version = version + 1
                 where service = 'RENTAL'
                """);
        clearPlatformServiceStateCache();
        Assertions.assertThrows(
                PlatformServiceNotAvailableException.class,
                () -> searchService.search(
                        null, null, null, null, null, null, null, null
                )
        );
    }

    private RentalPropertyResponse published(String slug, String dailyPrice) {
        return RentalTestFixtures.publishedProperty(
                propertyService,
                mediaService,
                slug,
                new BigDecimal(dailyPrice)
        );
    }

    private RentalSearchResponse browse(String cursor, Integer size, UUID previousSearchId) {
        return searchService.search(
                null,
                null,
                null,
                null,
                null,
                cursor,
                size,
                previousSearchId
        );
    }

    private static CreateRentalBookingRequest request(
            long propertyId,
            LocalDate checkIn,
            LocalDate checkOut,
            BigDecimal expectedTotal,
            String currency,
            UUID executionId
    ) {
        return new CreateRentalBookingRequest(
                propertyId,
                RentalTermType.DATE_RANGE,
                checkIn,
                checkOut,
                null,
                2,
                "+905551234567",
                null,
                expectedTotal,
                currency,
                executionId
        );
    }
}
