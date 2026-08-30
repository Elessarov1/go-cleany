package com.cleany.transfer;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.cleany.admin.AdminAccessService;
import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;

@TestPropertySource(properties = "transfer.assignment-mode=DRIVER_SELF_ACCEPT")
class TransferAssignmentConcurrencyIntegrationTest extends BaseIntegrationTest {

    @Autowired private TransferBookingService bookingService;
    @Autowired private TransferBookingPolicy bookingPolicy;
    @Autowired private TransferDriverAssignmentService assignmentService;
    @Autowired private AdminTransferService adminTransferService;
    @Autowired private TransferAirportRepository airportRepository;
    @Autowired private TransferVehicleTypeRepository vehicleRepository;
    @Autowired private TransferPriceRepository priceRepository;
    @Autowired private TransferBookingRepository bookingRepository;
    @Autowired private TransferDriverRepository driverRepository;
    @Autowired private CustomerAccountService customerAccountService;
    @Autowired private CustomerExternalIdentityRepository identityRepository;
    @Autowired private CustomerAccountRepository accountRepository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private Clock clock;

    @MockitoBean private AdminAccessService adminAccessService;

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
        jdbcTemplate.update("update platform_service_state set status = 'ENABLED', version = version + 1 where service = 'TRANSFER'");
        clearPlatformServiceStateCache();
        Mockito.when(adminAccessService.requireCurrentAdmin()).thenReturn(900001L);
    }

    @Test
    void twoTelegramDriversAcceptConcurrently_exactlyOneWins() throws Exception {
        TransferBookingResponse booking = booking();
        TransferDriver first = connectedDriver("First", 71001L);
        TransferDriver second = connectedDriver("Second", 71002L);

        List<Boolean> outcomes = race(
                () -> selfAccept(booking.id(), first.getVerifiedTelegramUserId()),
                () -> selfAccept(booking.id(), second.getVerifiedTelegramUserId())
        );

        TransferBooking stored = bookingRepository.findById(booking.id()).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, outcomes.stream().filter(Boolean::booleanValue).count()),
                () -> Assertions.assertEquals(TransferBookingStatus.CONFIRMED, stored.getStatus()),
                () -> Assertions.assertTrue(
                        stored.getDriver().getId().equals(first.getId())
                                || stored.getDriver().getId().equals(second.getId())
                )
        );
    }

    @Test
    void adminAndTelegramDriverAssignConcurrently_exactlyOneWins() throws Exception {
        TransferBookingResponse booking = booking();
        TransferDriver selfAccepting = connectedDriver("Self", 72001L);
        TransferDriver manual = driverRepository.saveAndFlush(new TransferDriver(
                "Manual", "+905557200002", true, null, clock.instant()
        ));

        List<Boolean> outcomes = race(
                () -> selfAccept(booking.id(), selfAccepting.getVerifiedTelegramUserId()),
                () -> adminAssign(booking.id(), manual.getId())
        );

        TransferBooking stored = bookingRepository.findById(booking.id()).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, outcomes.stream().filter(Boolean::booleanValue).count()),
                () -> Assertions.assertEquals(TransferBookingStatus.CONFIRMED, stored.getStatus()),
                () -> Assertions.assertTrue(
                        stored.getDriver().getId().equals(selfAccepting.getId())
                                || stored.getDriver().getId().equals(manual.getId())
                )
        );
    }

    private TransferBookingResponse booking() {
        TransferAirport airport = airportRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        TransferVehicleType vehicle = vehicleRepository.findAllByOrderBySortOrderAscIdAsc().getFirst();
        priceRepository.saveAndFlush(new TransferPrice(
                airport, vehicle, TransferDirection.TO_AIRPORT,
                new BigDecimal("1800.00"), "TRY", true, clock.instant()
        ));
        var customer = customerAccountService.resolveCustomer(new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.TELEGRAM,
                "transfer-race-customer",
                "transfer-race-customer",
                "Transfer race customer",
                "ru"
        ));
        return bookingService.create(customer, new CreateTransferBookingRequest(
                TransferDirection.TO_AIRPORT, airport.getId(), vehicle.getId(),
                bookingPolicy.earliestBookingDate(), LocalTime.of(10, 0), "Kestel, Alanya",
                2, 2, null, null, "+905551112233", null, null
        ));
    }

    private TransferDriver connectedDriver(String name, long telegramId) {
        TransferDriver driver = new TransferDriver(
                name, "+90555" + telegramId, true, telegramId, clock.instant()
        );
        driver.authorizeTelegram(telegramId, telegramId, clock.instant());
        return driverRepository.saveAndFlush(driver);
    }

    private boolean selfAccept(long bookingId, long telegramId) {
        try {
            assignmentService.selfAccept(bookingId, telegramId);
            return true;
        } catch (TransferAssignmentConflictException exception) {
            return false;
        }
    }

    private boolean adminAssign(long bookingId, long driverId) {
        try {
            adminTransferService.assign(bookingId, driverId);
            return true;
        } catch (TransferAssignmentConflictException exception) {
            return false;
        }
    }

    private List<Boolean> race(Task first, Task second) throws Exception {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            Future<Boolean> firstResult = executor.submit(() -> awaitAndRun(ready, start, first));
            Future<Boolean> secondResult = executor.submit(() -> awaitAndRun(ready, start, second));
            ready.await();
            start.countDown();
            return List.of(firstResult.get(), secondResult.get());
        }
    }

    private static boolean awaitAndRun(CountDownLatch ready, CountDownLatch start, Task task) throws Exception {
        ready.countDown();
        start.await();
        return task.run();
    }

    @FunctionalInterface
    private interface Task {
        boolean run();
    }
}
