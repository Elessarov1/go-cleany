package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.authorization.PlatformRole;
import com.cleany.authorization.PlatformRoleService;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefit;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitIssuanceResult;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitIssuanceService;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitIssuedEvent;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitRepository;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitStatus;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProviderReferenceRepository;
import com.cleany.notification.CustomerNotificationDispatcher;
import com.cleany.catalog.PlatformServiceStatus;

@RecordApplicationEvents
class RentalCleaningBenefitIssuanceIntegrationTest extends BaseIntegrationTest {

    private static final long ADMIN_ACTOR_ID = 900001L;

    @Autowired
    private RentalCleaningBenefitIssuanceService issuanceService;

    @Autowired
    private RentalCleaningBenefitRepository benefitRepository;

    @Autowired
    private RentalBookingService bookingService;

    @Autowired
    private AdminRentalBookingService adminBookingService;

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
    private PlatformRoleService roleService;

    @Autowired
    private MediaProviderReferenceRepository providerReferenceRepository;

    @Autowired
    private MediaAssetRepository mediaAssetRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ApplicationEvents applicationEvents;

    @MockitoBean
    private CustomerNotificationDispatcher notificationDispatcher;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        benefitRepository.deleteAll();
        jdbcTemplate.update("delete from rental_occupancy");
        bookingRepository.deleteAll();
        propertyMediaRepository.deleteAll();
        propertyRepository.deleteAll();
        providerReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
        jdbcTemplate.update("update platform_service_state set status = 'ENABLED'");
    }

    @Test
    void confirmedRentalInCheckoutWindow_schedulerRetriesCreateExactlyOneBenefit() {
        LocalDate today = stayPolicy.today();
        CurrentCustomer customer = RentalTestFixtures.customer(customerAccountService, "940001");
        RentalBookingResponse booking = futureBooking(customer, "benefit-idempotency");
        moveBookingDates(booking.id(), today.minusDays(7), today.plusDays(3));

        RentalCleaningBenefitIssuanceResult first = issuanceService.issueEligible(today, 100);
        RentalCleaningBenefitIssuanceResult retry = issuanceService.issueEligible(today, 100);
        RentalCleaningBenefit benefit = benefitRepository
                .findByRentalBookingId(booking.id())
                .orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, first.issued()),
                () -> Assertions.assertEquals(0, first.failed()),
                () -> Assertions.assertEquals(0, retry.candidates()),
                () -> Assertions.assertEquals(1L, benefitRepository.count()),
                () -> Assertions.assertEquals(customer.customerId(), benefit.getCustomerId()),
                () -> Assertions.assertEquals(
                        RentalCleaningBenefitStatus.AVAILABLE,
                        benefit.getStatus()
                ),
                () -> Assertions.assertEquals(
                        1L,
                        applicationEvents.stream(RentalCleaningBenefitIssuedEvent.class).count()
                )
        );
    }

    @Test
    void checkInAndDaysBeforeCheckoutWindow_doNotIssueOrPublishPromo() {
        LocalDate today = stayPolicy.today();
        CurrentCustomer customer = RentalTestFixtures.customer(customerAccountService, "940011");
        RentalBookingResponse booking = futureBooking(customer, "benefit-not-at-check-in");
        moveBookingDates(booking.id(), today, today.plusDays(7));

        RentalCleaningBenefitIssuanceResult atCheckIn = issuanceService.issueEligible(today, 100);
        RentalCleaningBenefitIssuanceResult beforeWindow = issuanceService.issueEligible(
                today.plusDays(3),
                100
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, atCheckIn.candidates()),
                () -> Assertions.assertEquals(0, beforeWindow.candidates()),
                () -> Assertions.assertFalse(
                        benefitRepository.existsByRentalBookingId(booking.id())
                ),
                () -> Assertions.assertEquals(
                        0L,
                        applicationEvents.stream(RentalCleaningBenefitIssuedEvent.class).count()
                )
        );
    }

    @Test
    void inTestIssuesOnlyForAdminAndDisabledIssuesForNobody() {
        LocalDate today = stayPolicy.today();
        CurrentCustomer ordinary = RentalTestFixtures.customer(customerAccountService, "940012");
        CurrentCustomer admin = RentalTestFixtures.customer(
                customerAccountService,
                Long.toString(ADMIN_ACTOR_ID)
        );
        roleService.ensureRole(admin.customerId(), PlatformRole.ADMIN);
        RentalBookingResponse ordinaryBooking = futureBooking(ordinary, "benefit-in-test-customer");
        RentalBookingResponse adminBooking = futureBooking(admin, "benefit-in-test-admin");
        moveBookingDates(ordinaryBooking.id(), today.minusDays(5), today.plusDays(2));
        moveBookingDates(adminBooking.id(), today.minusDays(5), today.plusDays(2));
        setCleaningStatus(PlatformServiceStatus.IN_TEST);

        RentalCleaningBenefitIssuanceResult inTest = issuanceService.issueEligible(today, 100);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, inTest.issued()),
                () -> Assertions.assertEquals(1, inTest.ineligible()),
                () -> Assertions.assertFalse(
                        benefitRepository.existsByRentalBookingId(ordinaryBooking.id())
                ),
                () -> Assertions.assertTrue(
                        benefitRepository.existsByRentalBookingId(adminBooking.id())
                )
        );

        CurrentCustomer disabledCustomer = RentalTestFixtures.customer(
                customerAccountService,
                "940013"
        );
        RentalBookingResponse disabledBooking = futureBooking(
                disabledCustomer,
                "benefit-disabled"
        );
        moveBookingDates(disabledBooking.id(), today.minusDays(5), today.plusDays(2));
        setCleaningStatus(PlatformServiceStatus.DISABLED);

        RentalCleaningBenefitIssuanceResult disabled = issuanceService.issueEligible(today, 100);

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, disabled.issued()),
                () -> Assertions.assertFalse(
                        benefitRepository.existsByRentalBookingId(disabledBooking.id())
                ),
                () -> Assertions.assertEquals(
                        RentalCleaningBenefitStatus.AVAILABLE,
                        benefitRepository.findByRentalBookingId(adminBooking.id())
                                .orElseThrow()
                                .getStatus()
                )
        );
    }

    @Test
    void cancelledBeforeCheckIn_neverReceivesBenefit() {
        CurrentCustomer customer = RentalTestFixtures.customer(customerAccountService, "940002");
        CurrentCustomer admin = RentalTestFixtures.customer(
                customerAccountService,
                Long.toString(ADMIN_ACTOR_ID)
        );
        roleService.ensureRole(admin.customerId(), PlatformRole.ADMIN);
        RentalBookingResponse booking = futureBooking(customer, "benefit-cancelled-before-start");
        adminBookingService.cancel(
                admin.customerId(),
                booking.id(),
                new AdminCancelRentalBookingRequest("Cancelled before arrival", false)
        );

        RentalCleaningBenefitIssuanceResult result = issuanceService.issueEligible(
                stayPolicy.today().plusYears(1),
                100
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, result.candidates()),
                () -> Assertions.assertFalse(
                        benefitRepository.existsByRentalBookingId(booking.id())
                )
        );
    }

    @Test
    void issuedAvailableBenefit_adminCancellationRevokesIt() {
        LocalDate today = stayPolicy.today();
        CurrentCustomer customer = RentalTestFixtures.customer(customerAccountService, "940003");
        CurrentCustomer admin = RentalTestFixtures.customer(
                customerAccountService,
                Long.toString(ADMIN_ACTOR_ID)
        );
        roleService.ensureRole(admin.customerId(), PlatformRole.ADMIN);
        RentalBookingResponse booking = futureBooking(customer, "benefit-revocation");
        moveBookingDates(booking.id(), today.minusDays(7), today.plusDays(3));
        issuanceService.issueEligible(today, 100);

        adminBookingService.cancel(
                admin.customerId(),
                booking.id(),
                new AdminCancelRentalBookingRequest("Rental became unavailable", false)
        );
        RentalCleaningBenefit benefit = benefitRepository
                .findByRentalBookingId(booking.id())
                .orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        RentalCleaningBenefitStatus.REVOKED,
                        benefit.getStatus()
                ),
                () -> Assertions.assertNotNull(benefit.getRevokedAt())
        );
    }

    @Test
    void notificationDeliveryFailure_doesNotRollBackBenefitCreation() {
        LocalDate today = stayPolicy.today();
        CurrentCustomer customer = RentalTestFixtures.customer(customerAccountService, "940004");
        RentalBookingResponse booking = futureBooking(customer, "benefit-notification-failure");
        moveBookingDates(booking.id(), today.minusDays(7), today.plusDays(3));
        Mockito.when(notificationDispatcher.send(
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any()
        )).thenThrow(new IllegalStateException("channel unavailable"));

        Assertions.assertDoesNotThrow(() -> issuanceService.issueEligible(today, 100));

        Assertions.assertTrue(benefitRepository.existsByRentalBookingId(booking.id()));
    }

    private RentalBookingResponse futureBooking(CurrentCustomer customer, String slug) {
        RentalPropertyResponse property = RentalTestFixtures.publishedProperty(
                propertyService,
                propertyMediaService,
                slug,
                new BigDecimal("100.00")
        );
        LocalDate checkIn = stayPolicy.today().plusDays(5);
        return bookingService.create(
                customer,
                new CreateRentalBookingRequest(
                        property.id(),
                        RentalTermType.DATE_RANGE,
                        checkIn,
                        checkIn.plusDays(7),
                        null,
                        2,
                        "+90 555 123 45 67",
                        null
                )
        );
    }

    private void moveBookingDates(long bookingId, LocalDate checkIn, LocalDate checkOut) {
        jdbcTemplate.update(
                """
                update rental_booking
                set check_in_date = ?, check_out_date = ?, duration_days = ?
                where id = ?
                """,
                checkIn,
                checkOut,
                Math.toIntExact(checkOut.toEpochDay() - checkIn.toEpochDay()),
                bookingId
        );
    }

    private void setCleaningStatus(PlatformServiceStatus status) {
        jdbcTemplate.update(
                "update platform_service_state set status = ? where service = 'CLEANING'",
                status.name()
        );
        clearPlatformServiceStateCache();
    }
}
