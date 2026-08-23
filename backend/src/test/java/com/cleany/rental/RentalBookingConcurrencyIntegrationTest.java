package com.cleany.rental;

import java.math.BigDecimal;
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
import org.springframework.jdbc.core.JdbcTemplate;

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
                                checkIn,
                                checkOut,
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
}
