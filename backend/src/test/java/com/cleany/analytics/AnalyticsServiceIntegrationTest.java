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
        jdbcTemplate.update("delete from rental_transfer_action_event");
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
                () -> Assertions.assertEquals(5, result.businessHealth().completedTasks()),
                () -> Assertions.assertEquals(3, result.businessHealth().activeCustomers()),
                () -> Assertions.assertEquals(
                        new BigDecimal("1.6667"),
                        result.businessHealth().completedTasksPerActiveCustomer()
                ),
                () -> Assertions.assertEquals(2, result.businessHealth().customersWithTwoPlusCompletedTasks()),
                () -> Assertions.assertEquals(1, result.businessHealth().customersUsingTwoPlusServices()),
                () -> Assertions.assertEquals(
                        new BigDecimal("0.3333"),
                        result.businessHealth().crossServiceCustomerRate()
                ),
                () -> Assertions.assertNull(result.retention().repeat30Days().rate()),
                () -> Assertions.assertNull(result.retention().repeat90Days().rate()),
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
                () -> Assertions.assertEquals(0, empty.businessHealth().completedTasks()),
                () -> Assertions.assertEquals(BigDecimal.ZERO.setScale(4), empty.businessHealth().completedTasksPerActiveCustomer()),
                () -> Assertions.assertNull(empty.retention().repeat30Days().rate()),
                () -> Assertions.assertNull(empty.retention().secondOrderConversion().rate()),
                () -> Assertions.assertNull(empty.retention().medianDaysToSecondTask()),
                () -> Assertions.assertTrue(empty.averageChecks().isEmpty()),
                () -> Assertions.assertTrue(empty.acquisition().isEmpty())
        );
    }

    @Test
    void reminderAnalytics_attributesTypedTargetsAndUsesNullableOperationalCreation() {
        CurrentCustomer cleaningCustomer = customerAt(PERIOD_EVENT.minusSeconds(7200));
        completeCleaning(cleaningCustomer, PERIOD_EVENT.minusSeconds(3600), 1);
        long cleaningSourceId = jdbcTemplate.queryForObject(
                "select max(id) from cleaning_order where customer_id = ?",
                Long.class,
                cleaningCustomer.customerId()
        );
        insertReminder(
                cleaningCustomer.customerId(),
                "CLEANING_REPEAT",
                "CLEANING",
                cleaningSourceId,
                14,
                PERIOD_EVENT
        );
        CleaningOrder repeated = cleaningOrderService.createOrder(
                cleaningCustomer,
                new CreateCleaningOrderCommand(
                        ServiceArea.MAHMUTLAR,
                        "Reminder analytics repeat",
                        ApartmentType.ONE_PLUS_ONE,
                        false,
                        CleaningType.REGULAR,
                        LocalDate.now(ZoneId.of("Europe/Istanbul")).plusDays(1),
                        "+905551112233",
                        null,
                        null,
                        null
                ),
                cleaningSourceId
        );
        jdbcTemplate.update(
                "update cleaning_order set status = 'COMPLETED', created_at = ?, completed_at = ? where id = ?",
                Timestamp.from(PERIOD_EVENT.plusSeconds(60)),
                Timestamp.from(PERIOD_EVENT.plusSeconds(120)),
                repeated.getId()
        );

        CurrentCustomer transferCustomer = customerAt(PERIOD_EVENT.minusSeconds(7200));
        completeTransfer(transferCustomer, PERIOD_EVENT.plusSeconds(300), new BigDecimal("1900.00"));
        long transferSourceId = jdbcTemplate.queryForObject(
                "select max(id) from transfer_booking where customer_id = ?",
                Long.class,
                transferCustomer.customerId()
        );
        insertReminder(
                transferCustomer.customerId(),
                "TRANSFER_UPCOMING",
                "TRANSFER",
                transferSourceId,
                null,
                PERIOD_EVENT.plusSeconds(180)
        );

        AnalyticsOverviewResponse all = analyticsService.overview(PERIOD_DAY, PERIOD_DAY, AnalyticsServiceDimension.ALL);
        AnalyticsOverviewResponse cleaning = analyticsService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.CLEANING
        );
        AnalyticsOverviewResponse transfer = analyticsService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.TRANSFER
        );
        AnalyticsReminderMetric cleaningMetric = cleaning.reminders().getFirst();
        AnalyticsReminderMetric transferMetric = transfer.reminders().getFirst();

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, all.reminders().size()),
                () -> Assertions.assertEquals(1, cleaning.reminders().size()),
                () -> Assertions.assertEquals(1, cleaningMetric.notificationsCreated()),
                () -> Assertions.assertEquals(1L, cleaningMetric.targetTasksCreated()),
                () -> Assertions.assertEquals(1, cleaningMetric.targetTasksCompleted()),
                () -> Assertions.assertEquals(new BigDecimal("1.0000"), cleaningMetric.creationRate()),
                () -> Assertions.assertEquals(1, transferMetric.notificationsCreated()),
                () -> Assertions.assertNull(transferMetric.targetTasksCreated()),
                () -> Assertions.assertNull(transferMetric.creationRate()),
                () -> Assertions.assertEquals(new BigDecimal("1.0000"), transferMetric.completionRate())
        );
    }

    @Test
    void overview_reportsRepeatActionFunnelBySourceService() {
        CurrentCustomer customer = customerAt(PERIOD_EVENT.minusSeconds(3600));
        CreateCleaningOrderCommand command = new CreateCleaningOrderCommand(
                ServiceArea.MAHMUTLAR,
                "Repeat analytics address",
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                LocalDate.now(ZoneId.of("Europe/Istanbul")).plusDays(1),
                "+905551112233",
                null,
                null,
                null
        );
        CleaningOrder source = cleaningOrderService.createOrder(customer, command);
        jdbcTemplate.update(
                "update cleaning_order set status = 'COMPLETED', completed_at = ? where id = ?",
                Timestamp.from(PERIOD_EVENT),
                source.getId()
        );
        CleaningOrder repeated = cleaningOrderService.createOrder(customer, command, source.getId());
        jdbcTemplate.update(
                "update cleaning_order set created_at = ?, status = 'COMPLETED', completed_at = ? where id = ?",
                Timestamp.from(PERIOD_EVENT.plusSeconds(3600)),
                Timestamp.from(PERIOD_EVENT.plusSeconds(7200)),
                repeated.getId()
        );
        jdbcTemplate.update(
                """
                insert into repeat_action_event(customer_id, service, source_entity_id, event_type, occurred_at)
                values (?, 'CLEANING', ?, 'CTA_SHOWN', ?),
                       (?, 'CLEANING', ?, 'PREFILL_STARTED', ?)
                """,
                customer.customerId(), source.getId(), Timestamp.from(PERIOD_EVENT.plusSeconds(300)),
                customer.customerId(), source.getId(), Timestamp.from(PERIOD_EVENT.plusSeconds(600))
        );

        AnalyticsOverviewResponse all = analyticsService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.ALL
        );
        AnalyticsRepeatActionMetric metric = all.repeatActions().getFirst();
        AnalyticsOverviewResponse transfer = analyticsService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.TRANSFER
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, all.repeatActions().size()),
                () -> Assertions.assertEquals(PlatformService.CLEANING, metric.service()),
                () -> Assertions.assertEquals(1, metric.shownSources()),
                () -> Assertions.assertEquals(1, metric.startedSources()),
                () -> Assertions.assertEquals(1, metric.createdRepeatSources()),
                () -> Assertions.assertEquals(1, metric.completedRepeatSources()),
                () -> Assertions.assertEquals(new BigDecimal("1.0000"), metric.startRate()),
                () -> Assertions.assertEquals(new BigDecimal("1.0000"), metric.completionRate()),
                () -> Assertions.assertEquals(new BigDecimal("1.0"), metric.medianHoursToRepeat()),
                () -> Assertions.assertTrue(transfer.repeatActions().isEmpty())
        );
    }

    @Test
    void overview_reportsRentalToTransferFunnelByContextAndSourceService() {
        CurrentCustomer customer = customerAt(PERIOD_EVENT.minusSeconds(3600));
        RentalPropertyResponse draft = rentalPropertyService.createDraft();
        rentalPropertyService.update(draft.id(), new RentalPropertyDetails(
                "Контекст трансфера",
                "Transfer context property",
                "Rental to Transfer analytics property",
                "Махмутлар",
                "Analytics contextual address",
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
        LocalDate checkIn = rentalStayPolicy.today().plusDays(2);
        RentalBookingResponse rental = rentalBookingService.create(customer, new CreateRentalBookingRequest(
                draft.id(),
                RentalTermType.DATE_RANGE,
                checkIn,
                checkIn.plusDays(7),
                null,
                1,
                "+905551112233",
                null
        ));
        var airport = transferAirportRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        var vehicle = transferVehicleRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        transferPriceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                TransferDirection.FROM_AIRPORT,
                new BigDecimal("2500.00"),
                "TRY",
                true,
                PERIOD_EVENT.minusSeconds(60)
        ));
        TransferBookingResponse transfer = transferBookingService.create(
                customer,
                new CreateTransferBookingRequest(
                        TransferDirection.FROM_AIRPORT,
                        airport.getId(),
                        vehicle.getId(),
                        transferBookingPolicy.earliestBookingDate(),
                        LocalTime.of(10, 0),
                        "Analytics contextual address",
                        1,
                        0,
                        "TK123",
                        LocalTime.of(9, 30),
                        "+905551112233",
                        null,
                        null,
                        null
                )
        );
        TransferDriver driver = transferDriverRepository.saveAndFlush(new TransferDriver(
                "Context analytics driver",
                "+905551118888",
                true,
                null,
                PERIOD_EVENT.plusSeconds(8000)
        ));
        jdbcTemplate.update("""
                insert into rental_transfer_action_event(
                    customer_id, rental_booking_id, context_type, event_type, occurred_at
                ) values
                    (?, ?, 'ARRIVAL', 'CTA_SHOWN', ?),
                    (?, ?, 'ARRIVAL', 'PREFILL_STARTED', ?),
                    (?, ?, 'CHECKOUT', 'CTA_SHOWN', ?)
                """,
                customer.customerId(), rental.id(), Timestamp.from(PERIOD_EVENT),
                customer.customerId(), rental.id(), Timestamp.from(PERIOD_EVENT.plusSeconds(3600)),
                customer.customerId(), rental.id(), Timestamp.from(PERIOD_EVENT.plusSeconds(60))
        );
        jdbcTemplate.update("""
                update transfer_booking
                   set source_rental_booking_id = ?, rental_context_type = 'ARRIVAL',
                       created_at = ?, status = 'COMPLETED', driver_id = ?,
                       confirmed_at = ?, completed_at = ?
                 where id = ?
                """,
                rental.id(),
                Timestamp.from(PERIOD_EVENT.plusSeconds(7200)),
                driver.getId(),
                Timestamp.from(PERIOD_EVENT.plusSeconds(9000)),
                Timestamp.from(PERIOD_EVENT.plusSeconds(10800)),
                transfer.id()
        );

        AnalyticsOverviewResponse all = analyticsService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.ALL
        );
        AnalyticsOverviewResponse rentalOnly = analyticsService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.RENTAL
        );
        AnalyticsOverviewResponse transferOnly = analyticsService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.TRANSFER
        );
        AnalyticsActionFunnelMetric total = all.rentalToTransfer().total();
        Map<String, AnalyticsActionFunnelMetric> contexts = all.rentalToTransfer().byContext().stream()
                .collect(Collectors.toMap(metric -> metric.context().name(), AnalyticsRentalTransferContextMetric::funnel));

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, total.shownSources()),
                () -> Assertions.assertEquals(1, total.startedSources()),
                () -> Assertions.assertEquals(1, total.createdSources()),
                () -> Assertions.assertEquals(1, total.completedSources()),
                () -> Assertions.assertEquals(new BigDecimal("0.5000"), total.startRate()),
                () -> Assertions.assertEquals(new BigDecimal("0.5000"), total.creationRate()),
                () -> Assertions.assertEquals(new BigDecimal("1.0000"), total.completionRate()),
                () -> Assertions.assertEquals(new BigDecimal("2.0"), total.medianHoursToCreation()),
                () -> Assertions.assertEquals(2, contexts.size()),
                () -> Assertions.assertEquals(1, contexts.get("ARRIVAL").completedSources()),
                () -> Assertions.assertEquals(0, contexts.get("CHECKOUT").startedSources()),
                () -> Assertions.assertEquals(2, rentalOnly.rentalToTransfer().total().shownSources()),
                () -> Assertions.assertEquals(0, transferOnly.rentalToTransfer().total().shownSources())
        );
    }

    @Test
    void overview_retentionUsesMatureCohortsAndInclusiveThirtyAndNinetyDayBoundaries() {
        Instant firstA = Instant.parse("2026-01-02T10:00:00Z");
        CurrentCustomer exactThirtyDays = customerAt(firstA.minusSeconds(60));
        completeCleaning(exactThirtyDays, firstA, 1);
        completeCleaning(exactThirtyDays, firstA.plusSeconds(30L * 86400), 1);

        Instant firstB = Instant.parse("2026-01-03T10:00:00Z");
        CurrentCustomer afterThirtyDays = customerAt(firstB.minusSeconds(60));
        completeCleaning(afterThirtyDays, firstB, 1);
        completeRental(afterThirtyDays, firstB.plusSeconds(30L * 86400 + 1));

        Instant firstC = Instant.parse("2026-01-04T10:00:00Z");
        CurrentCustomer exactNinetyDays = customerAt(firstC.minusSeconds(60));
        completeCleaning(exactNinetyDays, firstC, 1);
        completeRental(exactNinetyDays, firstC.plusSeconds(90L * 86400));

        Instant firstD = Instant.parse("2026-01-05T10:00:00Z");
        CurrentCustomer afterNinetyDays = customerAt(firstD.minusSeconds(60));
        completeCleaning(afterNinetyDays, firstD, 1);
        completeRental(afterNinetyDays, firstD.plusSeconds(90L * 86400 + 1));

        Instant immatureFirst = Instant.parse("2026-04-15T10:00:00Z");
        completeCleaning(customerAt(immatureFirst.minusSeconds(60)), immatureFirst, 1);

        AnalyticsOverviewResponse result = analyticsService.overview(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 4, 30),
                AnalyticsServiceDimension.CLEANING
        );

        Assertions.assertAll(
                () -> assertCohort(result.retention().repeat30Days(), 4, 1, "0.2500"),
                () -> assertCohort(result.retention().repeat90Days(), 4, 3, "0.7500"),
                () -> assertCohort(result.retention().secondOrderConversion(), 4, 3, "0.7500"),
                () -> Assertions.assertEquals(
                        new BigDecimal("30.0"),
                        result.retention().medianDaysToSecondTask()
                )
        );
    }

    @Test
    void overview_transitionsUseOnlyImmediateSecondTaskAndFilterByFirstService() {
        Instant rentalFirst = Instant.parse("2026-01-05T10:00:00Z");
        CurrentCustomer rentalToCleaning = customerAt(rentalFirst.minusSeconds(60));
        completeRental(rentalToCleaning, rentalFirst);
        completeCleaning(rentalToCleaning, rentalFirst.plusSeconds(86400), 1);
        completeTransfer(rentalToCleaning, rentalFirst.plusSeconds(2 * 86400), new BigDecimal("2000.00"));

        Instant secondRentalFirst = Instant.parse("2026-01-06T10:00:00Z");
        CurrentCustomer rentalToTransfer = customerAt(secondRentalFirst.minusSeconds(60));
        completeRental(rentalToTransfer, secondRentalFirst);
        completeTransfer(rentalToTransfer, secondRentalFirst.plusSeconds(86400), new BigDecimal("2000.00"));

        Instant cleaningFirst = Instant.parse("2026-01-07T10:00:00Z");
        CurrentCustomer cleaningRepeat = customerAt(cleaningFirst.minusSeconds(60));
        completeCleaning(cleaningRepeat, cleaningFirst, 1);
        completeCleaning(cleaningRepeat, cleaningFirst.plusSeconds(86400), 1);

        Instant transferFirst = Instant.parse("2026-01-08T10:00:00Z");
        CurrentCustomer transferRepeat = customerAt(transferFirst.minusSeconds(60));
        completeTransfer(transferRepeat, transferFirst, new BigDecimal("2000.00"));
        completeTransfer(transferRepeat, transferFirst.plusSeconds(86400), new BigDecimal("2000.00"));

        AnalyticsOverviewResponse all = analyticsService.overview(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 1),
                AnalyticsServiceDimension.ALL
        );
        AnalyticsOverviewResponse rental = analyticsService.overview(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 1),
                AnalyticsServiceDimension.RENTAL
        );
        Map<String, AnalyticsTransitionMetric> transitions = all.transitions().stream()
                .collect(Collectors.toMap(this::transitionKey, Function.identity()));

        Assertions.assertAll(
                () -> assertTransition(transitions.get("CLEANING-CLEANING"), 1, 1, "1.0000"),
                () -> assertTransition(transitions.get("RENTAL-TRANSFER"), 2, 1, "0.5000"),
                () -> assertTransition(transitions.get("RENTAL-CLEANING"), 2, 1, "0.5000"),
                () -> assertTransition(transitions.get("TRANSFER-TRANSFER"), 1, 1, "1.0000"),
                () -> Assertions.assertEquals(2, rental.transitions().size()),
                () -> Assertions.assertTrue(rental.transitions().stream()
                        .allMatch(metric -> metric.fromService() == PlatformService.RENTAL)),
                () -> Assertions.assertNull(rental.retention().repeat30Days().rate())
        );
    }

    @Test
    void overview_usesIstanbulCalendarBoundariesAndKeepsCurrenciesSeparate() {
        CurrentCustomer beforeDay = customerAt(Instant.parse("2026-08-27T20:00:00Z"));
        completeCleaning(beforeDay, Instant.parse("2026-08-27T20:59:59Z"), 1);
        CurrentCustomer atDayStart = customerAt(Instant.parse("2026-08-27T21:00:00Z"));
        completeCleaning(atDayStart, Instant.parse("2026-08-27T21:00:00Z"), 1);
        CurrentCustomer usdCustomer = customerAt(Instant.parse("2026-08-28T09:00:00Z"));
        completeCleaning(usdCustomer, Instant.parse("2026-08-28T10:00:00Z"), 1);
        jdbcTemplate.update(
                "update cleaning_order set currency = 'USD' where customer_id = ?",
                usdCustomer.customerId()
        );
        CurrentCustomer atNextDay = customerAt(Instant.parse("2026-08-28T20:00:00Z"));
        completeCleaning(atNextDay, Instant.parse("2026-08-28T21:00:00Z"), 1);

        AnalyticsOverviewResponse result = analyticsService.overview(
                PERIOD_DAY,
                PERIOD_DAY,
                AnalyticsServiceDimension.CLEANING
        );
        Map<String, AverageCheckMetric> checks = result.averageChecks().stream()
                .collect(Collectors.toMap(AverageCheckMetric::currency, Function.identity()));

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.businessHealth().completedTasks()),
                () -> Assertions.assertEquals(2, result.businessHealth().activeCustomers()),
                () -> Assertions.assertEquals(2, checks.size()),
                () -> Assertions.assertEquals(new BigDecimal("1100.00"), checks.get("TRY").amount()),
                () -> Assertions.assertEquals(new BigDecimal("1100.00"), checks.get("USD").amount())
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
                () -> Assertions.assertEquals(0, result.customers().repeatCustomers()),
                () -> Assertions.assertEquals(1, result.businessHealth().completedTasks()),
                () -> Assertions.assertEquals(1, result.businessHealth().activeCustomers())
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

    private void insertReminder(
            long customerId,
            String type,
            String sourceService,
            long sourceEntityId,
            Integer intervalDays,
            Instant notifiedAt
    ) {
        jdbcTemplate.update("""
                insert into customer_reminder (
                    customer_id, type, source_service, source_entity_id, scheduled_date,
                    cleaning_interval_days, status, created_at, updated_at, notified_at
                ) values (?, ?, ?, ?, ?, ?, 'NOTIFIED', ?, ?, ?)
                """,
                customerId,
                type,
                sourceService,
                sourceEntityId,
                PERIOD_DAY,
                intervalDays,
                Timestamp.from(notifiedAt.minusSeconds(60)),
                Timestamp.from(notifiedAt),
                Timestamp.from(notifiedAt)
        );
    }

    private void completeTransfer(
            CurrentCustomer customer,
            Instant completedAt,
            BigDecimal amount
    ) {
        var airport = transferAirportRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        var vehicle = transferVehicleRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        transferPriceRepository.findByAirport_IdAndVehicleType_IdAndDirection(
                airport.getId(),
                vehicle.getId(),
                TransferDirection.TO_AIRPORT
        ).orElseGet(() -> transferPriceRepository.saveAndFlush(new TransferPrice(
                airport, vehicle, TransferDirection.TO_AIRPORT,
                amount, "TRY", true, completedAt.minusSeconds(120)
        )));
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
                        null,
                        null,
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

    private String transitionKey(AnalyticsTransitionMetric metric) {
        return metric.fromService() + "-" + metric.toService();
    }

    private static void assertCohort(
            AnalyticsCohortMetric metric,
            long cohortCustomers,
            long convertedCustomers,
            String rate
    ) {
        Assertions.assertAll(
                () -> Assertions.assertEquals(cohortCustomers, metric.cohortCustomers()),
                () -> Assertions.assertEquals(convertedCustomers, metric.convertedCustomers()),
                () -> Assertions.assertEquals(new BigDecimal(rate), metric.rate())
        );
    }

    private static void assertTransition(
            AnalyticsTransitionMetric metric,
            long cohortCustomers,
            long convertedCustomers,
            String rate
    ) {
        Assertions.assertNotNull(metric);
        Assertions.assertAll(
                () -> Assertions.assertEquals(cohortCustomers, metric.cohortCustomers()),
                () -> Assertions.assertEquals(convertedCustomers, metric.convertedCustomers()),
                () -> Assertions.assertEquals(new BigDecimal(rate), metric.conversionRate())
        );
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
