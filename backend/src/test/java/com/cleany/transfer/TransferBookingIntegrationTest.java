package com.cleany.transfer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.mockito.Mockito;

import com.cleany.admin.AdminAccessService;
import com.cleany.base.BaseIntegrationTest;
import com.cleany.catalog.PlatformServiceNotAvailableException;
import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.repeat.RepeatSourceNotEligibleException;

class TransferBookingIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransferBookingService bookingService;

    @Autowired
    private TransferBookingPolicy bookingPolicy;

    @Autowired
    private TransferAirportRepository airportRepository;

    @Autowired
    private TransferVehicleTypeRepository vehicleRepository;

    @Autowired
    private TransferPriceRepository priceRepository;

    @Autowired
    private TransferBookingRepository bookingRepository;

    @Autowired
    private TransferDriverRepository driverRepository;

    @Autowired
    private AdminTransferService adminTransferService;

    @Autowired
    private TransferDriverLinkService driverLinkService;

    @Autowired
    private CustomerAccountService customerAccountService;

    @Autowired
    private CustomerExternalIdentityRepository identityRepository;

    @Autowired
    private CustomerAccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Clock clock;

    @MockitoBean
    private AdminAccessService adminAccessService;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        bookingRepository.deleteAll();
        driverRepository.deleteAll();
        priceRepository.deleteAll();
        jdbcTemplate.update("delete from customer_acquisition");
        jdbcTemplate.update("delete from customer_role");
        identityRepository.deleteAll();
        accountRepository.deleteAll();
        jdbcTemplate.update("""
                update platform_service_state
                   set status = 'ENABLED',
                       updated_by_customer_id = null,
                       version = version + 1
                 where service = 'TRANSFER'
                """);
        jdbcTemplate.update("""
                update transfer_airport
                   set enabled = true,
                       name_ru = case code
                           when 'GZP' then 'Аэропорт Газипаша'
                           else 'Аэропорт Анталья'
                       end,
                       name_en = case code
                           when 'GZP' then 'Alanya Gazipaşa Airport'
                           else 'Antalya Airport'
                       end,
                       version = version + 1
                """);
        jdbcTemplate.update("""
                update transfer_vehicle_type
                   set enabled = true,
                       max_passengers = case code when 'SEDAN' then 3 else 6 end,
                       max_luggage = case code when 'SEDAN' then 3 else 6 end,
                       version = version + 1
                """);
        clearPlatformServiceStateCache();
        Mockito.when(adminAccessService.requireCurrentAdmin()).thenReturn(900001L);
    }

    @Test
    void activeConfigurationCreatesRequestedBookingWithStableSnapshotsAndOwnership() {
        CurrentCustomer owner = customer("transfer-owner");
        CurrentCustomer stranger = customer("transfer-stranger");
        TransferAirport airport = airport("AYT");
        TransferVehicleType vehicle = vehicle("MINIVAN");
        TransferPrice price = priceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                TransferDirection.TO_AIRPORT,
                new BigDecimal("3200.00"),
                "TRY",
                true,
                clock.instant()
        ));

        TransferBookingResponse created = bookingService.create(
                owner,
                request(airport, vehicle, TransferDirection.TO_AIRPORT, null, null)
        );
        price.update(new BigDecimal("4100.00"), "TRY", true, clock.instant());
        airport.update("Новое имя", "New name", true, 20, clock.instant());
        priceRepository.saveAndFlush(price);
        airportRepository.saveAndFlush(airport);
        TransferBookingResponse historical = bookingService.currentCustomerBooking(owner, created.id());

        Assertions.assertAll(
                () -> Assertions.assertEquals(TransferBookingStatus.REQUESTED, created.status()),
                () -> Assertions.assertEquals(new BigDecimal("3200.00"), historical.priceAmount()),
                () -> Assertions.assertEquals("Аэропорт Анталья", historical.airportNameRu()),
                () -> Assertions.assertEquals("+905551112233", historical.phone()),
                () -> Assertions.assertThrows(
                        TransferBookingNotFoundException.class,
                        () -> bookingService.currentCustomerBooking(stranger, created.id())
                )
        );
    }

    @Test
    void disabledAirportVehicleOrPriceCannotBeBooked() {
        CurrentCustomer customer = customer("transfer-disabled");
        TransferAirport airport = airport("GZP");
        TransferVehicleType vehicle = vehicle("SEDAN");
        TransferPrice price = priceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                TransferDirection.TO_AIRPORT,
                new BigDecimal("1800.00"),
                "TRY",
                true,
                clock.instant()
        ));

        jdbcTemplate.update("update transfer_airport set enabled = false where id = ?", airport.getId());
        Assertions.assertThrows(
                TransferConfigurationUnavailableException.class,
                () -> bookingService.create(
                        customer,
                        request(airport, vehicle, TransferDirection.TO_AIRPORT, null, null)
                )
        );

        jdbcTemplate.update("update transfer_airport set enabled = true where id = ?", airport.getId());
        jdbcTemplate.update("update transfer_vehicle_type set enabled = false where id = ?", vehicle.getId());
        Assertions.assertThrows(
                TransferConfigurationUnavailableException.class,
                () -> bookingService.create(
                        customer,
                        request(airport, vehicle, TransferDirection.TO_AIRPORT, null, null)
                )
        );

        jdbcTemplate.update("update transfer_vehicle_type set enabled = true where id = ?", vehicle.getId());
        jdbcTemplate.update("update transfer_price set enabled = false where id = ?", price.getId());
        Assertions.assertThrows(
                TransferConfigurationUnavailableException.class,
                () -> bookingService.create(
                        customer,
                        request(airport, vehicle, TransferDirection.TO_AIRPORT, null, null)
                )
        );
    }

    @Test
    void dateAndFlightRulesAreAppliedByApplicationService() {
        CurrentCustomer customer = customer("transfer-rules");
        TransferAirport airport = airport("AYT");
        TransferVehicleType vehicle = vehicle("MINIVAN");
        priceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                TransferDirection.FROM_AIRPORT,
                new BigDecimal("3500.00"),
                "TRY",
                true,
                clock.instant()
        ));

        CreateTransferBookingRequest missingFlight = request(
                airport,
                vehicle,
                TransferDirection.FROM_AIRPORT,
                null,
                null
        );
        CreateTransferBookingRequest today = new CreateTransferBookingRequest(
                TransferDirection.FROM_AIRPORT,
                airport.getId(),
                vehicle.getId(),
                bookingPolicy.earliestBookingDate().minusDays(1),
                LocalTime.of(3, 30),
                "Kestel, Alanya",
                2,
                2,
                "TK123",
                LocalTime.of(3, 0),
                "+905551112233",
                null,
                null,
                null
        );

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        InvalidTransferBookingException.class,
                        () -> bookingService.create(customer, missingFlight)
                ),
                () -> Assertions.assertThrows(
                        InvalidTransferBookingException.class,
                        () -> bookingService.create(customer, today)
                ),
                () -> Assertions.assertDoesNotThrow(
                        () -> bookingService.create(
                                customer,
                                request(
                                        airport,
                                        vehicle,
                                        TransferDirection.FROM_AIRPORT,
                                        "TK123",
                                        LocalTime.of(3, 0)
                                )
                        )
                )
        );
    }

    @Test
    void viewingRequestedBookingDoesNotConfirmItAndAdminAssignmentPublishesConfirmedStatus() {
        CurrentCustomer customer = customer("transfer-admin-assignment");
        TransferAirport airport = airport("GZP");
        TransferVehicleType vehicle = vehicle("SEDAN");
        priceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                TransferDirection.TO_AIRPORT,
                new BigDecimal("1800.00"),
                "TRY",
                true,
                clock.instant()
        ));
        TransferBookingResponse booking = bookingService.create(
                customer,
                request(airport, vehicle, TransferDirection.TO_AIRPORT, null, null)
        );
        TransferDriver disabled = driverRepository.saveAndFlush(new TransferDriver(
                "Disabled Driver", "+905551112234", false, null, clock.instant()
        ));
        TransferDriver manualDriver = driverRepository.saveAndFlush(new TransferDriver(
                "Manual Driver", "+905551112235", true, null, clock.instant()
        ));

        TransferBookingResponse viewed = adminTransferService.booking(booking.id());
        Assertions.assertAll(
                () -> Assertions.assertEquals(TransferBookingStatus.REQUESTED, viewed.status()),
                () -> Assertions.assertEquals(0, jdbcTemplate.queryForObject("""
                        select count(*)
                          from customer_notification
                         where customer_id = ?
                           and type = 'TRANSFER_CONFIRMED'
                        """, Integer.class, customer.customerId()))
        );

        Assertions.assertThrows(
                TransferConfigurationUnavailableException.class,
                () -> adminTransferService.assign(booking.id(), disabled.getId())
        );
        TransferBookingResponse confirmed = adminTransferService.assign(
                booking.id(),
                manualDriver.getId()
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(TransferBookingStatus.CONFIRMED, confirmed.status()),
                () -> Assertions.assertEquals(manualDriver.getId(), confirmed.driverId()),
                () -> Assertions.assertEquals("Manual Driver", confirmed.driverName()),
                () -> Assertions.assertEquals(1, jdbcTemplate.queryForObject("""
                        select count(*)
                          from customer_notification
                         where customer_id = ?
                           and type = 'TRANSFER_CONFIRMED'
                        """, Integer.class, customer.customerId())),
                () -> Assertions.assertThrows(
                        TransferAssignmentConflictException.class,
                        () -> adminTransferService.assign(booking.id(), manualDriver.getId())
                ),
                () -> Assertions.assertThrows(
                        TransferBookingStateException.class,
                        () -> adminTransferService.complete(booking.id())
                )
        );
    }

    @Test
    void oneTimeLinkVerifiesActualConfiguredTelegramIdentity() {
        TransferDriver driver = driverRepository.saveAndFlush(new TransferDriver(
                "Telegram Driver", "+905551112236", true, 77112233L, clock.instant()
        ));

        TransferDriverLinkResponse link = driverLinkService.createLink(driver.getId());
        String marker = "start=driver_";
        String rawToken = link.url().substring(link.url().indexOf(marker) + marker.length());

        Assertions.assertThrows(
                InvalidTransferConfigurationException.class,
                () -> driverLinkService.authorize(rawToken, 99887766L, 99887766L)
        );
        TransferDriver connected = driverLinkService.authorize(rawToken, 77112233L, 77112233L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(DriverTelegramStatus.CONNECTED, connected.telegramStatus()),
                () -> Assertions.assertEquals(77112233L, connected.getVerifiedTelegramUserId()),
                () -> Assertions.assertTrue(connected.isTelegramNotificationsEnabled()),
                () -> Assertions.assertThrows(
                        TransferDriverLinkException.class,
                        () -> driverLinkService.authorize(rawToken, 77112233L, 77112233L)
                )
        );
    }

    @Test
    void completedOwnedBooking_prefillsReusableFieldsAndCreatesFreshLinkedBooking() {
        CurrentCustomer owner = customer("transfer-repeat-owner");
        TransferAirport airport = airport("AYT");
        TransferVehicleType vehicle = vehicle("MINIVAN");
        TransferPrice price = priceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                TransferDirection.TO_AIRPORT,
                new BigDecimal("3200.00"),
                "TRY",
                true,
                clock.instant()
        ));
        TransferBookingResponse source = bookingService.create(
                owner,
                request(airport, vehicle, TransferDirection.TO_AIRPORT, null, null)
        );
        markCompleted(source);

        bookingService.recordRepeatShown(owner, source.id());
        bookingService.recordRepeatShown(owner, source.id());
        TransferRepeatPrefillResponse prefill = bookingService.repeatPrefill(owner, source.id());
        bookingService.repeatPrefill(owner, source.id());
        price.update(new BigDecimal("4100.00"), "TRY", true, clock.instant());
        priceRepository.saveAndFlush(price);
        TransferBookingResponse repeated = bookingService.create(owner, new CreateTransferBookingRequest(
                prefill.direction(),
                prefill.airportId(),
                prefill.vehicleTypeId(),
                bookingPolicy.earliestBookingDate().plusDays(1),
                LocalTime.of(11, 0),
                prefill.address(),
                prefill.passengerCount(),
                prefill.luggageCount(),
                null,
                null,
                "+905559990000",
                null,
                source.id(),
                null
        ));
        jdbcTemplate.update("update transfer_price set enabled = false where id = ?", price.getId());
        TransferRepeatPrefillResponse unavailablePair = bookingService.repeatPrefill(owner, source.id());
        TransferBooking repeatedEntity = bookingRepository.findById(repeated.id()).orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertEquals(source.id(), prefill.sourceBookingId()),
                () -> Assertions.assertEquals(TransferDirection.TO_AIRPORT, prefill.direction()),
                () -> Assertions.assertEquals(airport.getId(), prefill.airportId()),
                () -> Assertions.assertEquals(vehicle.getId(), prefill.vehicleTypeId()),
                () -> Assertions.assertEquals("Kestel, Alanya", prefill.address()),
                () -> Assertions.assertEquals(source.id(), repeatedEntity.getRepeatSourceBookingId()),
                () -> Assertions.assertEquals(new BigDecimal("4100.00"), repeated.priceAmount()),
                () -> Assertions.assertEquals("+905559990000", repeated.phone()),
                () -> Assertions.assertNull(repeated.flightNumber()),
                () -> Assertions.assertNull(repeated.scheduledArrivalTime()),
                () -> Assertions.assertNull(repeated.comment()),
                () -> Assertions.assertNotEquals(source.pickupDate(), repeated.pickupDate()),
                () -> Assertions.assertNotEquals(source.pickupTime(), repeated.pickupTime()),
                () -> Assertions.assertNull(unavailablePair.airportId()),
                () -> Assertions.assertNull(unavailablePair.vehicleTypeId()),
                () -> Assertions.assertEquals(2L, jdbcTemplate.queryForObject(
                        "select count(*) from repeat_action_event where customer_id = ? and service = 'TRANSFER'",
                        Long.class,
                        owner.customerId()
                ))
        );
    }

    @Test
    void transferRepeatRejectsForeignNonCompletedAndDisabledSources() {
        CurrentCustomer owner = customer("transfer-repeat-validation-owner");
        CurrentCustomer stranger = customer("transfer-repeat-validation-stranger");
        TransferAirport airport = airport("GZP");
        TransferVehicleType vehicle = vehicle("SEDAN");
        priceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                TransferDirection.TO_AIRPORT,
                new BigDecimal("1800.00"),
                "TRY",
                true,
                clock.instant()
        ));
        TransferBookingResponse source = bookingService.create(
                owner,
                request(airport, vehicle, TransferDirection.TO_AIRPORT, null, null)
        );

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        RepeatSourceNotEligibleException.class,
                        () -> bookingService.repeatPrefill(owner, source.id())
                ),
                () -> Assertions.assertThrows(
                        TransferBookingNotFoundException.class,
                        () -> bookingService.repeatPrefill(stranger, source.id())
                )
        );

        markCompleted(source);
        jdbcTemplate.update("""
                update platform_service_state
                   set status = 'DISABLED', version = version + 1
                 where service = 'TRANSFER'
                """);
        clearPlatformServiceStateCache();

        Assertions.assertThrows(
                PlatformServiceNotAvailableException.class,
                () -> bookingService.repeatPrefill(owner, source.id())
        );
    }

    private CreateTransferBookingRequest request(
            TransferAirport airport,
            TransferVehicleType vehicle,
            TransferDirection direction,
            String flightNumber,
            LocalTime scheduledArrivalTime
    ) {
        return new CreateTransferBookingRequest(
                direction,
                airport.getId(),
                vehicle.getId(),
                bookingPolicy.earliestBookingDate(),
                LocalTime.of(3, 30),
                "Kestel, Alanya",
                2,
                2,
                flightNumber,
                scheduledArrivalTime,
                "+90 555 111 22 33",
                null,
                null,
                null
        );
    }

    private void markCompleted(TransferBookingResponse booking) {
        TransferDriver driver = driverRepository.saveAndFlush(new TransferDriver(
                "Repeat test driver",
                "+905551118888",
                true,
                null,
                clock.instant().minusSeconds(120)
        ));
        jdbcTemplate.update(
                """
                update transfer_booking
                   set status = 'COMPLETED', driver_id = ?, confirmed_at = ?, completed_at = ?
                 where id = ?
                """,
                driver.getId(),
                Timestamp.from(clock.instant().minusSeconds(60)),
                Timestamp.from(clock.instant()),
                booking.id()
        );
    }

    private CurrentCustomer customer(String subject) {
        return customerAccountService.resolveCustomer(new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.TELEGRAM,
                subject,
                subject,
                "Customer " + subject,
                "ru"
        ));
    }

    private TransferAirport airport(String code) {
        return airportRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .filter(candidate -> candidate.getCode().equals(code))
                .findFirst()
                .orElseThrow();
    }

    private TransferVehicleType vehicle(String code) {
        return vehicleRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .filter(candidate -> candidate.getCode().equals(code))
                .findFirst()
                .orElseThrow();
    }
}
