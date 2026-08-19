package com.cleany.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccount;
import com.cleany.customer.CustomerAccountRepository;
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

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    void newOrder_twoCleanersClaimConcurrently_exactlyOneCleanerWins() throws Exception {
        long customerId = customerAccountRepository.save(new CustomerAccount(Instant.now())).getId();
        CleaningOrder order = orderRepository.save(new CleaningOrder(
                customerId,
                900001L,
                "browser_preview",
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
}
