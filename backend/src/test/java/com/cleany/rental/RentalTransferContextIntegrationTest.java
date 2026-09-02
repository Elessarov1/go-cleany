package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.catalog.PlatformServiceNotAvailableException;
import com.cleany.crossservice.rentaltransfer.RentalTransferAlreadyBookedException;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextAvailability;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextNotEligibleException;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextResponse;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;
import com.cleany.crossservice.rentaltransfer.RentalTransferPrefillResponse;
import com.cleany.crossservice.rentaltransfer.RentalTransferSourceRequest;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProviderReferenceRepository;
import com.cleany.reminder.CustomerReminderRepository;
import com.cleany.reminder.CustomerReminderStatus;
import com.cleany.reminder.CustomerReminderType;
import com.cleany.reminder.SmartReminderProcessingResult;
import com.cleany.reminder.SmartReminderService;
import com.cleany.transfer.CreateTransferBookingRequest;
import com.cleany.transfer.TransferAirport;
import com.cleany.transfer.TransferAirportRepository;
import com.cleany.transfer.TransferBooking;
import com.cleany.transfer.TransferBookingPolicy;
import com.cleany.transfer.TransferBookingRepository;
import com.cleany.transfer.TransferBookingResponse;
import com.cleany.transfer.TransferBookingService;
import com.cleany.transfer.TransferBookingStatus;
import com.cleany.transfer.TransferDirection;
import com.cleany.transfer.TransferPrice;
import com.cleany.transfer.TransferPriceRepository;
import com.cleany.transfer.TransferVehicleType;
import com.cleany.transfer.TransferVehicleTypeRepository;

class RentalTransferContextIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RentalTransferContextService contextService;

    @Autowired
    private RentalBookingService rentalBookingService;

    @Autowired
    private RentalBookingRepository rentalBookingRepository;

    @Autowired
    private RentalPropertyService rentalPropertyService;

    @Autowired
    private RentalPropertyMediaService rentalPropertyMediaService;

    @Autowired
    private RentalPropertyRepository rentalPropertyRepository;

    @Autowired
    private RentalPropertyMediaRepository rentalPropertyMediaRepository;

    @Autowired
    private TransferBookingService transferBookingService;

    @Autowired
    private TransferBookingRepository transferBookingRepository;

    @Autowired
    private TransferAirportRepository airportRepository;

    @Autowired
    private TransferVehicleTypeRepository vehicleRepository;

    @Autowired
    private TransferPriceRepository priceRepository;

    @Autowired
    private TransferBookingPolicy transferBookingPolicy;

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

    @Autowired
    private SmartReminderService smartReminderService;

    @Autowired
    private CustomerReminderRepository reminderRepository;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from rental_transfer_action_event");
        reminderRepository.deleteAll();
        transferBookingRepository.deleteAll();
        priceRepository.deleteAll();
        jdbcTemplate.update("delete from rental_cleaning_benefit");
        jdbcTemplate.update("delete from rental_occupancy");
        rentalBookingRepository.deleteAll();
        rentalPropertyMediaRepository.deleteAll();
        rentalPropertyRepository.deleteAll();
        providerReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
        jdbcTemplate.update("delete from customer_notification");
        jdbcTemplate.update("delete from customer_acquisition");
        jdbcTemplate.update("delete from customer_role");
        identityRepository.deleteAll();
        accountRepository.deleteAll();
        jdbcTemplate.update("""
                update platform_service_state
                   set status = 'ENABLED', updated_by_customer_id = null, version = version + 1
                 where service in ('RENTAL', 'TRANSFER')
                """);
        jdbcTemplate.update("update transfer_airport set enabled = true, version = version + 1");
        jdbcTemplate.update("update transfer_vehicle_type set enabled = true, version = version + 1");
        clearPlatformServiceStateCache();
    }

    @Test
    void confirmedRentalCheckout_createsOneDurableCatchUpReminder() {
        CurrentCustomer owner = RentalTestFixtures.customer(
                customerAccountService,
                "rental-checkout-reminder"
        );
        RentalBookingResponse rental = rental(owner, "rental-checkout-reminder-property");
        LocalDate checkout = transferBookingPolicy.earliestBookingDate().plusDays(2);
        jdbcTemplate.update(
                """
                update rental_booking
                   set check_in_date = ?, check_out_date = ?, duration_days = 7
                 where id = ?
                """,
                checkout.minusDays(7),
                checkout,
                rental.id()
        );

        SmartReminderProcessingResult first = smartReminderService.process();
        SmartReminderProcessingResult repeated = smartReminderService.process();
        var reminder = reminderRepository.findAll().getFirst();
        long notificationCount = jdbcTemplate.queryForObject(
                """
                select count(*)
                  from customer_notification
                 where customer_id = ?
                   and type = 'RENTAL_CHECKOUT_TRANSFER_REMINDER'
                """,
                Long.class,
                owner.customerId()
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, first.notified()),
                () -> Assertions.assertEquals(0, repeated.notified()),
                () -> Assertions.assertEquals(
                        CustomerReminderType.RENTAL_CHECKOUT_TRANSFER,
                        reminder.getType()
                ),
                () -> Assertions.assertEquals(CustomerReminderStatus.NOTIFIED, reminder.getStatus()),
                () -> Assertions.assertEquals(1, notificationCount)
        );
    }

    @Test
    void confirmedRentalOffersArrivalAndCheckoutAndCreatesTypedEditableTransfer() {
        CurrentCustomer owner = RentalTestFixtures.customer(customerAccountService, "rental-transfer-owner");
        CurrentCustomer stranger = RentalTestFixtures.customer(customerAccountService, "rental-transfer-stranger");
        RentalBookingResponse rental = rental(owner, "rental-transfer-main");
        TransferConfiguration configuration = transferConfiguration(TransferDirection.FROM_AIRPORT);

        RentalTransferContextResponse context = contextService.context(owner, rental.id());
        RentalTransferPrefillResponse prefill = contextService.prefill(
                owner,
                rental.id(),
                RentalTransferContextType.ARRIVAL
        );
        contextService.recordShown(owner, rental.id(), RentalTransferContextType.ARRIVAL);
        contextService.recordShown(owner, rental.id(), RentalTransferContextType.ARRIVAL);

        LocalDate editedDate = prefill.suggestedDate().plusDays(1);
        TransferBookingResponse created = transferBookingService.create(
                owner,
                transferRequest(
                        configuration,
                        TransferDirection.FROM_AIRPORT,
                        editedDate,
                        "Edited destination",
                        new RentalTransferSourceRequest(rental.id(), RentalTransferContextType.ARRIVAL)
                )
        );
        TransferBooking entity = transferBookingRepository.findById(created.id()).orElseThrow();
        RentalTransferContextResponse afterCreate = contextService.context(owner, rental.id());

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, context.options().size()),
                () -> Assertions.assertEquals(rental.checkInDate(), prefill.suggestedDate()),
                () -> Assertions.assertEquals("Barbaros Cd. 24", prefill.address()),
                () -> Assertions.assertEquals(editedDate, created.pickupDate()),
                () -> Assertions.assertEquals("Edited destination", created.address()),
                () -> Assertions.assertEquals(rental.id(), entity.getSourceRentalBookingId()),
                () -> Assertions.assertEquals(RentalTransferContextType.ARRIVAL, entity.getRentalContext()),
                () -> Assertions.assertEquals(1, afterCreate.options().size()),
                () -> Assertions.assertEquals(
                        RentalTransferContextType.CHECKOUT,
                        afterCreate.options().getFirst().context()
                ),
                () -> Assertions.assertEquals(2, jdbcTemplate.queryForObject(
                        "select count(*) from rental_transfer_action_event",
                        Integer.class
                )),
                () -> Assertions.assertThrows(
                        RentalBookingNotFoundException.class,
                        () -> contextService.context(stranger, rental.id())
                )
        );

        rentalBookingService.cancel(owner, rental.id());
        Assertions.assertEquals(
                TransferBookingStatus.REQUESTED,
                transferBookingRepository.findById(created.id()).orElseThrow().getStatus()
        );
    }

    @Test
    void farDatesShowAvailabilityAndManualMatchingTransferSuppressesOnlyActiveStates() {
        CurrentCustomer owner = RentalTestFixtures.customer(customerAccountService, "rental-transfer-match");
        RentalBookingResponse rental = rental(owner, "rental-transfer-match-property");
        TransferConfiguration configuration = transferConfiguration(TransferDirection.TO_AIRPORT);
        LocalDate farCheckIn = transferBookingPolicy.latestBookingDate().plusDays(1);
        jdbcTemplate.update(
                "update rental_booking set check_in_date = ?, check_out_date = ? where id = ?",
                farCheckIn,
                farCheckIn.plusDays(7),
                rental.id()
        );

        RentalTransferContextResponse farContext = contextService.context(owner, rental.id());
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, farContext.options().size()),
                () -> farContext.options().forEach(option -> Assertions.assertEquals(
                        RentalTransferContextAvailability.AVAILABLE_LATER,
                        option.availability()
                )),
                () -> Assertions.assertEquals(
                        transferBookingPolicy.bookingOpensOn(farCheckIn),
                        farContext.options().getFirst().availableFromDate()
                )
        );

        LocalDate checkout = transferBookingPolicy.earliestBookingDate().plusDays(8);
        jdbcTemplate.update(
                "update rental_booking set check_in_date = ?, check_out_date = ? where id = ?",
                checkout.minusDays(7),
                checkout,
                rental.id()
        );
        TransferBookingResponse manual = transferBookingService.create(
                owner,
                transferRequest(
                        configuration,
                        TransferDirection.TO_AIRPORT,
                        checkout,
                        "  BARBAROS   CD. 24 ",
                        null
                )
        );

        Assertions.assertEquals(
                1,
                contextService.context(owner, rental.id()).options().size()
        );
        transferBookingService.cancel(owner, manual.id());
        Assertions.assertEquals(
                2,
                contextService.context(owner, rental.id()).options().size()
        );

        jdbcTemplate.update("""
                update platform_service_state
                   set status = 'DISABLED', version = version + 1
                 where service = 'TRANSFER'
                """);
        clearPlatformServiceStateCache();
        RentalTransferContextResponse disabled = contextService.context(owner, rental.id());
        Assertions.assertAll(
                () -> Assertions.assertFalse(disabled.transferFlowAvailable()),
                () -> Assertions.assertTrue(disabled.options().isEmpty()),
                () -> Assertions.assertThrows(
                        PlatformServiceNotAvailableException.class,
                        () -> contextService.prefill(
                                owner,
                                rental.id(),
                                RentalTransferContextType.CHECKOUT
                        )
                )
        );
    }

    @Test
    void creationRejectsDirectionMismatchDuplicateAndMixedSourceTypes() {
        CurrentCustomer owner = RentalTestFixtures.customer(customerAccountService, "rental-transfer-validation");
        RentalBookingResponse rental = rental(owner, "rental-transfer-validation-property");
        TransferConfiguration configuration = transferConfiguration(TransferDirection.FROM_AIRPORT);
        RentalTransferSourceRequest source = new RentalTransferSourceRequest(
                rental.id(),
                RentalTransferContextType.ARRIVAL
        );
        CreateTransferBookingRequest valid = transferRequest(
                configuration,
                TransferDirection.FROM_AIRPORT,
                rental.checkInDate(),
                "Barbaros Cd. 24",
                source
        );
        transferBookingService.create(owner, valid);

        CreateTransferBookingRequest mixedSources = new CreateTransferBookingRequest(
                valid.direction(),
                valid.airportId(),
                valid.vehicleTypeId(),
                valid.pickupDate(),
                valid.pickupTime(),
                valid.address(),
                valid.passengerCount(),
                valid.luggageCount(),
                valid.flightNumber(),
                valid.scheduledArrivalTime(),
                valid.phone(),
                valid.comment(),
                999L,
                source
        );

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        RentalTransferAlreadyBookedException.class,
                        () -> transferBookingService.create(owner, valid)
                ),
                () -> Assertions.assertThrows(
                        RentalTransferContextNotEligibleException.class,
                        () -> transferBookingService.create(
                                owner,
                                transferRequest(
                                        configuration,
                                        TransferDirection.TO_AIRPORT,
                                        rental.checkInDate(),
                                        "Barbaros Cd. 24",
                                        source
                                )
                        )
                ),
                () -> Assertions.assertThrows(
                        com.cleany.transfer.InvalidTransferBookingException.class,
                        () -> transferBookingService.create(owner, mixedSources)
                )
        );
    }

    @Test
    void concurrentCreationAllowsOnlyOneActiveTransferPerRentalContext() throws Exception {
        CurrentCustomer owner = RentalTestFixtures.customer(customerAccountService, "rental-transfer-concurrent");
        RentalBookingResponse rental = rental(owner, "rental-transfer-concurrent-property");
        TransferConfiguration configuration = transferConfiguration(TransferDirection.FROM_AIRPORT);
        CreateTransferBookingRequest request = transferRequest(
                configuration,
                TransferDirection.FROM_AIRPORT,
                rental.checkInDate(),
                "Barbaros Cd. 24",
                new RentalTransferSourceRequest(rental.id(), RentalTransferContextType.ARRIVAL)
        );
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var first = executor.submit(() -> createConcurrently(owner, request, ready, start));
            var second = executor.submit(() -> createConcurrently(owner, request, ready, start));
            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            long successes = java.util.stream.Stream.of(first.get(), second.get())
                    .filter(Boolean::booleanValue)
                    .count();

            Assertions.assertAll(
                    () -> Assertions.assertEquals(1, successes),
                    () -> Assertions.assertEquals(1, transferBookingRepository.count())
            );
        }
    }

    private boolean createConcurrently(
            CurrentCustomer customer,
            CreateTransferBookingRequest request,
            CountDownLatch ready,
            CountDownLatch start
    ) throws InterruptedException {
        ready.countDown();
        start.await(5, TimeUnit.SECONDS);
        try {
            transferBookingService.create(customer, request);
            return true;
        } catch (RentalTransferAlreadyBookedException exception) {
            return false;
        }
    }

    private RentalBookingResponse rental(CurrentCustomer customer, String slug) {
        RentalPropertyResponse property = RentalTestFixtures.publishedProperty(
                rentalPropertyService,
                rentalPropertyMediaService,
                slug,
                new BigDecimal("100.00")
        );
        LocalDate checkIn = transferBookingPolicy.earliestBookingDate().plusDays(2);
        return rentalBookingService.create(customer, new CreateRentalBookingRequest(
                property.id(),
                RentalTermType.DATE_RANGE,
                checkIn,
                checkIn.plusDays(7),
                null,
                2,
                "+90 555 123 45 67",
                null
        ));
    }

    private TransferConfiguration transferConfiguration(TransferDirection direction) {
        TransferAirport airport = airportRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        TransferVehicleType vehicle = vehicleRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        priceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                direction,
                new BigDecimal("2500.00"),
                "TRY",
                true,
                transferBookingPolicy.pickupInstant(
                        transferBookingPolicy.earliestBookingDate(),
                        LocalTime.MIDNIGHT
                )
        ));
        return new TransferConfiguration(airport, vehicle);
    }

    private static CreateTransferBookingRequest transferRequest(
            TransferConfiguration configuration,
            TransferDirection direction,
            LocalDate pickupDate,
            String address,
            RentalTransferSourceRequest rentalSource
    ) {
        return new CreateTransferBookingRequest(
                direction,
                configuration.airport().getId(),
                configuration.vehicle().getId(),
                pickupDate,
                LocalTime.of(12, 0),
                address,
                1,
                0,
                direction == TransferDirection.FROM_AIRPORT ? "TK123" : null,
                direction == TransferDirection.FROM_AIRPORT ? LocalTime.of(11, 30) : null,
                "+905551234567",
                null,
                null,
                rentalSource
        );
    }

    private record TransferConfiguration(
            TransferAirport airport,
            TransferVehicleType vehicle
    ) {
    }
}
