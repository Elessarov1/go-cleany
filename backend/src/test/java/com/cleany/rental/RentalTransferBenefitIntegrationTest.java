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
import com.cleany.crossservice.rentaltransfer.RentalTransferBenefitRepository;
import com.cleany.crossservice.rentaltransfer.RentalTransferBenefitStatus;
import com.cleany.crossservice.rentaltransfer.RentalTransferBenefitUnavailableException;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextResponse;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;
import com.cleany.crossservice.rentaltransfer.RentalTransferSourceRequest;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProviderReferenceRepository;
import com.cleany.transfer.CreateTransferBookingRequest;
import com.cleany.transfer.InvalidTransferBookingException;
import com.cleany.transfer.TransferAirport;
import com.cleany.transfer.TransferAirportRepository;
import com.cleany.transfer.TransferBenefitType;
import com.cleany.transfer.TransferBookingPolicy;
import com.cleany.transfer.TransferBookingRepository;
import com.cleany.transfer.TransferBookingResponse;
import com.cleany.transfer.TransferBookingService;
import com.cleany.transfer.TransferDirection;
import com.cleany.transfer.TransferDriver;
import com.cleany.transfer.TransferDriverAssignmentService;
import com.cleany.transfer.TransferDriverRepository;
import com.cleany.transfer.TransferPrice;
import com.cleany.transfer.TransferPriceRepository;
import com.cleany.transfer.TransferQuoteRequest;
import com.cleany.transfer.TransferQuoteResponse;
import com.cleany.transfer.TransferVehicleType;
import com.cleany.transfer.TransferVehicleTypeRepository;

class RentalTransferBenefitIntegrationTest extends BaseIntegrationTest {

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
    private TransferDriverAssignmentService assignmentService;

    @Autowired
    private TransferAirportRepository airportRepository;

    @Autowired
    private TransferVehicleTypeRepository vehicleRepository;

    @Autowired
    private TransferPriceRepository priceRepository;

    @Autowired
    private TransferDriverRepository driverRepository;

    @Autowired
    private RentalTransferBenefitRepository benefitRepository;

    @Autowired
    private TransferBookingPolicy bookingPolicy;

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
        jdbcTemplate.update("delete from rental_transfer_action_event");
        benefitRepository.deleteAll();
        transferBookingRepository.deleteAll();
        driverRepository.deleteAll();
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
    void benefitIsVisibleAndBackendQuotePersistsTenPercentForAnyCurrency() {
        CurrentCustomer owner = RentalTestFixtures.customer(customerAccountService, "benefit-currency");
        RentalBookingResponse rental = rental(owner, "benefit-currency-property");
        TransferConfiguration configuration = transferConfiguration(
                TransferDirection.FROM_AIRPORT,
                new BigDecimal("123.45"),
                "USD"
        );
        RentalTransferSourceRequest source = new RentalTransferSourceRequest(
                rental.id(),
                RentalTransferContextType.ARRIVAL
        );

        RentalTransferContextResponse context = contextService.context(owner, rental.id());
        TransferQuoteResponse quote = transferBookingService.quote(owner, new TransferQuoteRequest(
                TransferDirection.FROM_AIRPORT,
                configuration.airport().getId(),
                configuration.vehicle().getId(),
                source,
                TransferBenefitType.RENTAL_FIRST_TRANSFER
        ));
        TransferBookingResponse created = transferBookingService.create(
                owner,
                request(configuration, TransferDirection.FROM_AIRPORT, rental.checkInDate(), source, true)
        );

        Assertions.assertAll(
                () -> Assertions.assertTrue(context.options().stream().allMatch(option -> option.benefit() != null)),
                () -> Assertions.assertEquals(new BigDecimal("123.45"), quote.baseAmount()),
                () -> Assertions.assertEquals(new BigDecimal("12.35"), quote.discountAmount()),
                () -> Assertions.assertEquals(new BigDecimal("111.10"), quote.payableAmount()),
                () -> Assertions.assertEquals("USD", quote.currency()),
                () -> Assertions.assertEquals(new BigDecimal("123.45"), created.basePriceAmount()),
                () -> Assertions.assertEquals(new BigDecimal("12.35"), created.discountAmount()),
                () -> Assertions.assertEquals(new BigDecimal("111.10"), created.priceAmount()),
                () -> Assertions.assertEquals(TransferBenefitType.RENTAL_FIRST_TRANSFER, created.appliedBenefit()),
                () -> Assertions.assertEquals(
                        RentalTransferBenefitStatus.RESERVED,
                        benefitRepository.findById(rental.id()).orElseThrow().getStatus()
                ),
                () -> Assertions.assertNull(
                        contextService.context(owner, rental.id()).options().getFirst().benefit()
                )
        );
    }

