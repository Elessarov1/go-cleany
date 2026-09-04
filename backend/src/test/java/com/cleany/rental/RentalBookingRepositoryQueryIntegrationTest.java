package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProviderReferenceRepository;

@TestPropertySource(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class RentalBookingRepositoryQueryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RentalBookingService bookingService;

    @Autowired
    private RentalPropertyService propertyService;

    @Autowired
    private RentalPropertyMediaService mediaService;

    @Autowired
    private RentalStayPolicy stayPolicy;

    @Autowired
    private RentalBookingRepository bookingRepository;

    @Autowired
    private RentalPropertyMediaRepository mediaRepository;

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
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.update("delete from rental_occupancy");
        bookingRepository.deleteAll();
        mediaRepository.deleteAll();
        propertyRepository.deleteAll();
        providerReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void customerAndAdminReadsFetchPropertiesWithOneStatement() {
        CurrentCustomer customer = RentalTestFixtures.customer(customerAccountService, "915001");
        long firstBookingId = createBookings(customer);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        statementsFor(() -> bookingRepository
                                .findAllByCustomerIdOrderByCreatedAtDesc(customer.customerId())
                                .forEach(booking -> booking.getProperty().getSlug()))
                ),
                () -> Assertions.assertEquals(
                        1,
                        statementsFor(() -> bookingRepository
                                .findByIdAndCustomerId(firstBookingId, customer.customerId())
                                .orElseThrow()
                                .getProperty()
                                .getSlug())
                ),
                () -> Assertions.assertEquals(
                        1,
                        statementsFor(() -> bookingRepository
                                .findAllByOrderByCreatedAtDesc()
                                .forEach(booking -> booking.getProperty().getSlug()))
                )
        );
    }

    private long createBookings(CurrentCustomer customer) {
        LocalDate checkIn = stayPolicy.today().plusDays(10);
        long firstBookingId = 0;
        for (int index = 0; index < 3; index++) {
            RentalPropertyResponse property = RentalTestFixtures.publishedProperty(
                    propertyService,
                    mediaService,
                    "query-plan-" + index,
                    new BigDecimal("100.00")
            );
            RentalBookingResponse booking = bookingService.create(
                    customer,
                    new CreateRentalBookingRequest(
                            property.id(),
                            RentalTermType.DATE_RANGE,
                            checkIn.plusDays(index),
                            checkIn.plusDays(index + 7L),
                            null,
                            2,
                            "+90 555 123 45 67",
                            null
                    )
            );
            if (index == 0) {
                firstBookingId = booking.id();
            }
        }
        return firstBookingId;
    }

    private long statementsFor(Runnable query) {
        var statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        transactionTemplate.executeWithoutResult(status -> {
            entityManager.clear();
            statistics.clear();
            query.run();
        });
        return statistics.getPrepareStatementCount();
    }
}
