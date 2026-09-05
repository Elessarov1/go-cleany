package com.cleany.customer.home;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.EnumSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;

import com.cleany.authorization.PlatformRole;
import com.cleany.authorization.PlatformRoleService;
import com.cleany.base.BaseIntegrationTest;
import com.cleany.catalog.PlatformService;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitStatus;
import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
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

@AutoConfigureMockMvc
class CustomerHomeIntegrationTest extends BaseIntegrationTest {

    private static final String OWNER_SUBJECT = "home-owner";
    private static final String OWNER_EMAIL = "home-owner@example.test";
    private static final ZoneId ISTANBUL = ZoneId.of("Europe/Istanbul");

    @Autowired private CustomerHomeService homeService;
    @Autowired private CustomerAccountService accountService;
    @Autowired private PlatformRoleService roleService;
    @Autowired private CleaningOrderService cleaningOrderService;
    @Autowired private RentalBookingService rentalBookingService;
    @Autowired private RentalPropertyService rentalPropertyService;
    @Autowired private RentalStayPolicy rentalStayPolicy;
    @Autowired private TransferBookingService transferBookingService;
    @Autowired private TransferBookingPolicy transferBookingPolicy;
    @Autowired private TransferAirportRepository transferAirportRepository;
    @Autowired private TransferVehicleTypeRepository transferVehicleRepository;
    @Autowired private TransferPriceRepository transferPriceRepository;
    @Autowired private TransferDriverRepository transferDriverRepository;
    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private Clock clock;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from rental_transfer_action_event");
        jdbcTemplate.update("delete from repeat_action_event");
        jdbcTemplate.update("delete from customer_notification");
        jdbcTemplate.update("delete from transfer_booking");
        jdbcTemplate.update("delete from transfer_driver");
        jdbcTemplate.update("delete from transfer_price");
        jdbcTemplate.update("delete from rental_cleaning_benefit");
        jdbcTemplate.update("delete from rental_occupancy");
        jdbcTemplate.update("delete from rental_booking");
        jdbcTemplate.update("delete from rental_property_amenity");
        jdbcTemplate.update("delete from rental_property");
        jdbcTemplate.update("delete from cleaning_order");
        jdbcTemplate.update("delete from customer_acquisition");
        jdbcTemplate.update("delete from customer_role");
        jdbcTemplate.update("delete from customer_external_identity");
        jdbcTemplate.update("delete from customer_account");
        jdbcTemplate.update("""
                update platform_service_state
                   set status = 'ENABLED', updated_by_customer_id = null, version = version + 1
                """);
        clearPlatformServiceStateCache();
    }

    @Test
    void emptyCustomerGetsEmptyHomeAndAnonymousRequestIsRejected() throws Exception {
        CurrentCustomer owner = customer(OWNER_SUBJECT, OWNER_EMAIL);

        CustomerHomeResponse result = homeService.home(owner);

        Assertions.assertAll(
                () -> Assertions.assertFalse(result.hasActivity()),
                () -> Assertions.assertNull(result.activeTransaction()),
                () -> Assertions.assertEquals(0, result.activeTransactionCount()),
                () -> Assertions.assertNull(result.primaryAction()),
                () -> Assertions.assertNull(result.repeatOpportunity())
        );
        mvc.perform(get("/api/v1/account/home"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/account/home")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasActivity").value(false))
                .andExpect(jsonPath("$.activeTransaction").doesNotExist())
                .andExpect(jsonPath("$.activeTransactionCount").value(0));
    }

    @Test
    void nearestOwnedActiveTransactionRemainsVisibleWhenServicesAreUnavailable() {
        CurrentCustomer owner = customer(OWNER_SUBJECT, OWNER_EMAIL);
        CurrentCustomer stranger = customer("home-stranger", "home-stranger@example.test");
        LocalDate today = LocalDate.now(clock.withZone(ISTANBUL));
        TransferBookingResponse transfer = transfer(
                owner,
                TransferDirection.TO_AIRPORT,
                transferBookingPolicy.earliestBookingDate(),
                "Active transfer"
        );
        rental(owner, today.plusDays(4), 7, "Active rental");
        cleaning(owner, today.plusDays(6), "Active cleaning");
        cleaning(stranger, today.plusDays(1), "Private cleaning");
        setServiceStatus("CLEANING", "DISABLED");
        setServiceStatus("RENTAL", "IN_TEST");
        setServiceStatus("TRANSFER", "DISABLED");

        CustomerHomeResponse result = homeService.home(owner);

        Assertions.assertAll(
                () -> Assertions.assertTrue(result.hasActivity()),
                () -> Assertions.assertEquals(3, result.activeTransactionCount()),
                () -> Assertions.assertNotNull(result.activeTransaction()),
                () -> Assertions.assertEquals(PlatformService.TRANSFER, result.activeTransaction().service()),
                () -> Assertions.assertEquals(transfer.id(), result.activeTransaction().entityId()),
                () -> Assertions.assertNull(result.primaryAction()),
                () -> Assertions.assertNull(result.repeatOpportunity())
        );
    }

    @Test
    void primaryActionUsesNearestBookableContextAndIgnoresUnavailableLaterAndClosedBenefit() {
        CurrentCustomer owner = customer(OWNER_SUBJECT, OWNER_EMAIL);
        LocalDate today = LocalDate.now(clock.withZone(ISTANBUL));
        RentalBookingResponse near = rental(owner, today.plusDays(5), 7, "Near rental");
        addCleaningBenefit(owner, near, RentalCleaningBenefitStatus.AVAILABLE, "RC23456789");
        RentalBookingResponse far = rental(owner, today.plusDays(10), 7, "Far rental");
        LocalDate farCheckIn = transferBookingPolicy.latestBookingDate().plusDays(20);
        jdbcTemplate.update(
                "update rental_booking set check_in_date = ?, check_out_date = ? where id = ?",
                farCheckIn,
                farCheckIn.plusDays(7),
                far.id()
        );

        setServiceStatus("TRANSFER", "DISABLED");
        CustomerHomeResponse cleaningFirst = homeService.home(owner);
        jdbcTemplate.update(
                "update rental_cleaning_benefit set status = 'REDEEMED', redeemed_at = ? where rental_booking_id = ?",
                Timestamp.from(clock.instant()),
                near.id()
        );
        setServiceStatus("TRANSFER", "ENABLED");
        CustomerHomeResponse transferAfterBenefit = homeService.home(owner);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        CustomerHomePrimaryActionType.RENTAL_CLEANING,
                        cleaningFirst.primaryAction().type()
                ),
                () -> Assertions.assertEquals(today.plusDays(9), cleaningFirst.primaryAction().relevantDate()),
                () -> Assertions.assertEquals(today.plusDays(9), cleaningFirst.primaryAction().eligibleFrom()),
                () -> Assertions.assertEquals(today.plusDays(12), cleaningFirst.primaryAction().expiresOn()),
                () -> Assertions.assertEquals(
                        "/cleaning?rentalBooking=" + near.id() + "&promo=RC23456789",
                        cleaningFirst.primaryAction().targetPath()
                ),
                () -> Assertions.assertEquals(
                        CustomerHomePrimaryActionType.RENTAL_TRANSFER_ARRIVAL,
                        transferAfterBenefit.primaryAction().type()
                ),
                () -> Assertions.assertEquals(near.id(), transferAfterBenefit.primaryAction().sourceEntityId()),
                () -> Assertions.assertEquals(today.plusDays(5), transferAfterBenefit.primaryAction().relevantDate())
        );
    }

    @Test
    void transferContextRespectsEnabledInTestDisabledAndSuppressesMatchingRide() {
        CurrentCustomer owner = customer(OWNER_SUBJECT, OWNER_EMAIL);
        LocalDate today = LocalDate.now(clock.withZone(ISTANBUL));
        RentalBookingResponse rental = rental(owner, today.plusDays(5), 7, "Matching rental");

        CustomerHomeResponse enabled = homeService.home(owner);
        setServiceStatus("TRANSFER", "IN_TEST");
        CustomerHomeResponse inTestCustomer = homeService.home(owner);
        roleService.ensureRole(owner.customerId(), PlatformRole.ADMIN);
        CustomerHomeResponse inTestAdmin = homeService.home(owner);
        setServiceStatus("TRANSFER", "DISABLED");
        CustomerHomeResponse disabledAdmin = homeService.home(owner);
        setServiceStatus("TRANSFER", "ENABLED");
        transfer(
                owner,
                TransferDirection.FROM_AIRPORT,
                rental.checkInDate(),
                "Matching rental"
        );
        CustomerHomeResponse matchingTransfer = homeService.home(owner);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        CustomerHomePrimaryActionType.RENTAL_TRANSFER_ARRIVAL,
                        enabled.primaryAction().type()
                ),
                () -> Assertions.assertNull(inTestCustomer.primaryAction()),
                () -> Assertions.assertEquals(
                        CustomerHomePrimaryActionType.RENTAL_TRANSFER_ARRIVAL,
                        inTestAdmin.primaryAction().type()
                ),
                () -> Assertions.assertNull(disabledAdmin.primaryAction()),
                () -> Assertions.assertEquals(
                        CustomerHomePrimaryActionType.RENTAL_TRANSFER_CHECKOUT,
                        matchingTransfer.primaryAction().type()
                )
        );
    }

    @Test
    void repeatUsesLatestEligibleServiceWithoutDuplicatingActiveOrPrimaryTargetAndGetIsReadOnly() {
        CurrentCustomer owner = customer(OWNER_SUBJECT, OWNER_EMAIL);
        LocalDate today = LocalDate.now(clock.withZone(ISTANBUL));
        CleaningOrder completedCleaning = cleaning(owner, today.plusDays(1), "Completed cleaning");
        completeCleaning(completedCleaning, clock.instant().minusSeconds(3600));
        TransferBookingResponse completedTransfer = transfer(
                owner,
                TransferDirection.TO_AIRPORT,
                transferBookingPolicy.earliestBookingDate(),
                "Completed transfer"
        );
        completeTransfer(completedTransfer, clock.instant());

        CustomerHomeResponse latestTransfer = homeService.home(owner);
        RentalBookingResponse rental = rental(owner, today.plusDays(5), 7, "Repeat rental");
        CustomerHomeResponse transferPrimary = homeService.home(owner);

        Assertions.assertAll(
                () -> Assertions.assertEquals(PlatformService.TRANSFER, latestTransfer.repeatOpportunity().service()),
                () -> Assertions.assertEquals(completedTransfer.id(), latestTransfer.repeatOpportunity().sourceEntityId()),
                () -> Assertions.assertEquals(
                        CustomerHomePrimaryActionType.RENTAL_TRANSFER_ARRIVAL,
                        transferPrimary.primaryAction().type()
                ),
                () -> Assertions.assertEquals(PlatformService.CLEANING, transferPrimary.repeatOpportunity().service()),
                () -> Assertions.assertEquals(completedCleaning.getId(), transferPrimary.repeatOpportunity().sourceEntityId()),
                () -> Assertions.assertEquals(0, jdbcTemplate.queryForObject(
                        "select count(*) from repeat_action_event",
                        Integer.class
                )),
                () -> Assertions.assertEquals(0, jdbcTemplate.queryForObject(
                        "select count(*) from rental_transfer_action_event",
                        Integer.class
                )),
                () -> Assertions.assertEquals(rental.id(), transferPrimary.primaryAction().sourceEntityId())
        );
    }

    private CurrentCustomer customer(String subject, String email) {
        return accountService.resolveCustomer(new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.GOOGLE,
                subject,
                email,
                "Home customer",
                "en",
                email,
                true,
                false
        ));
    }

    private CleaningOrder cleaning(CurrentCustomer customer, LocalDate date, String address) {
        return cleaningOrderService.createOrder(customer, new CreateCleaningOrderCommand(
                ServiceArea.MAHMUTLAR,
                address,
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                date,
                "+905551112233",
                null,
                null,
                null
        ));
    }

    private RentalBookingResponse rental(
            CurrentCustomer customer,
            LocalDate checkIn,
            int durationDays,
            String address
    ) {
        RentalPropertyResponse draft = rentalPropertyService.createDraft();
        rentalPropertyService.update(draft.id(), new RentalPropertyDetails(
                "Дом " + draft.id(),
                "Home " + draft.id(),
                "Customer home test property",
                "Махмутлар",
                address,
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
        LocalDate allowedCheckIn = checkIn.isAfter(rentalStayPolicy.today())
                ? checkIn
                : rentalStayPolicy.today().plusDays(1);
        return rentalBookingService.create(customer, new CreateRentalBookingRequest(
                draft.id(),
                RentalTermType.DATE_RANGE,
                allowedCheckIn,
                allowedCheckIn.plusDays(durationDays),
                null,
                1,
                "+905551112233",
                null
        ));
    }

    private TransferBookingResponse transfer(
            CurrentCustomer customer,
            TransferDirection direction,
            LocalDate date,
            String address
    ) {
        var airport = transferAirportRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        var vehicle = transferVehicleRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        transferPriceRepository.findByAirport_IdAndVehicleType_IdAndDirection(
                airport.getId(), vehicle.getId(), direction
        ).orElseGet(() -> transferPriceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                direction,
                new BigDecimal("1800.00"),
                "TRY",
                true,
                clock.instant()
        )));
        return transferBookingService.create(customer, new CreateTransferBookingRequest(
                direction,
                airport.getId(),
                vehicle.getId(),
                date,
                LocalTime.of(8, 30),
                address,
                2,
                1,
                direction == TransferDirection.FROM_AIRPORT ? "TK 123" : null,
                direction == TransferDirection.FROM_AIRPORT ? LocalTime.of(7, 45) : null,
                "+905551112233",
                null,
                null,
                null,
                null
        ));
    }

    private void addCleaningBenefit(
            CurrentCustomer customer,
            RentalBookingResponse booking,
            RentalCleaningBenefitStatus status,
            String code
    ) {
        jdbcTemplate.update(
                """
                insert into rental_cleaning_benefit(
                    rental_booking_id, customer_id, code, status, created_at, available_at, version
                ) values (?, ?, ?, ?, ?, ?, 0)
                """,
                booking.id(),
                customer.customerId(),
                code,
                status.name(),
                Timestamp.from(clock.instant()),
                Timestamp.from(clock.instant())
        );
    }

    private void completeCleaning(CleaningOrder order, Instant completedAt) {
        jdbcTemplate.update(
                "update cleaning_order set status = 'COMPLETED', completed_at = ? where id = ?",
                Timestamp.from(completedAt),
                order.getId()
        );
    }

    private void completeTransfer(TransferBookingResponse booking, Instant completedAt) {
        TransferDriver driver = transferDriverRepository.saveAndFlush(new TransferDriver(
                "Home driver " + booking.id(),
                "+90555" + String.format("%07d", booking.id()),
                true,
                null,
                completedAt.minusSeconds(120)
        ));
        jdbcTemplate.update(
                """
                update transfer_booking
                   set status = 'COMPLETED', driver_id = ?, confirmed_at = ?, completed_at = ?
                 where id = ?
                """,
                driver.getId(),
                Timestamp.from(completedAt.minusSeconds(60)),
                Timestamp.from(completedAt),
                booking.id()
        );
    }

    private void setServiceStatus(String service, String status) {
        jdbcTemplate.update(
                "update platform_service_state set status = ?, version = version + 1 where service = ?",
                status,
                service
        );
        clearPlatformServiceStateCache();
    }

    private static OidcUser oidcUser(String subject, String email) {
        Instant issuedAt = Instant.parse("2026-08-30T10:00:00Z");
        OidcIdToken token = OidcIdToken.withTokenValue("home-id-token")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(3600))
                .subject(subject)
                .claim("name", "Home customer")
                .claim("email", email)
                .claim("email_verified", true)
                .build();
        return new DefaultOidcUser(Collections.emptyList(), token);
    }
}