    @Test
    void cancellationBeforeConfirmationReleasesButConfirmationConsumesPermanently() {
        CurrentCustomer owner = RentalTestFixtures.customer(customerAccountService, "benefit-lifecycle");
        RentalBookingResponse rental = rental(owner, "benefit-lifecycle-property");
        TransferConfiguration arrival = transferConfiguration(
                TransferDirection.FROM_AIRPORT,
                new BigDecimal("2000.00"),
                "TRY"
        );
        TransferConfiguration checkout = transferConfiguration(
                TransferDirection.TO_AIRPORT,
                new BigDecimal("1800.00"),
                "TRY"
        );
        TransferBookingResponse first = transferBookingService.create(
                owner,
                request(
                        arrival,
                        TransferDirection.FROM_AIRPORT,
                        rental.checkInDate(),
                        new RentalTransferSourceRequest(rental.id(), RentalTransferContextType.ARRIVAL),
                        true
                )
        );

        transferBookingService.cancel(owner, first.id());
        Assertions.assertEquals(
                RentalTransferBenefitStatus.RELEASED,
                benefitRepository.findById(rental.id()).orElseThrow().getStatus()
        );

        TransferBookingResponse second = transferBookingService.create(
                owner,
                request(
                        checkout,
                        TransferDirection.TO_AIRPORT,
                        rental.checkOutDate(),
                        new RentalTransferSourceRequest(rental.id(), RentalTransferContextType.CHECKOUT),
                        true
                )
        );
        TransferDriver driver = driverRepository.saveAndFlush(new TransferDriver(
                "Benefit driver",
                "+905551110099",
                true,
                null,
                bookingPolicy.pickupInstant(bookingPolicy.earliestBookingDate(), LocalTime.MIDNIGHT)
        ));
        assignmentService.assignByAdmin(second.id(), driver.getId());
        transferBookingService.cancel(owner, second.id());

        Assertions.assertAll(
                () -> Assertions.assertEquals(new BigDecimal("180.00"), second.discountAmount()),
                () -> Assertions.assertEquals(
                        RentalTransferBenefitStatus.CONSUMED,
                        benefitRepository.findById(rental.id()).orElseThrow().getStatus()
                ),
                () -> Assertions.assertThrows(
                        RentalTransferBenefitUnavailableException.class,
                        () -> transferBookingService.quote(owner, new TransferQuoteRequest(
                                TransferDirection.FROM_AIRPORT,
                                arrival.airport().getId(),
                                arrival.vehicle().getId(),
                                new RentalTransferSourceRequest(
                                        rental.id(),
                                        RentalTransferContextType.ARRIVAL
                                ),
                                TransferBenefitType.RENTAL_FIRST_TRANSFER
                        ))
                )
        );
    }

