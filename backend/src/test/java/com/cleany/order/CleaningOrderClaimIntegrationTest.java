package com.cleany.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.CustomerIdentityTestFixture;
import com.cleany.finance.AcquisitionSource;
import com.cleany.finance.CustomerDiscountType;
import com.cleany.finance.OrderFinancialSnapshot;

class CleaningOrderClaimIntegrationTest extends BaseIntegrationTest {

    private static final long FIRST_CLEANER_ID = 123456789L;
    private static final long SECOND_CLEANER_ID = 987654321L;

    @Autowired
    private CleaningOrderRepository orderRepository;

    @Autowired
    private CleaningOrderService orderService;

    @Autowired
    private CustomerAccountRepository customerAccountRepository;

    @Autowired
    private CustomerExternalIdentityRepository customerIdentityRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    void newOrder_twoCleanersClaimConcurrently_exactlyOneCleanerWins() throws Exception {
        var customer = CustomerIdentityTestFixture.telegramIdentity(
                customerAccountRepository,
                customerIdentityRepository,
                Instant.now()
        );
        CleaningOrder order = orderRepository.save(new CleaningOrder(
                customer.customerId(),
                customer.externalIdentityId(),
                "Alex",
                "+90 555 123 45 67",
                ServiceArea.MAHMUTLAR,
                "Barbaros Cd. 24",
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                organicSnapshot(BigDecimal.valueOf(1100)),
                null,
                null,
                null,
                null,
                "TRY",
                LocalDate.now().plusDays(1),
                null,
                Instant.now()
        ));

        var ready = new CountDownLatch(2);
        var start = new CountDownLatch(1);

        try (var executor = Executors.newFixedThreadPool(2)) {
            var firstClaim = executor.submit(() -> claim(order.getId(), FIRST_CLEANER_ID, ready, start));
            var secondClaim = executor.submit(() -> claim(order.getId(), SECOND_CLEANER_ID, ready, start));

            ready.await();
            start.countDown();

            long successfulClaims = List.of(firstClaim.get(), secondClaim.get()).stream()
                    .filter(Boolean::booleanValue)
                    .count();

            Assertions.assertEquals(1, successfulClaims);
        }

        CleaningOrder acceptedOrder = orderRepository.findById(order.getId()).orElseThrow();
        Assertions.assertEquals(CleaningOrderStatus.ACCEPTED, acceptedOrder.getStatus());
        Assertions.assertTrue(
                acceptedOrder.getCleanerTelegramUserId() == FIRST_CLEANER_ID
                        || acceptedOrder.getCleanerTelegramUserId() == SECOND_CLEANER_ID
        );
        Assertions.assertNotNull(acceptedOrder.getAcceptedAt());
        Assertions.assertEquals(1L, acceptedOrder.getVersion());
    }

    @Test
    void sameOrder_twoTerminalTransitionsConcurrently_exactlyOneCommits() throws Exception {
        var customer = CustomerIdentityTestFixture.telegramIdentity(
                customerAccountRepository,
                customerIdentityRepository,
                Instant.now()
        );
        CleaningOrder order = orderRepository.save(new CleaningOrder(
                customer.customerId(),
                customer.externalIdentityId(),
                "Alex",
                "+90 555 123 45 67",
                ServiceArea.MAHMUTLAR,
                "Barbaros Cd. 24",
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                organicSnapshot(BigDecimal.valueOf(1100)),
                null,
                null,
                null,
                null,
                "TRY",
                LocalDate.now().plusDays(1),
                null,
                Instant.now()
        ));
        var ready = new CountDownLatch(2);
        var start = new CountDownLatch(1);

        try (var executor = Executors.newFixedThreadPool(2)) {
            var cancellation = executor.submit(() -> terminalTransition(
                    order.getId(),
                    false,
                    ready,
                    start
            ));
            var rejection = executor.submit(() -> terminalTransition(
                    order.getId(),
                    true,
                    ready,
                    start
            ));
            Assertions.assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();

            Assertions.assertEquals(
                    1,
                    List.of(cancellation.get(), rejection.get()).stream()
                            .filter(Boolean::booleanValue)
                            .count()
            );
        }

        CleaningOrder persisted = orderRepository.findById(order.getId()).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertTrue(
                        persisted.getStatus() == CleaningOrderStatus.CANCELLED
                                || persisted.getStatus() == CleaningOrderStatus.REJECTED
                ),
                () -> Assertions.assertEquals(1L, persisted.getVersion())
        );
    }

    private static OrderFinancialSnapshot organicSnapshot(BigDecimal basePrice) {
        BigDecimal commission = basePrice.multiply(new BigDecimal("0.15")).setScale(2);
        return new OrderFinancialSnapshot(
                basePrice.setScale(2), new BigDecimal("0.15"), commission,
                BigDecimal.ZERO.setScale(2), BigDecimal.ZERO.setScale(2), basePrice.setScale(2), commission,
                AcquisitionSource.ORGANIC, CustomerDiscountType.NONE
        );
    }

    private boolean claim(
            long orderId,
            long cleanerId,
            CountDownLatch ready,
            CountDownLatch start
    ) throws InterruptedException {
        ready.countDown();
        start.await();
        try {
            orderService.acceptOrder(orderId, cleanerId);
            return true;
        } catch (OrderClaimConflictException exception) {
            return false;
        }
    }

    private boolean terminalTransition(
            long orderId,
            boolean reject,
            CountDownLatch ready,
            CountDownLatch start
    ) {
        try {
            new TransactionTemplate(transactionManager).executeWithoutResult(ignored -> {
                CleaningOrder order = orderRepository.findById(orderId).orElseThrow();
                ready.countDown();
                await(start);
                if (reject) {
                    order.reject();
                } else {
                    order.cancelByCustomer();
                }
                orderRepository.flush();
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
