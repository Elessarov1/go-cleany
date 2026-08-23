package com.cleany.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.CustomerIdentityTestFixture;
import com.cleany.finance.AcquisitionSource;
import com.cleany.finance.CustomerDiscountType;
import com.cleany.finance.OrderFinancialSnapshot;

class CleaningOrderRepositoryIntegrationTest extends BaseIntegrationTest {

    private static final long CLEANER_ID = 123456789L;
    private static final Instant TODAY_START = Instant.parse("2026-08-23T21:00:00Z");
    private static final Instant TOMORROW_START = Instant.parse("2026-08-24T21:00:00Z");

    @Autowired
    private CleaningOrderRepository orderRepository;

    @Autowired
    private CustomerAccountRepository customerAccountRepository;

    @Autowired
    private CustomerExternalIdentityRepository customerIdentityRepository;

    @Test
    @Transactional
    void calculateStatistics_aggregatesStatusesDayBoundaryAndCompletedAmountInDatabase() {
        var customer = CustomerIdentityTestFixture.telegramIdentity(
                customerAccountRepository,
                customerIdentityRepository,
                TODAY_START
        );
        CleaningOrder newOrder = save(customer, TODAY_START.plusSeconds(1), "100.00");
        CleaningOrder accepted = save(customer, TODAY_START.plusSeconds(2), "200.00");
        CleaningOrder awaitingReport = save(customer, TODAY_START.minusSeconds(1), "300.00");
        CleaningOrder completed = save(customer, TODAY_START.minusSeconds(2), "700.00");
        CleaningOrder cancelled = save(customer, TOMORROW_START, "500.00");
        CleaningOrder rejected = save(customer, TOMORROW_START.minusSeconds(1), "600.00");

        claim(accepted);
        claim(awaitingReport);
        claim(completed);

        awaitingReport = orderRepository.findById(awaitingReport.getId()).orElseThrow();
        awaitingReport.startReportCollection(CLEANER_ID);
        orderRepository.saveAndFlush(awaitingReport);

        completed = orderRepository.findById(completed.getId()).orElseThrow();
        completed.startReportCollection(CLEANER_ID);
        completed.complete(CLEANER_ID, null, TODAY_START);
        orderRepository.saveAndFlush(completed);

        cancelled = orderRepository.findById(cancelled.getId()).orElseThrow();
        cancelled.cancelByCustomer();
        orderRepository.saveAndFlush(cancelled);

        rejected = orderRepository.findById(rejected.getId()).orElseThrow();
        rejected.reject();
        orderRepository.saveAndFlush(rejected);

        CleaningOrderStatistics statistics = orderRepository.calculateStatistics(
                TODAY_START,
                TOMORROW_START
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(6L, statistics.getTotalOrders()),
                () -> Assertions.assertEquals(3L, statistics.getOrdersToday()),
                () -> Assertions.assertEquals(1L, statistics.getNewOrders()),
                () -> Assertions.assertEquals(2L, statistics.getActiveOrders()),
                () -> Assertions.assertEquals(1L, statistics.getCompletedOrders()),
                () -> Assertions.assertEquals(1L, statistics.getCancelledOrders()),
                () -> Assertions.assertEquals(
                        0,
                        new BigDecimal("700.00").compareTo(statistics.getCompletedAmount())
                )
        );

        Assertions.assertEquals(CleaningOrderStatus.NEW, newOrder.getStatus());
    }

    private CleaningOrder save(
            CustomerIdentityTestFixture.PersistedCustomerIdentity customer,
            Instant createdAt,
            String price
    ) {
        return orderRepository.saveAndFlush(new CleaningOrder(
                customer.customerId(),
                customer.externalIdentityId(),
                "Alex",
                "+90 555 123 45 67",
                ServiceArea.MAHMUTLAR,
                "Barbaros Cd. 24",
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                organicSnapshot(new BigDecimal(price)),
                null,
                null,
                null,
                null,
                "TRY",
                LocalDate.of(2026, 8, 25),
                null,
                createdAt
        ));
    }

    private void claim(CleaningOrder order) {
        Assertions.assertEquals(
                1,
                orderRepository.claimNewOrder(
                        order.getId(),
                        CLEANER_ID,
                        TODAY_START,
                        CleaningOrderStatus.NEW,
                        CleaningOrderStatus.ACCEPTED
                )
        );
    }

    private static OrderFinancialSnapshot organicSnapshot(BigDecimal basePrice) {
        BigDecimal normalizedPrice = basePrice.setScale(2);
        BigDecimal commission = normalizedPrice.multiply(new BigDecimal("0.15")).setScale(2);
        return new OrderFinancialSnapshot(
                normalizedPrice,
                new BigDecimal("0.15"),
                commission,
                BigDecimal.ZERO.setScale(2),
                BigDecimal.ZERO.setScale(2),
                normalizedPrice,
                commission,
                AcquisitionSource.ORGANIC,
                CustomerDiscountType.NONE
        );
    }
}