    @Test
    void benefitRequiresRentalSourceAndExistingFirstTripClosesIt() {
        CurrentCustomer owner = RentalTestFixtures.customer(customerAccountService, "benefit-validation");
        RentalBookingResponse rental = rental(owner, "benefit-validation-property");
        TransferConfiguration arrival = transferConfiguration(
                TransferDirection.FROM_AIRPORT,
                new BigDecimal("900.00"),
                "EUR"
        );
        TransferConfiguration checkout = transferConfiguration(
                TransferDirection.TO_AIRPORT,
                new BigDecimal("900.00"),
                "EUR"
        );
        transferBookingService.create(
                owner,
                request(
                        arrival,
                        TransferDirection.FROM_AIRPORT,
                        rental.checkInDate(),
                        new RentalTransferSourceRequest(rental.id(), RentalTransferContextType.ARRIVAL),
                        false
                )
        );

        RentalTransferContextResponse remaining = contextService.context(owner, rental.id());
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, remaining.options().size()),
                () -> Assertions.assertNull(remaining.options().getFirst().benefit()),
                () -> Assertions.assertThrows(
                        RentalTransferBenefitUnavailableException.class,
                        () -> transferBookingService.quote(owner, new TransferQuoteRequest(
                                TransferDirection.TO_AIRPORT,
                                checkout.airport().getId(),
                                checkout.vehicle().getId(),
                                new RentalTransferSourceRequest(
                                        rental.id(),
                                        RentalTransferContextType.CHECKOUT
                                ),
                                TransferBenefitType.RENTAL_FIRST_TRANSFER
                        ))
                ),
                () -> Assertions.assertThrows(
                        InvalidTransferBookingException.class,
                        () -> transferBookingService.quote(owner, new TransferQuoteRequest(
                                TransferDirection.TO_AIRPORT,
                                checkout.airport().getId(),
                                checkout.vehicle().getId(),
                                null,
                                TransferBenefitType.RENTAL_FIRST_TRANSFER
                        ))
                )
        );
    }

    @Test
    void arrivalAndCheckoutCannotReserveTheSameBenefitConcurrently() throws Exception {
        CurrentCustomer owner = RentalTestFixtures.customer(customerAccountService, "benefit-race");
        RentalBookingResponse rental = rental(owner, "benefit-race-property");
        TransferConfiguration arrival = transferConfiguration(
                TransferDirection.FROM_AIRPORT,
                new BigDecimal("2000.00"),
                "TRY"
        );
        TransferConfiguration checkout = transferConfiguration(
                TransferDirection.TO_AIRPORT,
                new BigDecimal("1800.00"),
                "TRY"
        );
        CreateTransferBookingRequest arrivalRequest = request(
                arrival,
                TransferDirection.FROM_AIRPORT,
                rental.checkInDate(),
                new RentalTransferSourceRequest(rental.id(), RentalTransferContextType.ARRIVAL),
                true
        );
        CreateTransferBookingRequest checkoutRequest = request(
                checkout,
                TransferDirection.TO_AIRPORT,
                rental.checkOutDate(),
                new RentalTransferSourceRequest(rental.id(), RentalTransferContextType.CHECKOUT),
                true
        );
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var first = executor.submit(() -> createConcurrently(owner, arrivalRequest, ready, start));
            var second = executor.submit(() -> createConcurrently(owner, checkoutRequest, ready, start));
            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            long successes = java.util.stream.Stream.of(first.get(), second.get())
                    .filter(Boolean::booleanValue)
                    .count();

            Assertions.assertAll(
                    () -> Assertions.assertEquals(1, successes),
                    () -> Assertions.assertEquals(1, transferBookingRepository.count()),
                    () -> Assertions.assertEquals(
                            RentalTransferBenefitStatus.RESERVED,
                            benefitRepository.findById(rental.id()).orElseThrow().getStatus()
                    )
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
        } catch (RentalTransferBenefitUnavailableException exception) {
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
        LocalDate checkIn = bookingPolicy.earliestBookingDate().plusDays(2);
        return rentalBookingService.create(customer, new CreateRentalBookingRequest(
                property.id(),
                RentalTermType.DATE_RANGE,
                checkIn,
                checkIn.plusDays(7),
                null,
                2,
                "+905551234567",
                null
        ));
    }

    private TransferConfiguration transferConfiguration(
            TransferDirection direction,
            BigDecimal amount,
            String currency
    ) {
        TransferAirport airport = airportRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        TransferVehicleType vehicle = vehicleRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        priceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                direction,
                amount,
                currency,
                true,
                bookingPolicy.pickupInstant(bookingPolicy.earliestBookingDate(), LocalTime.MIDNIGHT)
        ));
        return new TransferConfiguration(airport, vehicle);
    }

    private static CreateTransferBookingRequest request(
            TransferConfiguration configuration,
            TransferDirection direction,
            LocalDate date,
            RentalTransferSourceRequest source,
            boolean applyBenefit
    ) {
        return new CreateTransferBookingRequest(
                direction,
                configuration.airport().getId(),
                configuration.vehicle().getId(),
                date,
                LocalTime.of(12, 0),
                "Barbaros Cd. 24",
                1,
                0,
                direction == TransferDirection.FROM_AIRPORT ? "TK123" : null,
                direction == TransferDirection.FROM_AIRPORT ? LocalTime.of(11, 30) : null,
                "+905551234567",
                null,
                null,
                source,
                applyBenefit ? TransferBenefitType.RENTAL_FIRST_TRANSFER : null
        );
    }

    private record TransferConfiguration(
            TransferAirport airport,
            TransferVehicleType vehicle
    ) {
    }
}
