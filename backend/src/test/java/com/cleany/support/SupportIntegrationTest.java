package com.cleany.support;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cleany.authorization.PlatformRole;
import com.cleany.authorization.PlatformRoleService;
import com.cleany.base.BaseIntegrationTest;
import com.cleany.catalog.PlatformService;
import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerAccountMergeService;
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

import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class SupportIntegrationTest extends BaseIntegrationTest {

    private static final String OWNER_SUBJECT = "support-owner";
    private static final String OWNER_EMAIL = "support-owner@example.test";
    private static final String ADMIN_SUBJECT = "support-admin";
    private static final String ADMIN_EMAIL = "support-admin@example.test";

    @Autowired private CustomerSupportService supportService;
    @Autowired private CustomerAccountService accountService;
    @Autowired private CustomerAccountMergeService accountMergeService;
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
    @Autowired private SupportCaseRepository supportCaseRepository;
    @Autowired private TransactionFeedbackRepository feedbackRepository;
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private TransactionTemplate transactionTemplate;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from transaction_feedback");
        jdbcTemplate.update("delete from support_case");
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
        jdbcTemplate.update("delete from customer_role");
        jdbcTemplate.update("delete from customer_external_identity");
        jdbcTemplate.update("delete from customer_account");
        jdbcTemplate.update("update platform_service_state set status = 'ENABLED', version = version + 1");
        clearPlatformServiceStateCache();
    }

    @Test
    void sourcesCoverAllVerticalsIgnoreAvailabilityAndHideForeignOwnership() throws Exception {
        CurrentCustomer owner = customer(OWNER_SUBJECT, OWNER_EMAIL);
        CurrentCustomer outsider = customer("support-outsider", "support-outsider@example.test");
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Istanbul"));
        CleaningOrder cleaning = cleaning(owner, today.plusDays(2));
        RentalBookingResponse rental = rental(owner, today.plusDays(5));
        TransferBookingResponse transfer = transfer(owner, transferBookingPolicy.earliestBookingDate());

        jdbcTemplate.update("update platform_service_state set status = 'DISABLED', version = version + 1 where service = 'CLEANING'");
        jdbcTemplate.update("update platform_service_state set status = 'IN_TEST', version = version + 1 where service in ('RENTAL', 'TRANSFER')");
        clearPlatformServiceStateCache();

        for (SourceRef source : List.of(
                new SourceRef(PlatformService.CLEANING, cleaning.getId()),
                new SourceRef(PlatformService.RENTAL, rental.id()),
                new SourceRef(PlatformService.TRANSFER, transfer.id())
        )) {
            mvc.perform(get("/api/v1/account/support/sources/{service}/{id}", source.service(), source.id())
                            .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.service").value(source.service().name()))
                    .andExpect(jsonPath("$.sourceEntityId").value(source.id()))
                    .andExpect(jsonPath("$.feedbackEligible").value(false));
        }

        mvc.perform(post("/api/v1/account/support/cases")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(caseJson(PlatformService.CLEANING, cleaning.getId(), "BOOKING_PROBLEM", null)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"));

        mvc.perform(get("/api/v1/account/support/sources/CLEANING/{id}", cleaning.getId())
                        .with(oidcLogin().oidcUser(oidcUser("support-outsider", "support-outsider@example.test"))))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/account/support/sources/CLEANING/{id}", Long.MAX_VALUE)
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL))))
                .andExpect(status().isNotFound());

        Assertions.assertThrows(
                SupportSourceNotFoundException.class,
                () -> supportService.source(outsider, PlatformService.CLEANING, cleaning.getId())
        );
    }

    @Test
    void feedbackIsImmutableAndProblemAtomicallyCreatesOrReusesCase() throws Exception {
        CurrentCustomer owner = customer(OWNER_SUBJECT, OWNER_EMAIL);
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Istanbul"));
        CleaningOrder goodCleaning = cleaning(owner, today.plusDays(1));
        CleaningOrder unfinishedCleaning = cleaning(owner, today.plusDays(2));
        RentalBookingResponse problemRental = rental(owner, today.plusDays(10));
        complete("cleaning_order", goodCleaning.getId());
        complete("rental_booking", problemRental.id());

        String good = feedbackJson(PlatformService.CLEANING, goodCleaning.getId(), "GOOD", null, null);
        mvc.perform(post("/api/v1/account/support/feedback")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(good))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback.outcome").value("GOOD"))
                .andExpect(jsonPath("$.latestCase").doesNotExist());
        mvc.perform(post("/api/v1/account/support/feedback")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(good))
                .andExpect(status().isOk());
        mvc.perform(post("/api/v1/account/support/feedback")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(feedbackJson(
                                PlatformService.CLEANING,
                                goodCleaning.getId(),
                                "PROBLEM",
                                "QUALITY_PROBLEM",
                                "Changed answer"
                        )))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/v1/account/support/feedback")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(feedbackJson(
                                PlatformService.CLEANING,
                                unfinishedCleaning.getId(),
                                "GOOD",
                                null,
                                null
                        )))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/account/support/feedback")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(feedbackJson(
                                PlatformService.RENTAL,
                                problemRental.id(),
                                "PROBLEM",
                                null,
                                "Missing category"
                        )))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/account/support/cases")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(caseJson(
                                PlatformService.CLEANING,
                                unfinishedCleaning.getId(),
                                "OTHER",
                                "x".repeat(2001)
                        )))
                .andExpect(status().isBadRequest());

        MvcResult opened = mvc.perform(post("/api/v1/account/support/cases")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(caseJson(PlatformService.RENTAL, problemRental.id(), "QUALITY_PROBLEM", "No hot water")))
                .andExpect(status().isCreated())
                .andReturn();
        long openedCaseId = objectMapper.readTree(opened.getResponse().getContentAsString()).get("id").asLong();

        mvc.perform(post("/api/v1/account/support/feedback")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(feedbackJson(
                                PlatformService.RENTAL,
                                problemRental.id(),
                                "PROBLEM",
                                "PROVIDER_NO_SHOW",
                                "Host is absent"
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback.outcome").value("PROBLEM"))
                .andExpect(jsonPath("$.feedback.supportCaseId").value(openedCaseId))
                .andExpect(jsonPath("$.latestCase.id").value(openedCaseId));

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, feedbackRepository.count()),
                () -> Assertions.assertEquals(1, supportCaseRepository.count())
        );
    }

    @Test
    void concurrentOpenCreatesOneCaseAndAdminCanResolveThenCustomerCanReopen() throws Exception {
        CurrentCustomer owner = customer(OWNER_SUBJECT, OWNER_EMAIL);
        CurrentCustomer admin = customer(ADMIN_SUBJECT, ADMIN_EMAIL);
        roleService.ensureRole(admin.customerId(), PlatformRole.ADMIN);
        CleaningOrder cleaning = cleaning(owner, LocalDate.now(ZoneId.of("Europe/Istanbul")).plusDays(1));
        CreateSupportCaseRequest request = new CreateSupportCaseRequest(
                PlatformService.CLEANING,
                cleaning.getId(),
                SupportCaseCategory.PROVIDER_LATE,
                "Waiting"
        );

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> createAfterLatch(owner, request, ready, start));
            var second = executor.submit(() -> createAfterLatch(owner, request, ready, start));
            Assertions.assertTrue(ready.await(10, TimeUnit.SECONDS));
            start.countDown();
            SupportCaseCreationResult firstResult = first.get(20, TimeUnit.SECONDS);
            SupportCaseCreationResult secondResult = second.get(20, TimeUnit.SECONDS);

            Assertions.assertAll(
                    () -> Assertions.assertEquals(firstResult.supportCase().id(), secondResult.supportCase().id()),
                    () -> Assertions.assertEquals(1, supportCaseRepository.count()),
                    () -> Assertions.assertEquals(1, List.of(firstResult, secondResult).stream()
                            .filter(SupportCaseCreationResult::created)
                            .count())
            );
        }

        long caseId = supportCaseRepository.findAll().getFirst().getId();
        mvc.perform(get("/api/v1/admin/support/cases")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL))))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/admin/support/cases?status=OPEN&service=CLEANING&page=0&size=20")
                        .with(oidcLogin().oidcUser(oidcUser(ADMIN_SUBJECT, ADMIN_EMAIL))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(caseId))
                .andExpect(jsonPath("$.content[0].sourceAdminPath")
                        .value("/admin/cleaning/orders/" + cleaning.getId()));
        mvc.perform(post("/api/v1/admin/support/cases/{id}/resolve", caseId)
                        .with(oidcLogin().oidcUser(oidcUser(ADMIN_SUBJECT, ADMIN_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resolutionComment\":\"  \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/admin/support/cases/{id}/resolve", caseId)
                        .with(oidcLogin().oidcUser(oidcUser(ADMIN_SUBJECT, ADMIN_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resolutionComment\":\"Provider contacted the customer\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.status").value("RESOLVED"))
                .andExpect(jsonPath("$.resolutionComment").value("Provider contacted the customer"));
        mvc.perform(post("/api/v1/admin/support/cases/{id}/resolve", caseId)
                        .with(oidcLogin().oidcUser(oidcUser(ADMIN_SUBJECT, ADMIN_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resolutionComment\":\"Again\"}"))
                .andExpect(status().isConflict());

        mvc.perform(get("/api/v1/account/support/sources/CLEANING/{id}", cleaning.getId())
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latestCase.status").value("RESOLVED"))
                .andExpect(jsonPath("$.latestCase.resolutionComment")
                        .value("Provider contacted the customer"));
        mvc.perform(post("/api/v1/account/support/cases")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(caseJson(PlatformService.CLEANING, cleaning.getId(), "OTHER", "New issue")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(org.hamcrest.Matchers.not(caseId)));

        Integer notificationCount = jdbcTemplate.queryForObject(
                "select count(*) from customer_notification where customer_id = ? and type = 'SUPPORT_CASE_CREATED'",
                Integer.class,
                admin.customerId()
        );
        Assertions.assertEquals(2, notificationCount);
    }

    @Test
    void negativeTransferFeedbackCreatesOneCaseAndOneNotificationPerPersistedAdmin() throws Exception {
        CurrentCustomer owner = customer(OWNER_SUBJECT, OWNER_EMAIL);
        CurrentCustomer firstAdmin = customer(ADMIN_SUBJECT, ADMIN_EMAIL);
        CurrentCustomer secondAdmin = customer("support-admin-two", "support-admin-two@example.test");
        roleService.ensureRole(firstAdmin.customerId(), PlatformRole.ADMIN);
        roleService.ensureRole(secondAdmin.customerId(), PlatformRole.ADMIN);
        TransferBookingResponse transfer = transfer(owner, transferBookingPolicy.earliestBookingDate());
        completeTransfer(transfer);

        String problem = feedbackJson(
                PlatformService.TRANSFER,
                transfer.id(),
                "PROBLEM",
                "PROVIDER_LATE",
                "Driver was late"
        );
        mvc.perform(post("/api/v1/account/support/feedback")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(problem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latestCase.category").value("PROVIDER_LATE"));
        mvc.perform(post("/api/v1/account/support/feedback")
                        .with(oidcLogin().oidcUser(oidcUser(OWNER_SUBJECT, OWNER_EMAIL)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(problem))
                .andExpect(status().isOk());

        Integer notificationCount = jdbcTemplate.queryForObject(
                "select count(*) from customer_notification where type = 'SUPPORT_CASE_CREATED'",
                Integer.class
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, supportCaseRepository.count()),
                () -> Assertions.assertEquals(1, feedbackRepository.count()),
                () -> Assertions.assertEquals(2, notificationCount)
        );
    }

    @Test
    void explicitAccountMergeMovesTransferSupportAndFeedbackOwnershipTogether() {
        CurrentCustomer target = customer(OWNER_SUBJECT, OWNER_EMAIL);
        CurrentCustomer source = accountService.resolveCustomer(new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.TELEGRAM,
                "912345678",
                null,
                "Support Telegram customer",
                "ru",
                null,
                false,
                true
        ));
        TransferBookingResponse transfer = transfer(source, transferBookingPolicy.earliestBookingDate());
        completeTransfer(transfer);
        SupportCaseCreationResult supportCase = supportService.createCase(source, new CreateSupportCaseRequest(
                PlatformService.TRANSFER,
                transfer.id(),
                SupportCaseCategory.OTHER,
                "Merge me"
        ));
        long feedbackId = transactionTemplate.execute(status -> feedbackRepository.saveAndFlush(
                new TransactionFeedback(
                        source.customerId(),
                        PlatformService.TRANSFER,
                        transfer.id(),
                        FeedbackOutcome.GOOD,
                        null,
                        null,
                        null,
                        Instant.parse("2026-08-30T12:00:00Z")
                )
        ).getId());

        transactionTemplate.executeWithoutResult(status ->
                accountMergeService.mergeInto(target.customerId(), source.customerId())
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(target.customerId(), jdbcTemplate.queryForObject(
                        "select customer_id from transfer_booking where id = ?", Long.class, transfer.id()
                )),
                () -> Assertions.assertEquals(
                        target.customerId(),
                        supportCaseRepository.findById(supportCase.supportCase().id()).orElseThrow().getCustomerId()
                ),
                () -> Assertions.assertEquals(
                        target.customerId(),
                        feedbackRepository.findById(feedbackId).orElseThrow().getCustomerId()
                ),
                () -> Assertions.assertEquals(0, jdbcTemplate.queryForObject(
                        "select count(*) from customer_account where id = ?", Integer.class, source.customerId()
                ))
        );
    }

    private SupportCaseCreationResult createAfterLatch(
            CurrentCustomer owner,
            CreateSupportCaseRequest request,
            CountDownLatch ready,
            CountDownLatch start
    ) throws InterruptedException {
        ready.countDown();
        if (!start.await(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Support concurrency test did not start");
        }
        return supportService.createCase(owner, request);
    }

    private CurrentCustomer customer(String subject, String email) {
        return accountService.resolveCustomer(new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.GOOGLE,
                subject,
                email,
                "Support customer",
                "en",
                email,
                true,
                false
        ));
    }

    private CleaningOrder cleaning(CurrentCustomer customer, LocalDate date) {
        return cleaningOrderService.createOrder(customer, new CreateCleaningOrderCommand(
                ServiceArea.MAHMUTLAR,
                "Support cleaning address",
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

    private RentalBookingResponse rental(CurrentCustomer customer, LocalDate checkIn) {
        RentalPropertyResponse draft = rentalPropertyService.createDraft();
        rentalPropertyService.update(draft.id(), new RentalPropertyDetails(
                "Квартира поддержки " + draft.id(),
                "Support apartment " + draft.id(),
                "Support test property",
                "Махмутлар",
                "Support rental address",
                1,
                1,
                1,
                2,
                new BigDecimal("50.00"),
                2,
                new BigDecimal("100.00"),
                "EUR",
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

    private TransferBookingResponse transfer(CurrentCustomer customer, LocalDate date) {
        var airport = transferAirportRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        var vehicle = transferVehicleRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        transferPriceRepository.findByAirport_IdAndVehicleType_IdAndDirection(
                airport.getId(), vehicle.getId(), TransferDirection.TO_AIRPORT
        ).orElseGet(() -> transferPriceRepository.saveAndFlush(new TransferPrice(
                airport,
                vehicle,
                TransferDirection.TO_AIRPORT,
                new BigDecimal("1800.00"),
                "USD",
                true,
                Instant.parse("2026-08-01T10:00:00Z")
        )));
        return transferBookingService.create(customer, new CreateTransferBookingRequest(
                TransferDirection.TO_AIRPORT,
                airport.getId(),
                vehicle.getId(),
                date,
                LocalTime.of(8, 30),
                "Support transfer address",
                2,
                1,
                null,
                null,
                "+905551112233",
                null,
                null,
                null
        ));
    }

    private void complete(String table, long id) {
        jdbcTemplate.update(
                "update " + table + " set status = 'COMPLETED', completed_at = ? where id = ?",
                Timestamp.from(Instant.parse("2026-08-30T08:00:00Z").plusSeconds(id)),
                id
        );
    }

    private void completeTransfer(TransferBookingResponse booking) {
        Instant completedAt = Instant.parse("2026-08-30T08:00:00Z").plusSeconds(booking.id());
        TransferDriver driver = transferDriverRepository.saveAndFlush(new TransferDriver(
                "Support driver",
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
    }

    private static String caseJson(
            PlatformService service,
            long sourceEntityId,
            String category,
            String description
    ) {
        return """
                {"service":"%s","sourceEntityId":%d,"category":"%s","description":%s}
                """.formatted(service, sourceEntityId, category, jsonString(description));
    }

    private static String feedbackJson(
            PlatformService service,
            long sourceEntityId,
            String outcome,
            String category,
            String comment
    ) {
        return """
                {"service":"%s","sourceEntityId":%d,"outcome":"%s","category":%s,"comment":%s}
                """.formatted(
                service,
                sourceEntityId,
                outcome,
                jsonString(category),
                jsonString(comment)
        );
    }

    private static String jsonString(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static OidcUser oidcUser(String subject, String email) {
        Instant issuedAt = Instant.parse("2026-08-30T10:00:00Z");
        OidcIdToken token = OidcIdToken.withTokenValue("support-id-token-" + subject)
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(3600))
                .subject(subject)
                .claim("name", "Support customer")
                .claim("email", email)
                .claim("email_verified", true)
                .build();
        return new DefaultOidcUser(Collections.emptyList(), token);
    }

    private record SourceRef(PlatformService service, long id) {
    }
}
