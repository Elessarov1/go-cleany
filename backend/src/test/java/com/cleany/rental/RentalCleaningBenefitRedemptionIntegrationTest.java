package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefit;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitIssuanceService;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitNotApplicableException;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitRepository;
import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitStatus;
import com.cleany.crossservice.rentalcleaning.RentalCleaningContextResponse;
import com.cleany.crossservice.rentalcleaning.RentalCleaningContextService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.finance.CustomerDiscountType;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProviderReferenceRepository;
import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderEventRepository;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderService;
import com.cleany.order.CleaningType;
import com.cleany.order.CreateCleaningOrderCommand;
import com.cleany.order.ServiceArea;

class RentalCleaningBenefitRedemptionIntegrationTest extends BaseIntegrationTest {

    private static final long CLEANER_ID = 123456789L;

    @Autowired
    private RentalCleaningBenefitIssuanceService issuanceService;

    @Autowired
    private RentalCleaningBenefitRepository benefitRepository;

    @Autowired
    private RentalCleaningContextService contextService;

    @Autowired
    private RentalBookingService bookingService;

    @Autowired
    private RentalPropertyService propertyService;

    @Autowired
    private RentalPropertyMediaService propertyMediaService;

    @Autowired
    private RentalStayPolicy stayPolicy;

    @Autowired
    private CleaningOrderService cleaningOrderService;

    @Autowired
    private CleaningOrderEventRepository cleaningOrderEventRepository;

