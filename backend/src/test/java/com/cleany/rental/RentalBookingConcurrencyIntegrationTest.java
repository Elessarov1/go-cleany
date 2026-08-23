package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProviderReferenceRepository;

class RentalBookingConcurrencyIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RentalBookingService bookingService;

    @Autowired
    private RentalOccupancyService occupancyService;

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
    private MediaProviderReferenceRepository providerReferenceRepository;

    @Autowired
    private MediaAssetRepository mediaAssetRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from rental_occupancy");
        bookingRepository.deleteAll();
        propertyMediaRepository.deleteAll();
        propertyRepository.deleteAll();
        providerReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void twoCustomersBookSameDatesConcurrently_exactlyOneSucceeds() throws Exception {
        RentalPropertyResponse property = RentalTestFixtures.publishedProperty(
                propertyService,
                propertyMediaService,
                "concurrent-booking",
                new BigDecimal("100.00")
        );
        CurrentCustomer firstCustomer = RentalTestFixtures.customer(customerAccountService, "920001");
        CurrentCustomer secondCustomer = RentalTestFixtures.customer(customerAccountService, "920002");
        LocalDate checkIn = stayPolicy.today().plusDays(5);
        LocalDate checkOut = checkIn.plusDays(7);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Object> firstAttempt = attempt(
                start,
                firstCustomer,
                property.id(),
                checkIn,
                checkOut
        );
        Callable<Object> secondAttempt = attempt(
                start,
                secondCustomer,
                property.id(),
                checkIn,
                checkOut
        );

        try (var executor = Executors.newFixedThreadPool(2)) {
            var firstFuture = executor.submit(firstAttempt);
            var secondFuture = executor.submit(secondAttempt);
            start.countDown();
            List<Object> results = List.of(firstFuture, secondFuture).stream()
                    .map(future -> {
                        try {
                            return future.get(15, TimeUnit.SECONDS);
                        } catch (Exception exception) {
                            throw new IllegalStateException(exception);
                        }
                    })
                    .toList();

            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            1,
                            results.stream().filter(RentalBookingResponse.class::isInstance).count()
                    ),
                    () -> Assertions.assertEquals(
                            1,
                            results.stream().filter(RentalDatesNotAvailableException.class::isInstance).count()
                    ),
                    () -> Assertions.assertEquals(1, bookingRepository.count()),
                    () -> Assertions.assertEquals(
                            1,
                            occupancyService.adminOccupancies(
                                    property.id(),
                                    checkIn,
                                    checkOut
                            ).size()
                    )
            );
        }
    }

    @Test
    void sameBooking_twoAdminCancellationsConcurrently_exactlyOneCommits() throws Exception {
        RentalPropertyResponse property = RentalTestFixtures.publishedProperty(
                propertyService,
                propertyMediaService,
                "concurrent-cancellation",
                new BigDecimal("100.00")
        );
        CurrentCustomer customer = RentalTestFixtures.customer(
                customerAccountService,
                "920003"
        );
        LocalDate checkIn = stayPolicy.today().plusDays(5);
        RentalBookingResponse booking = bookingService.create(
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
        var ready = new CountDownLatch(2);
        var start = new CountDownLatch(1);

        try (var executor = Executors.newFixedThreadPool(2)) {
            var firstCancellation = executor.submit(() -> cancel(
                    booking.id(),
                    "first",
                    ready,
                    start
            ));
            var secondCancellation = executor.submit(() -> cancel(
                    booking.id(),
                    "second",
                    ready,
                    start
            ));
            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();

            Assertions.assertEquals(
                    1,
                    List.of(firstCancellation.get(), secondCancellation.get()).stream()
                            .filter(Boolean::booleanValue)
                            .count()
            );
        }

        RentalBooking persisted = bookingRepository.findById(booking.id()).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        RentalBookingStatus.CANCELLED_BY_ADMIN,
                        persisted.getStatus()
                ),
                () -> Assertions.assertTrue(
                        persisted.getCancellationReason().equals("first")
                                || persisted.getCancellationReason().equals("second")
                ),
                () -> Assertions.assertEquals(1L, persisted.getVersion())
        );
    }

    private Callable<Object> attempt(
            CountDownLatch start,
            CurrentCustomer customer,
            long propertyId,
            LocalDate checkIn,
            LocalDate checkOut
    ) {
        return () -> {
            start.await(5, TimeUnit.SECONDS);
            try {
                return bookingService.create(
                        customer,
                        new CreateRentalBookingRequest(
                                propertyId,
                                RentalTermType.DATE_RANGE,
                                checkIn,
                                checkOut,
                                null,
                                2,
                                "+90 555 123 45 67",
                                null
                        )
                );
            } catch (RentalDatesNotAvailableException exception) {
                return exception;
            }
        };
    }

    private boolean cancel(
            long bookingId,
            String reason,
            CountDownLatch ready,
            CountDownLatch start
    ) {
        try {
            new TransactionTemplate(transactionManager).executeWithoutResult(ignored -> {
                RentalBooking booking = bookingRepository.findById(bookingId).orElseThrow();
                ready.countDown();
                await(start);
                booking.cancelByAdmin(reason, Instant.now());
                bookingRepository.flush();
            });
            return true;
        } catch (OptimisticLockingFailureException exception) {
            return false;
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Concurrent transition did not start in time");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Concurrent transition was interrupted", exception);
        }
    }
}
