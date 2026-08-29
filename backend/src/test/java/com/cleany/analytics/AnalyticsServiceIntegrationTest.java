package com.cleany.analytics;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.catalog.PlatformService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.CustomerIdentityTestFixture;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderService;
import com.cleany.order.CleaningType;
import com.cleany.order.CreateCleaningOrderCommand;
import com.cleany.order.ServiceArea;
import com.cleany.rental.CreateRentalBookingRequest;
import com.cleany.rental.RentalAmenity;
import com.cleany.rental.RentalBookingResponse;
import com.cleany.rental.RentalBookingService;
import com.cleany.rental.RentalPropertyDetails;
import com.cleany.rental.RentalPropertyResponse;
import com.cleany.rental.RentalPropertyService;
import com.cleany.rental.RentalStayPolicy;
import com.cleany.rental.RentalTermType;
import com.cleany.transfer.CreateTransferBookingRequest;
import com.cleany.transfer.TransferAirportRepository;
import com.cleany.transfer.TransferBookingPolicy;
import com.cleany.transfer.TransferBookingResponse;
import com.cleany.transfer.TransferBookingService;
import com.cleany.transfer.TransferDirection;
import com.cleany.transfer.TransferDriver;
import com.cleany.transfer.TransferDriverRepository;
import com.cleany.transfer.TransferPrice;
import com.cleany.transfer.TransferPriceRepository;
import com.cleany.transfer.TransferVehicleTypeRepository;

class AnalyticsServiceIntegrationTest extends BaseIntegrationTest {

    private static final LocalDate PERIOD_DAY = LocalDate.of(2026, 8, 28);
    private static final Instant PERIOD_EVENT = Instant.parse("2026-08-28T10:00:00Z");

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private AnalyticsQueryRepository analyticsQueryRepository;

    @Autowired
    private AcquisitionCampaignService campaignService;

    @Autowired
    private AcquisitionCampaignRepository campaignRepository;

    @Autowired
    private CustomerAttributionService attributionService;

    @Autowired
    private CleaningOrderService cleaningOrderService;

    @Autowired
    private RentalBookingService rentalBookingService;

    @Autowired
    private RentalPropertyService rentalPropertyService;

    @Autowired
    private RentalStayPolicy rentalStayPolicy;

    @Autowired private TransferBookingService transferBookingService;
    @Autowired private TransferBookingPolicy transferBookingPolicy;
    @Autowired private TransferAirportRepository transferAirportRepository;
    @Autowired private TransferVehicleTypeRepository transferVehicleRepository;
    @Autowired private TransferPriceRepository transferPriceRepository;
    @Autowired private TransferDriverRepository transferDriverRepository;

    @Autowired
    private CustomerAccountRepository accountRepository;