    @Autowired
    private CleaningOrderRepository cleaningOrderRepository;

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
        jdbcTemplate.update("""
                update rental_cleaning_benefit
                set status = 'AVAILABLE', reserved_cleaning_order_id = null,
                    redeemed_at = null, revoked_at = null
                """);
        cleaningOrderEventRepository.deleteAll();
        cleaningOrderRepository.deleteAll();
        benefitRepository.deleteAll();
        jdbcTemplate.update("delete from rental_occupancy");
        bookingRepository.deleteAll();
        propertyMediaRepository.deleteAll();
        propertyRepository.deleteAll();
        jdbcTemplate.update("delete from referral_reward");
        jdbcTemplate.update("delete from partner_payout");
        jdbcTemplate.update("delete from referral_code");
        jdbcTemplate.update("delete from referral_partner");
        providerReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
        jdbcTemplate.update("update platform_service_state set status = 'ENABLED'");
    }

    @Test
    void ownedCheckoutBenefit_reservesReleasesAndRedeemsWithoutReuse() {
        BenefitContext context = createAvailableBenefit("950001", "benefit-lifecycle");
        LocalDate cleaningDate = context.checkOutDate().minusDays(2);

        CleaningOrder cancelledOrder = cleaningOrderService.createOrder(
                context.customer(),
                command(context.code(), cleaningDate, null)
        );
        RentalCleaningBenefit reserved = benefit();
        Assertions.assertAll(
                () -> Assertions.assertEquals(RentalCleaningBenefitStatus.RESERVED, reserved.getStatus()),
                () -> Assertions.assertEquals(cancelledOrder.getId(), reserved.getReservedCleaningOrderId()),
                () -> Assertions.assertEquals(
                        CustomerDiscountType.RENTAL_CHECKOUT_PROMO,
                        cancelledOrder.getCustomerDiscountType()
                ),
                () -> assertAmount("110.00", cancelledOrder.getCustomerDiscount()),
                () -> assertAmount("55.00", cancelledOrder.getPlatformNet())
        );

        cleaningOrderService.cancel(context.customer(), cancelledOrder.getId());
        RentalCleaningBenefit released = benefit();
        Assertions.assertAll(
                () -> Assertions.assertEquals(RentalCleaningBenefitStatus.AVAILABLE, released.getStatus()),
                () -> Assertions.assertNull(released.getReservedCleaningOrderId())
        );

        CleaningOrder completedOrder = cleaningOrderService.createOrder(
                context.customer(),
                command(context.code(), cleaningDate, null)
        );
        cleaningOrderService.acceptOrder(completedOrder.getId(), CLEANER_ID);
        cleaningOrderService.markAwaitingReport(completedOrder.getId(), CLEANER_ID);
        cleaningOrderService.completeOrder(completedOrder.getId(), CLEANER_ID, null);
        RentalCleaningBenefit redeemed = benefit();

        Assertions.assertAll(
                () -> Assertions.assertEquals(RentalCleaningBenefitStatus.REDEEMED, redeemed.getStatus()),
                () -> Assertions.assertNotNull(redeemed.getRedeemedAt()),
                () -> Assertions.assertNull(redeemed.getReservedCleaningOrderId()),
                () -> Assertions.assertThrows(
                        RentalCleaningBenefitNotApplicableException.class,
                        () -> cleaningOrderService.createOrder(
                                context.customer(),
                                command(context.code(), cleaningDate, null)
                        )
                )
        );
    }

    @Test
    void copiedCodeWrongDateAndStacking_areRejected() {
        BenefitContext context = createAvailableBenefit("950002", "benefit-validation");
        CurrentCustomer anotherCustomer = RentalTestFixtures.customer(
                customerAccountService,
                "950003"
        );

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        RentalCleaningBenefitNotApplicableException.class,
                        () -> cleaningOrderService.createOrder(
                                anotherCustomer,
                                command(context.code(), context.checkOutDate(), null)
                        )
                ),
                () -> Assertions.assertThrows(
                        RentalCleaningBenefitNotApplicableException.class,
                        () -> cleaningOrderService.createOrder(
                                context.customer(),
                                command(context.code(), context.checkOutDate().plusDays(1), null)
                        )
                ),
                () -> Assertions.assertThrows(
                        RentalCleaningBenefitNotApplicableException.class,
                        () -> cleaningOrderService.createOrder(
                                context.customer(),
                                command(context.code(), context.checkOutDate(), "GCOTHER123")
                        )
                ),
                () -> Assertions.assertEquals(0L, cleaningOrderRepository.count()),
                () -> Assertions.assertEquals(
                        RentalCleaningBenefitStatus.AVAILABLE,
                        benefit().getStatus()
                )
        );
    }

    @Test
    void concurrentReservation_exactlyOneCleaningOrderWins() throws Exception {
        BenefitContext context = createAvailableBenefit("950004", "benefit-concurrency");
        CreateCleaningOrderCommand command = command(
                context.code(),
                context.checkOutDate(),
                null
        );
        var ready = new CountDownLatch(2);
        var start = new CountDownLatch(1);

        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> createConcurrently(context.customer(), command, ready, start));
            var second = executor.submit(() -> createConcurrently(context.customer(), command, ready, start));
            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            List<Object> results = List.of(
                    first.get(15, TimeUnit.SECONDS),
                    second.get(15, TimeUnit.SECONDS)
            );

            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            1,
                            results.stream().filter(CleaningOrder.class::isInstance).count()
                    ),
                    () -> Assertions.assertEquals(
                            1,
                            results.stream()
                                    .filter(RentalCleaningBenefitNotApplicableException.class::isInstance)
                                    .count()
                    ),
                    () -> Assertions.assertEquals(1L, cleaningOrderRepository.count()),
                    () -> Assertions.assertEquals(
                            RentalCleaningBenefitStatus.RESERVED,
                            benefit().getStatus()
                    )
            );
        }
    }

    @Test
    void cleaningContext_resolvesOwnedBookingDataAndRejectsAnotherCustomer() {
        BenefitContext context = createAvailableBenefit("950005", "benefit-context");
        CurrentCustomer anotherCustomer = RentalTestFixtures.customer(
                customerAccountService,
                "950006"
        );

        RentalCleaningContextResponse response = contextService.context(
                context.customer(),
                context.bookingId()
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals("Barbaros Cd. 24", response.address()),
                () -> Assertions.assertEquals("+905551234567", response.phone()),
                () -> Assertions.assertEquals(context.code(), response.promoCode()),
                () -> Assertions.assertTrue(response.cleaningFlowAvailable()),
                () -> Assertions.assertEquals(
                        context.checkOutDate().minusDays(3),
                        response.earliestBenefitCleaningDate()
                ),
                () -> Assertions.assertThrows(
                        RentalBookingNotFoundException.class,
                        () -> contextService.context(anotherCustomer, context.bookingId())
                )
        );
    }

    @Test
    void unavailableCleaningHidesExistingBenefitWithoutRevokingIt() {
        BenefitContext context = createAvailableBenefit("950007", "benefit-service-state");
        jdbcTemplate.update(
                "update platform_service_state set status = 'DISABLED' where service = 'CLEANING'"
        );

        RentalCleaningContextResponse response = contextService.context(
                context.customer(),
                context.bookingId()
        );

        Assertions.assertAll(
                () -> Assertions.assertFalse(response.cleaningFlowAvailable()),
                () -> Assertions.assertNull(response.benefitStatus()),
                () -> Assertions.assertNull(response.promoCode()),
                () -> Assertions.assertEquals(
                        RentalCleaningBenefitStatus.AVAILABLE,
                        benefitRepository.findByRentalBookingId(context.bookingId())
                                .orElseThrow()
                                .getStatus()
                )
        );
    }

    private Object createConcurrently(
            CurrentCustomer customer,
            CreateCleaningOrderCommand command,
            CountDownLatch ready,
            CountDownLatch start
    ) throws InterruptedException {
        ready.countDown();
        start.await(5, TimeUnit.SECONDS);
        try {
            return cleaningOrderService.createOrder(customer, command);
        } catch (RentalCleaningBenefitNotApplicableException exception) {
            return exception;
        }
    }

    private BenefitContext createAvailableBenefit(String subject, String slug) {
        LocalDate today = stayPolicy.today();
        LocalDate checkOut = today.plusDays(3);
        CurrentCustomer customer = RentalTestFixtures.customer(customerAccountService, subject);
        RentalPropertyResponse property = RentalTestFixtures.publishedProperty(
                propertyService,
                propertyMediaService,
                slug,
                new BigDecimal("100.00")
        );
        LocalDate futureCheckIn = today.plusDays(5);
        RentalBookingResponse booking = bookingService.create(
                customer,
                new CreateRentalBookingRequest(
                        property.id(),
                        RentalTermType.DATE_RANGE,
                        futureCheckIn,
                        futureCheckIn.plusDays(7),
                        null,
                        2,
                        "+90 555 123 45 67",
                        null
                )
        );
        jdbcTemplate.update(
                """
                update rental_booking
                set check_in_date = ?, check_out_date = ?, duration_days = 7
                where id = ?
                """,
                today.minusDays(4),
                checkOut,
                booking.id()
        );
        issuanceService.issueEligible(today, 100);
        RentalCleaningBenefit benefit = benefitRepository
                .findByRentalBookingId(booking.id())
                .orElseThrow();
        return new BenefitContext(customer, booking.id(), checkOut, benefit.getCode());
    }

    private RentalCleaningBenefit benefit() {
        return benefitRepository.findAll().getFirst();
    }

    private static CreateCleaningOrderCommand command(
            String rentalPromoCode,
            LocalDate requestedDate,
            String referralCode
    ) {
        return new CreateCleaningOrderCommand(
                ServiceArea.MAHMUTLAR,
                "Barbaros Cd. 24",
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                requestedDate,
                "+90 555 123 45 67",
                null,
                referralCode,
                rentalPromoCode
        );
    }

    private static void assertAmount(String expected, BigDecimal actual) {
        Assertions.assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }

    private record BenefitContext(
            CurrentCustomer customer,
            long bookingId,
            LocalDate checkOutDate,
            String code
    ) {
    }
}
