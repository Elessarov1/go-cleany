package com.cleany.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.CustomerIdentityTestFixture;
import com.cleany.finance.AcquisitionSource;
import com.cleany.finance.CustomerDiscountType;
import com.cleany.finance.OrderFinancialSnapshot;

class CleaningOrderPhotoReportIntegrationTest extends BaseIntegrationTest {

    private static final long CLEANER_ID = 123456789L;
    private static final long OTHER_CLEANER_ID = 987654321L;

    @Autowired
    private CleaningOrderRepository orderRepository;

    @Autowired
    private CleaningOrderPhotoRepository photoRepository;

    @Autowired
    private CleaningOrderService orderService;

    @Autowired
    private CustomerAccountRepository customerAccountRepository;

    @Autowired
    private CustomerExternalIdentityRepository customerIdentityRepository;

    @BeforeEach
    void cleanDatabase() {
        photoRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void assignedCleaner_photoCommentAndCompletion_persistedThroughLiquibaseSchema() {
        CleaningOrder order = orderRepository.save(newOrder("Barbaros Cd. 24"));

        orderService.acceptOrder(order.getId(), CLEANER_ID);
        CleaningOrder awaitingReport = orderService.markAwaitingReport(order.getId(), CLEANER_ID);
        Assertions.assertTrue(awaitingReport.isReportInputActive());

        CleaningOrderReportProgress firstPhoto = orderService.addPhotoToActiveReport(
                CLEANER_ID,
                "telegram-file-1",
                "telegram-unique-1",
                null
        );
        CleaningOrderReportProgress duplicatePhoto = orderService.addPhotoToActiveReport(
                CLEANER_ID,
                "telegram-file-1-new-reference",
                "telegram-unique-1",
                null
        );
        CleaningOrderReportProgress comment = orderService.updateActiveReportComment(
                CLEANER_ID,
                "Everything is ready"
        );
        CleaningOrderReport report = orderService.getReportForDelivery(order.getId(), CLEANER_ID);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, firstPhoto.photoCount()),
                () -> Assertions.assertEquals(1L, duplicatePhoto.photoCount()),
                () -> Assertions.assertTrue(comment.commentPresent()),
                () -> Assertions.assertEquals(1, report.telegramFileIds().size()),
                () -> Assertions.assertEquals("telegram-file-1", report.telegramFileIds().getFirst())
        );

        orderService.completeOrder(order.getId(), CLEANER_ID, report.order().getCleanerComment());

        CleaningOrder completed = orderRepository.findById(order.getId()).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertEquals(CleaningOrderStatus.COMPLETED, completed.getStatus()),
                () -> Assertions.assertEquals("Everything is ready", completed.getCleanerComment()),
                () -> Assertions.assertNotNull(completed.getCompletedAt()),
                () -> Assertions.assertFalse(completed.isReportInputActive()),
                () -> Assertions.assertEquals(1L, photoRepository.countByOrderId(order.getId()))
        );
    }

    @Test
    void cleanerSelectsAnotherOrder_onlyLatestReportInputRemainsActive() {
        CleaningOrder first = orderRepository.save(newOrder("First address"));
        CleaningOrder second = orderRepository.save(newOrder("Second address"));
        orderService.acceptOrder(first.getId(), CLEANER_ID);
        orderService.acceptOrder(second.getId(), CLEANER_ID);

        orderService.markAwaitingReport(first.getId(), CLEANER_ID);
        orderService.markAwaitingReport(second.getId(), CLEANER_ID);

        CleaningOrder reloadedFirst = orderRepository.findById(first.getId()).orElseThrow();
        CleaningOrder reloadedSecond = orderRepository.findById(second.getId()).orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertFalse(reloadedFirst.isReportInputActive()),
                () -> Assertions.assertTrue(reloadedSecond.isReportInputActive()),
                () -> Assertions.assertEquals(
                        second.getId(),
                        orderRepository.findByCleanerTelegramUserIdAndReportInputActiveTrue(CLEANER_ID)
                                .orElseThrow()
                                .getId()
                )
        );
    }

    @Test
    void photoReport_otherConfiguredCleaner_accessRejected() {
        CleaningOrder order = orderRepository.save(newOrder("Barbaros Cd. 24"));
        orderService.acceptOrder(order.getId(), CLEANER_ID);
        orderService.markAwaitingReport(order.getId(), CLEANER_ID);

        Assertions.assertThrows(
                CleanerNotAuthorizedException.class,
                () -> orderService.getReportForDelivery(order.getId(), OTHER_CLEANER_ID)
        );
    }

    private CleaningOrder newOrder(String address) {
        var customer = CustomerIdentityTestFixture.telegramIdentity(
                customerAccountRepository,
                customerIdentityRepository,
                Instant.now()
        );
        BigDecimal basePrice = BigDecimal.valueOf(1100);
        BigDecimal commission = basePrice.multiply(new BigDecimal("0.15")).setScale(2);
        return new CleaningOrder(
                customer.customerId(),
                customer.externalIdentityId(),
                "Alex",
                "+90 555 123 45 67",
                ServiceArea.MAHMUTLAR,
                address,
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                new OrderFinancialSnapshot(
                        basePrice.setScale(2), new BigDecimal("0.15"), commission,
                        BigDecimal.ZERO.setScale(2), BigDecimal.ZERO.setScale(2), basePrice.setScale(2), commission,
                        AcquisitionSource.ORGANIC, CustomerDiscountType.NONE
                ),
                null,
                null,
                null,
                null,
                "TRY",
                LocalDate.now().plusDays(1),
                null,
                Instant.now()
        );
    }
}
