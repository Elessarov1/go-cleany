package com.cleany.customer.activity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.cleany.base.BaseIntegrationTest;
import com.cleany.catalog.PlatformService;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CustomerActivityIntegrationTest extends BaseIntegrationTest {

    private static final String OWNER_SUBJECT = "activity-owner";
    private static final String OWNER_EMAIL = "activity-owner@example.test";

    @Autowired private CustomerActivityService activityService;
    @Autowired private CustomerAccountService accountService;
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

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from customer_notification");
        jdbcTemplate.update("delete from transfer_booking");
        jdbcTemplate.update("delete from transfer_driver");
        jdbcTemplate.update("delete from transfer_price");
        jdbcTemplate.update("delete from rental_occupancy");
        jdbcTemplate.update("delete from rental_cleaning_benefit");
        jdbcTemplate.update("delete from rental_booking");
        jdbcTemplate.update("delete from rental_property_amenity");
        jdbcTemplate.update("delete from rental_property");
        jdbcTemplate.update("delete from cleaning_order");
        jdbcTemplate.update("delete from customer_acquisition");
        jdbcTemplate.update("delete from customer_external_identity");
        jdbcTemplate.update("delete from customer_account");
        jdbcTemplate.update("update platform_service_state set status = 'ENABLED', version = version + 1");
        clearPlatformServiceStateCache();
    }

    @Test
    void activity_combinesOwnedVerticalsSortsSectionsAndKeepsPriceSnapshots() throws Exception {
        CurrentCustomer owner = customer(OWNER_SUBJECT, OWNER_EMAIL);
        CurrentCustomer outsider = customer("activity-outsider", "activity-outsider@example.test");
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Istanbul"));

        TransferBookingResponse activeTransfer = transfer(owner, transferBookingPolicy.earliestBookingDate(), "USD");
        RentalBookingResponse activeRental = rental(owner, today.plusDays(3), "EUR");
        CleaningOrder activeCleaning = cleaning(owner, today.plusDays(6), "Upcoming cleaning");

        CleaningOrder rejectedCleaning = cleaning(owner, today.plusDays(2), "Rejected cleaning");
        jdbcTemplate.update(
                "update cleaning_order set status = 'REJECTED', created_at = ? where id = ?",
                Timestamp.from(Instant.parse("2026-08-20T08:00:00Z")),
                rejectedCleaning.getId()
        );
        RentalBookingResponse cancelledRental = rental(owner, today.plusDays(10), "EUR");
        jdbcTemplate.update(
                "update rental_booking set status = 'CANCELLED_BY_ADMIN', cancelled_at = ? where id = ?",
                Timestamp.from(Instant.parse("2026-08-21T08:00:00Z")),
                cancelledRental.id()
        );
        TransferBookingResponse rejectedTransfer = transfer(owner, transferBookingPolicy.earliestBookingDate().plusDays(2), "USD");
        jdbcTemplate.update(
                "update transfer_booking set status = 'REJECTED', rejected_at = ? where id = ?",
                Timestamp.from(Instant.parse("2026-08-22T08:00:00Z")),
                rejectedTransfer.id()
        );
        cleaning(outsider, today.plusDays(1), "Private cleaning");

        CustomerActivityResponse result = activityService.activity(owner);
        CustomerActivityResponse outsiderResult = activityService.activity(outsider);

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, result.activeAndUpcoming().size()),
                () -> Assertions.assertEquals(
                        java.util.List.of(PlatformService.TRANSFER, PlatformService.RENTAL, PlatformService.CLEANING),
                        result.activeAndUpcoming().stream().map(CustomerActivityItem::service).toList()
                ),
                () -> Assertions.assertEquals(activeTransfer.id(), result.activeAndUpcoming().getFirst().entityId()),
                () -> Assertions.assertEquals("USD", result.activeAndUpcoming().getFirst().currency()),
                () -> Assertions.assertEquals(activeRental.id(), result.activeAndUpcoming().get(1).entityId()),
                () -> Assertions.assertEquals("EUR", result.activeAndUpcoming().get(1).currency()),
                () -> Assertions.assertEquals(activeCleaning.getId(), result.activeAndUpcoming().get(2).entityId()),
                () -> Assertions.assertEquals("TRY", result.activeAndUpcoming().get(2).currency()),
                () -> Assertions.assertEquals(
                        java.util.List.of(PlatformService.TRANSFER, PlatformService.RENTAL, PlatformService.CLEANING),
                        result.history().stream().map(CustomerActivityItem::service).toList()
                ),
                () -> Assertions.assertEquals("/transfer/bookings/" + rejectedTransfer.id(), result.history().getFirst().targetPath()),
                () -> Assertions.assertEquals(1, outsiderResult.activeAndUpcoming().size()),
                () -> Assertions.assertTrue(outsiderResult.history().isEmpty())
        );

        mvc.perform(get("/api/v1/account/activity")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeAndUpcoming.length()").value(3))
                .andExpect(jsonPath("$.history.length()").value(3))
                .andExpect(jsonPath("$.history[0].entityId").value(rejectedTransfer.id()))
                .andExpect(jsonPath("$.activeAndUpcoming[0].amount").value(1800.0))
                .andExpect(jsonPath("$.activeAndUpcoming[0].currency").value("USD"));
    }

    @Test
    void activity_keepsEveryTerminalStatusVisibleWhenServicesAreUnavailable() {
        CurrentCustomer owner = customer(OWNER_SUBJECT, OWNER_EMAIL);
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Istanbul"));

        updateCleaningStatus(cleaning(owner, today.plusDays(1), "Completed"), "COMPLETED", "completed_at");
        updateCleaningStatus(cleaning(owner, today.plusDays(2), "Cancelled"), "CANCELLED", null);
        updateCleaningStatus(cleaning(owner, today.plusDays(3), "Rejected"), "REJECTED", null);

        updateRentalStatus(rental(owner, today.plusDays(20), "EUR"), "COMPLETED", "completed_at");
        updateRentalStatus(rental(owner, today.plusDays(30), "EUR"), "CANCELLED_BY_CUSTOMER", "cancelled_at");
        updateRentalStatus(rental(owner, today.plusDays(40), "EUR"), "CANCELLED_BY_ADMIN", "cancelled_at");

        updateTransferStatus(transfer(owner, transferBookingPolicy.earliestBookingDate(), "USD"), "COMPLETED", "completed_at");
        updateTransferStatus(transfer(owner, transferBookingPolicy.earliestBookingDate().plusDays(1), "USD"), "CANCELLED", "cancelled_at");
        updateTransferStatus(transfer(owner, transferBookingPolicy.earliestBookingDate().plusDays(2), "USD"), "REJECTED", "rejected_at");

        jdbcTemplate.update("update platform_service_state set status = 'DISABLED', version = version + 1 where service in ('CLEANING', 'TRANSFER')");
        jdbcTemplate.update("update platform_service_state set status = 'IN_TEST', version = version + 1 where service = 'RENTAL'");
        clearPlatformServiceStateCache();

        CustomerActivityResponse result = activityService.activity(owner);
        Set<String> statuses = result.history().stream()
                .map(item -> item.service() + ":" + item.status())
                .collect(Collectors.toSet());

        Assertions.assertAll(
                () -> Assertions.assertTrue(result.activeAndUpcoming().isEmpty()),
                () -> Assertions.assertEquals(9, result.history().size()),
                () -> Assertions.assertEquals(Set.of(
                        "CLEANING:COMPLETED",
                        "CLEANING:CANCELLED",
                        "CLEANING:REJECTED",
                        "RENTAL:COMPLETED",
                        "RENTAL:CANCELLED_BY_CUSTOMER",
                        "RENTAL:CANCELLED_BY_ADMIN",
                        "TRANSFER:COMPLETED",
                        "TRANSFER:CANCELLED",
                        "TRANSFER:REJECTED"
                ), statuses)
        );
    }

    @Test
    void activity_emptyCustomerGetsEmptySectionsAndAnonymousRequestIsRejected() throws Exception {
        CurrentCustomer customer = customer(OWNER_SUBJECT, OWNER_EMAIL);

        CustomerActivityResponse result = activityService.activity(customer);

        Assertions.assertAll(
                () -> Assertions.assertTrue(result.activeAndUpcoming().isEmpty()),
                () -> Assertions.assertTrue(result.history().isEmpty())
        );
        mvc.perform(get("/api/v1/account/activity"))
                .andExpect(status().isUnauthorized());
    }

    private CurrentCustomer customer(String subject, String email) {
        return accountService.resolveCustomer(new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.GOOGLE,
                subject,
                email,
                "Activity customer",
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

    private RentalBookingResponse rental(CurrentCustomer customer, LocalDate checkIn, String currency) {
        RentalPropertyResponse draft = rentalPropertyService.createDraft();
        rentalPropertyService.update(draft.id(), new RentalPropertyDetails(
                "Квартира " + draft.id(),
                "Apartment " + draft.id(),
                "Activity test property",
                "Махмутлар",
                "Rental activity address",
                1,
                1,
                1,
                2,
                new BigDecimal("50.00"),
                2,
                new BigDecimal("100.00"),
                currency,
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
                allowedCheckIn.plusDays(7),
                null,
                1,
                "+905551112233",
                null
        ));
    }

    private TransferBookingResponse transfer(CurrentCustomer customer, LocalDate date, String currency) {
        var airport = transferAirportRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        var vehicle = transferVehicleRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        transferPriceRepository.findByAirport_IdAndVehicleType_IdAndDirection(
                airport.getId(), vehicle.getId(), TransferDirection.TO_AIRPORT
        ).orElseGet(() -> transferPriceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                TransferDirection.TO_AIRPORT,
                new BigDecimal("1800.00"),
                currency,
                true,
                Instant.parse("2026-08-01T10:00:00Z")
        )));
        return transferBookingService.create(customer, new CreateTransferBookingRequest(
                TransferDirection.TO_AIRPORT,
                airport.getId(),
                vehicle.getId(),
                date,
                LocalTime.of(8, 30),
                "Transfer activity address",
                2,
                1,
                null,
                null,
                "+905551112233",
                null
        ));
    }

    private void updateCleaningStatus(CleaningOrder order, String status, String timestampColumn) {
        updateStatus("cleaning_order", order.getId(), status, timestampColumn);
    }

    private void updateRentalStatus(RentalBookingResponse booking, String status, String timestampColumn) {
        updateStatus("rental_booking", booking.id(), status, timestampColumn);
    }

    private void updateTransferStatus(TransferBookingResponse booking, String status, String timestampColumn) {
        if ("COMPLETED".equals(status)) {
            Instant completedAt = Instant.parse("2026-08-25T10:00:00Z").plusSeconds(booking.id());
            TransferDriver driver = transferDriverRepository.saveAndFlush(new TransferDriver(
                    "Activity driver",
                    "+905551119999",
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
            return;
        }
        updateStatus("transfer_booking", booking.id(), status, timestampColumn);
    }

    private void updateStatus(String table, long id, String status, String timestampColumn) {
        if (timestampColumn == null) {
            jdbcTemplate.update("update " + table + " set status = ? where id = ?", status, id);
            return;
        }
        jdbcTemplate.update(
                "update " + table + " set status = ?, " + timestampColumn + " = ? where id = ?",
                status,
                Timestamp.from(Instant.parse("2026-08-25T10:00:00Z").plusSeconds(id)),
                id
        );
    }

    private static OidcUser oidcUser(String subject, String email) {
        Instant issuedAt = Instant.parse("2026-08-30T10:00:00Z");
        OidcIdToken token = OidcIdToken.withTokenValue("activity-id-token")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(3600))
                .subject(subject)
                .claim("name", "Activity customer")
                .claim("email", email)
                .claim("email_verified", true)
                .build();
        return new DefaultOidcUser(Collections.emptyList(), token);
    }
}
