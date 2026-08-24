package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderEventRepository;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderService;
import com.cleany.order.CleaningType;
import com.cleany.order.CreateCleaningOrderCommand;
import com.cleany.order.ServiceArea;

class CrossServiceCustomerIdentityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CustomerAccountService customerAccountService;

    @Autowired
    private CleaningOrderService cleaningOrderService;

    @Autowired
    private RentalBookingService rentalBookingService;

    @Autowired
    private RentalPropertyService rentalPropertyService;

    @Autowired
    private RentalPropertyMediaService rentalPropertyMediaService;

    @Autowired
    private RentalStayPolicy rentalStayPolicy;

    @Autowired
    private CleaningOrderEventRepository cleaningOrderEventRepository;

    @Autowired
    private CleaningOrderRepository cleaningOrderRepository;

    @Autowired
    private RentalBookingRepository rentalBookingRepository;

    @Autowired
    private RentalPropertyMediaRepository rentalPropertyMediaRepository;

    @Autowired
    private RentalPropertyRepository rentalPropertyRepository;

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
        rentalBookingRepository.deleteAll();
        rentalPropertyMediaRepository.deleteAll();
        rentalPropertyRepository.deleteAll();
        cleaningOrderEventRepository.deleteAll();
        cleaningOrderRepository.deleteAll();
        providerReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
        identityRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void sameTelegramIdentity_cleaningAndRentalSharePlatformCustomer() {
        CurrentCustomer cleaningCustomer = RentalTestFixtures.customer(
                customerAccountService,
                "930001"
        );
        CurrentCustomer rentalCustomer = RentalTestFixtures.customer(
                customerAccountService,
                "930001"
        );
        CurrentCustomer anotherCustomer = RentalTestFixtures.customer(
                customerAccountService,
                "930002"
        );
        LocalDate cleaningDate = rentalStayPolicy.today().plusDays(1);
        CleaningOrder cleaningOrder = cleaningOrderService.createOrder(
                cleaningCustomer,
                new CreateCleaningOrderCommand(
                        ServiceArea.MAHMUTLAR,
                        "Barbaros Cd. 24",
                        ApartmentType.TWO_PLUS_ONE,
                        false,
                        CleaningType.REGULAR,
                        cleaningDate,
                        "+90 555 123 45 67",
                        null,
                        null
                )
        );
        RentalPropertyResponse property = RentalTestFixtures.publishedProperty(
                rentalPropertyService,
                rentalPropertyMediaService,
                "shared-customer",
                new BigDecimal("100.00")
        );
        LocalDate checkIn = rentalStayPolicy.today().plusDays(5);
        RentalBookingResponse rentalBooking = rentalBookingService.create(
                rentalCustomer,
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
        RentalBooking persistedRentalBooking = rentalBookingRepository
                .findById(rentalBooking.id())
                .orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        cleaningCustomer.customerId(),
                        rentalCustomer.customerId()
                ),
                () -> Assertions.assertEquals(
                        cleaningOrder.getCustomerId(),
                        persistedRentalBooking.getCustomerId()
                ),
                () -> Assertions.assertNotEquals(
                        cleaningCustomer.customerId(),
                        anotherCustomer.customerId()
                ),
                () -> Assertions.assertEquals(2L, accountRepository.count())
        );
    }
}