    @Autowired
    private CustomerExternalIdentityRepository identityRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from transfer_booking");
        transferDriverRepository.deleteAll();
        transferPriceRepository.deleteAll();
        jdbcTemplate.update("delete from rental_occupancy");
        jdbcTemplate.update("delete from rental_cleaning_benefit");
        jdbcTemplate.update("delete from rental_booking");
        jdbcTemplate.update("delete from rental_property_amenity");
        jdbcTemplate.update("delete from rental_property");
        jdbcTemplate.update("delete from cleaning_order");
        jdbcTemplate.update("delete from acquisition_campaign_entry");
        jdbcTemplate.update("delete from customer_acquisition");
        jdbcTemplate.update("delete from acquisition_campaign");
        jdbcTemplate.update("delete from customer_external_identity");
        jdbcTemplate.update("delete from customer_account");
        jdbcTemplate.update("update platform_service_state set status = 'ENABLED', version = version + 1 where service = 'TRANSFER'");
        clearPlatformServiceStateCache();
    }

    @Test
    void overview_usesBusinessEventTimeAndSeparatesCampaigns() {
        AcquisitionCampaignResponse cleaningCampaign = campaign(
                "cleaning-magnet",
                "Cleaning magnet",
                AcquisitionMedium.QR_MAGNET,
                AcquisitionTargetService.CLEANING
        );
        AcquisitionCampaignResponse rentalCampaign = campaign(
                "rental-sticker",
                "Rental sticker",
                AcquisitionMedium.QR_STICKER,
                AcquisitionTargetService.RENTAL
        );

        CurrentCustomer singleCleaning = customerAt(PERIOD_EVENT.minusSeconds(3600));
        attach(singleCleaning, cleaningCampaign);
        completeCleaning(singleCleaning, PERIOD_EVENT, 1);

        CurrentCustomer repeatCleaning = customerAt(PERIOD_EVENT.minusSeconds(3000));
        completeCleaning(repeatCleaning, PERIOD_EVENT.plusSeconds(60), 2);

        CurrentCustomer crossService = customerAt(PERIOD_EVENT.minusSeconds(2400));
        attach(crossService, rentalCampaign);
        completeCleaning(crossService, PERIOD_EVENT.plusSeconds(120), 1);
        completeRental(crossService, PERIOD_EVENT.plusSeconds(180));

        CurrentCustomer cancelledOnly = customerAt(PERIOD_EVENT.minusSeconds(1800));
        cancelCleaning(cancelledOnly);

        customerAt(Instant.parse("2026-08-27T20:59:59Z"));
        entries(cleaningCampaign.id(), 2);
        entries(rentalCampaign.id(), 3);

        AnalyticsOverviewResponse result = analyticsService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.ALL
        );
        Map<PlatformService, AverageCheckMetric> checks = result.averageChecks().stream()
                .collect(Collectors.toMap(AverageCheckMetric::service, Function.identity()));
        Map<String, AcquisitionMetric> campaigns = result.acquisition().stream()
                .collect(Collectors.toMap(this::metricKey, Function.identity()));

        Assertions.assertAll(
                () -> Assertions.assertEquals(4, result.customers().newCustomers()),
                () -> Assertions.assertEquals(3, result.customers().activeCustomers()),
                () -> Assertions.assertEquals(2, result.customers().repeatCustomers()),
                () -> Assertions.assertEquals(new BigDecimal("0.6667"), result.customers().repeatRate()),
                () -> Assertions.assertEquals(new BigDecimal("1100.00"), checks.get(PlatformService.CLEANING).amount()),
                () -> Assertions.assertEquals(4, checks.get(PlatformService.CLEANING).completedTransactions()),
                () -> Assertions.assertEquals(new BigDecimal("700.00"), checks.get(PlatformService.RENTAL).amount()),
                () -> Assertions.assertEquals(1, checks.get(PlatformService.RENTAL).completedTransactions()),
                () -> assertMetric(campaigns.get("cleaning-magnet"), 2, 1, 1),
                () -> assertMetric(campaigns.get("rental-sticker"), 3, 1, 2),
                () -> assertMetric(campaigns.get("ORGANIC"), 0, 2, 2)
        );
    }

    @Test
    void overview_serviceFilterAndEmptyPeriodReturnConsistentMetrics() {
        CurrentCustomer cleaningCustomer = customerAt(PERIOD_EVENT.minusSeconds(1200));
        completeCleaning(cleaningCustomer, PERIOD_EVENT, 1);
        CurrentCustomer rentalCustomer = customerAt(PERIOD_EVENT.minusSeconds(600));
        completeRental(rentalCustomer, PERIOD_EVENT.plusSeconds(60));

        AnalyticsOverviewResponse cleaning = analyticsService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.CLEANING
        );
        AnalyticsOverviewResponse empty = analyticsService.overview(
                PERIOD_DAY.minusDays(10),
                PERIOD_DAY.minusDays(10),
                AnalyticsServiceDimension.ALL
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, cleaning.customers().newCustomers()),
                () -> Assertions.assertEquals(1, cleaning.customers().activeCustomers()),
                () -> Assertions.assertEquals(1, cleaning.averageChecks().size()),
                () -> Assertions.assertEquals(0, empty.customers().newCustomers()),
                () -> Assertions.assertEquals(0, empty.customers().activeCustomers()),
                () -> Assertions.assertEquals(BigDecimal.ZERO.setScale(4), empty.customers().repeatRate()),
                () -> Assertions.assertTrue(empty.averageChecks().isEmpty()),
                () -> Assertions.assertTrue(empty.acquisition().isEmpty())
        );
    }

    @Test
    void overview_commercialLaunchAtClampsRequestedPeriod() {
        customerAt(Instant.parse("2026-08-28T09:59:59Z"));
        CurrentCustomer commercialCustomer = customerAt(Instant.parse("2026-08-28T10:00:00Z"));
        completeCleaning(commercialCustomer, Instant.parse("2026-08-28T09:00:00Z"), 1);
        completeCleaning(commercialCustomer, Instant.parse("2026-08-28T11:00:00Z"), 1);
        AnalyticsService launchAwareService = new AnalyticsService(
                analyticsQueryRepository,
                new AnalyticsProperties(
                        ZoneId.of("Europe/Istanbul"),
                        Instant.parse("2026-08-28T10:00:00Z")
                )
        );

        AnalyticsOverviewResponse result = launchAwareService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.ALL
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.customers().newCustomers()),
                () -> Assertions.assertEquals(1, result.customers().activeCustomers()),
                () -> Assertions.assertEquals(0, result.customers().repeatCustomers())
        );
    }

    @Test
    void overview_transferFilterUsesCompletedPriceSnapshot() {
        CurrentCustomer customer = customerAt(PERIOD_EVENT.minusSeconds(600));
        completeTransfer(customer, PERIOD_EVENT, new BigDecimal("2450.00"));

        AnalyticsOverviewResponse result = analyticsService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.TRANSFER
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.customers().activeCustomers()),
                () -> Assertions.assertEquals(1, result.averageChecks().size()),
                () -> Assertions.assertEquals(PlatformService.TRANSFER, result.averageChecks().getFirst().service()),
                () -> Assertions.assertEquals(new BigDecimal("2450.00"), result.averageChecks().getFirst().amount())
        );
    }

    private CurrentCustomer customerAt(Instant createdAt) {
        var persisted = CustomerIdentityTestFixture.telegramIdentity(
                accountRepository,
                identityRepository,
                createdAt
        );
        return new CurrentCustomer(
                persisted.customerId(),
                persisted.externalIdentityId(),
                ExternalIdentityProvider.TELEGRAM,
                Long.toString(persisted.externalIdentityId()),
                "analytics-customer",
                "Analytics customer",
                "ru"
        );
    }

    private void attach(CurrentCustomer customer, AcquisitionCampaignResponse response) {
        AcquisitionCampaign campaign = campaignRepository.findById(response.id()).orElseThrow();
        attributionService.attachCampaign(
                customer.customerId(),
                campaign,
                PERIOD_EVENT.minusSeconds(7200),
                AttributionMethod.CAMPAIGN_LINK
        );
    }

    private void completeCleaning(CurrentCustomer customer, Instant completedAt, int count) {
        for (int index = 0; index < count; index++) {
            CleaningOrder order = cleaningOrderService.createOrder(customer, new CreateCleaningOrderCommand(
                    ServiceArea.MAHMUTLAR,
                    "Analytics address " + index,
                    ApartmentType.TWO_PLUS_ONE,
                    false,
                    CleaningType.REGULAR,
                    LocalDate.now(ZoneId.of("Europe/Istanbul")).plusDays(1),
                    "+905551112233",
                    null,
                    null,
                    null
            ));
            jdbcTemplate.update(
                    "update cleaning_order set status = 'COMPLETED', completed_at = ? where id = ?",
                    Timestamp.from(completedAt.plusSeconds(index)),
                    order.getId()
            );
        }
    }

    private void cancelCleaning(CurrentCustomer customer) {
        CleaningOrder order = cleaningOrderService.createOrder(customer, new CreateCleaningOrderCommand(
                ServiceArea.KESTEL,
                "Cancelled analytics order",
                ApartmentType.STUDIO,
                false,
                CleaningType.REGULAR,
                LocalDate.now(ZoneId.of("Europe/Istanbul")).plusDays(1),
                "+905551112233",
                null,
                null,
                null
        ));
        jdbcTemplate.update("update cleaning_order set status = 'CANCELLED' where id = ?", order.getId());
    }

    private void completeRental(CurrentCustomer customer, Instant completedAt) {
        RentalPropertyResponse draft = rentalPropertyService.createDraft();
        rentalPropertyService.update(draft.id(), new RentalPropertyDetails(
                "Аналитика",
                "Analytics property " + draft.id(),
                "Rental analytics test property",
                "Махмутлар",
                "Analytics rental address",
                1,
                1,
                1,
                2,
                new BigDecimal("50.00"),
                2,
                new BigDecimal("100.00"),
                "TRY",
                EnumSet.of(RentalAmenity.WIFI)
        ));
        jdbcTemplate.update("update rental_property set status = 'PUBLISHED' where id = ?", draft.id());
        LocalDate checkIn = rentalStayPolicy.today().plusDays(1);
        RentalBookingResponse booking = rentalBookingService.create(customer, new CreateRentalBookingRequest(
                draft.id(),
                RentalTermType.DATE_RANGE,
                checkIn,
                checkIn.plusDays(7),
                null,
                1,
                "+905551112233",
                null
        ));
        jdbcTemplate.update(
                "update rental_booking set status = 'COMPLETED', completed_at = ? where id = ?",
                Timestamp.from(completedAt),
                booking.id()
        );
    }

    private void completeTransfer(
            CurrentCustomer customer,
            Instant completedAt,
            BigDecimal amount
    ) {
        var airport = transferAirportRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        var vehicle = transferVehicleRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        transferPriceRepository.saveAndFlush(new TransferPrice(
                airport, vehicle, TransferDirection.TO_AIRPORT,
                amount, "TRY", true, completedAt.minusSeconds(120)
        ));
        TransferBookingResponse booking = transferBookingService.create(
                customer,
                new CreateTransferBookingRequest(
                        TransferDirection.TO_AIRPORT,
                        airport.getId(),
                        vehicle.getId(),
                        transferBookingPolicy.earliestBookingDate(),
                        LocalTime.of(10, 0),
                        "Analytics transfer address",
                        2,
                        2,
                        null,
                        null,
                        "+905551112233",
                        null
                )
        );
        TransferDriver driver = transferDriverRepository.saveAndFlush(new TransferDriver(
                "Analytics driver", "+905551119999", true, null, completedAt.minusSeconds(60)
        ));
        jdbcTemplate.update(
                """
                update transfer_booking
                   set status = 'COMPLETED', driver_id = ?, confirmed_at = ?, completed_at = ?
                 where id = ?
                """,
                driver.getId(), Timestamp.from(completedAt.minusSeconds(30)), Timestamp.from(completedAt), booking.id()
        );
    }

    private AcquisitionCampaignResponse campaign(
            String publicCode,
            String name,
            AcquisitionMedium medium,
            AcquisitionTargetService service
    ) {
        return campaignService.create(new CreateAcquisitionCampaignRequest(
                publicCode,
                name,
                AcquisitionChannel.QR,
                medium,
                service,
                null
        ));
    }

    private void entries(long campaignId, int count) {
        for (int index = 0; index < count; index++) {
            jdbcTemplate.update(
                    "insert into acquisition_campaign_entry(campaign_id, occurred_at, platform) values (?, ?, 'WEB')",
                    campaignId,
                    Timestamp.from(PERIOD_EVENT.plusSeconds(index))
            );
        }
    }

    private String metricKey(AcquisitionMetric metric) {
        return metric.campaignName() == null ? metric.channel().name() : metric.campaignName()
                .toLowerCase()
                .replace(' ', '-');
    }

    private static void assertMetric(
            AcquisitionMetric metric,
            long entries,
            long newCustomers,
            long completedTransactions
    ) {
        Assertions.assertNotNull(metric);
        Assertions.assertAll(
                () -> Assertions.assertEquals(entries, metric.entries()),
                () -> Assertions.assertEquals(newCustomers, metric.newCustomers()),
                () -> Assertions.assertEquals(completedTransactions, metric.completedTransactions())
        );
    }
}
