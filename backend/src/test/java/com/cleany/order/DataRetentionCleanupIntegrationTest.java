package com.cleany.order;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import com.cleany.base.BaseIntegrationTest;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.CustomerIdentityTestFixture;
import com.cleany.finance.AcquisitionSource;
import com.cleany.finance.CustomerDiscountType;
import com.cleany.finance.OrderFinancialSnapshot;
import com.cleany.media.MediaAssetRepository;
import com.cleany.media.MediaProvider;
import com.cleany.media.MediaProviderReferenceRepository;
import com.cleany.media.MediaProviderReferenceService;
import com.cleany.media.MediaUpload;
import com.cleany.retention.DataRetentionCleanupResult;
import com.cleany.retention.DataRetentionCleanupService;

class DataRetentionCleanupIntegrationTest extends BaseIntegrationTest {

    private static final long CLEANER_ID = 123456789L;
    private static final long ADMIN_ID = 900001L;
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xD9};

    @Autowired
    private CleaningOrderRepository orderRepository;

    @Autowired
    private CleaningOrderEventRepository eventRepository;

    @Autowired
    private CleaningOrderPhotoRepository completionPhotoRepository;

    @Autowired
    private CleaningOrderIssueReportRepository issueReportRepository;

    @Autowired
    private CleaningOrderIssuePhotoRepository issuePhotoRepository;

    @Autowired
    private CustomerAccountRepository customerAccountRepository;

    @Autowired
    private CustomerExternalIdentityRepository customerIdentityRepository;

    @Autowired
    private MediaAssetRepository mediaAssetRepository;

    @Autowired
    private MediaProviderReferenceRepository mediaProviderReferenceRepository;

    @Autowired
    private MediaProviderReferenceService mediaProviderReferenceService;

    @Autowired
    private DataRetentionCleanupService cleanupService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        issuePhotoRepository.deleteAll();
        issueReportRepository.deleteAll();
        completionPhotoRepository.deleteAll();
        eventRepository.deleteAll();
        orderRepository.deleteAll();
        customerAccountRepository.deleteAll();
        mediaProviderReferenceRepository.deleteAll();
        mediaAssetRepository.deleteAll();
    }

    @Test
    void cleanup_removesOnlyOldTerminalPayloadAndKeepsIssueMetadata() {
        Instant cutoff = Instant.parse("2026-08-14T12:00:00Z");
        Instant old = cutoff.minus(Duration.ofHours(1));
        Instant recent = cutoff.plus(Duration.ofHours(1));

        ActiveIssue activeIssue = createActiveIssue(old);
        long recentCompletedOrderId = createCompletedOrder(recent);
        long recentCancelledOrderId = createCancelledOrder(recent);
        long oldCompletedOrderId = createCompletedOrder(old);
        long oldCancelledOrderId = createCancelledOrder(old);
        ResolvedIssue resolvedIssue = createResolvedIssue(old);

        DataRetentionCleanupResult firstBatch = cleanupService.cleanupBatch(cutoff, 2);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, firstBatch.eligibleOrderCount()),
                () -> Assertions.assertEquals(0, firstBatch.deletedIssuePhotoCount()),
                () -> Assertions.assertEquals(2, firstBatch.deletedCompletionPhotoCount()),
                () -> Assertions.assertEquals(2, firstBatch.deletedAuditEventCount()),
                () -> Assertions.assertEquals(2, firstBatch.deletedMediaAssetCount()),
                () -> Assertions.assertTrue(firstBatch.hasMoreWork()),
                () -> Assertions.assertEquals(1L,
                        issuePhotoRepository.countByIssueReport_Id(resolvedIssue.reportId()))
        );

        DataRetentionCleanupResult secondBatch = cleanupService.cleanupBatch(cutoff, 2);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, secondBatch.eligibleOrderCount()),
                () -> Assertions.assertEquals(1, secondBatch.deletedIssuePhotoCount()),
                () -> Assertions.assertEquals(0, secondBatch.deletedCompletionPhotoCount()),
                () -> Assertions.assertEquals(1, secondBatch.deletedAuditEventCount()),
                () -> Assertions.assertEquals(1, secondBatch.deletedMediaAssetCount()),
                () -> Assertions.assertFalse(secondBatch.hasMoreWork()),
                () -> Assertions.assertEquals(3L, mediaAssetRepository.count()),
                () -> Assertions.assertEquals(3L, mediaProviderReferenceRepository.count()),
                () -> Assertions.assertEquals(1L,
                        issuePhotoRepository.countByIssueReport_Id(activeIssue.reportId())),
                () -> Assertions.assertEquals(1,
                        eventRepository.findAllByOrderIdOrderByOccurredAtAscIdAsc(activeIssue.orderId()).size()),
                () -> Assertions.assertEquals(1L,
                        completionPhotoRepository.countByOrderId(recentCompletedOrderId)),
                () -> Assertions.assertEquals(1,
                        eventRepository.findAllByOrderIdOrderByOccurredAtAscIdAsc(recentCompletedOrderId).size()),
                () -> Assertions.assertEquals(1L,
                        completionPhotoRepository.countByOrderId(recentCancelledOrderId)),
                () -> Assertions.assertEquals(1,
                        eventRepository.findAllByOrderIdOrderByOccurredAtAscIdAsc(recentCancelledOrderId).size()),
                () -> Assertions.assertEquals(0L,
                        completionPhotoRepository.countByOrderId(oldCompletedOrderId)),
                () -> Assertions.assertTrue(
                        eventRepository.findAllByOrderIdOrderByOccurredAtAscIdAsc(oldCompletedOrderId).isEmpty()),
                () -> Assertions.assertEquals(0L,
                        completionPhotoRepository.countByOrderId(oldCancelledOrderId)),
                () -> Assertions.assertTrue(
                        eventRepository.findAllByOrderIdOrderByOccurredAtAscIdAsc(oldCancelledOrderId).isEmpty()),
                () -> Assertions.assertEquals(0L,
                        issuePhotoRepository.countByIssueReport_Id(resolvedIssue.reportId())),
                () -> Assertions.assertTrue(issueReportRepository.findById(resolvedIssue.reportId()).isPresent()),
                () -> Assertions.assertTrue(orderRepository.findById(resolvedIssue.orderId()).isPresent())
        );

        CleaningOrderIssueReport retainedReport = issueReportRepository
                .findById(resolvedIssue.reportId())
                .orElseThrow();
        Assertions.assertAll(
                () -> Assertions.assertEquals(OnsiteIssueReason.OTHER, retainedReport.getReason()),
                () -> Assertions.assertEquals("Resolved incident", retainedReport.getResolutionComment()),
                () -> Assertions.assertNotNull(retainedReport.getResolvedAt())
        );

        DataRetentionCleanupResult emptyBatch = cleanupService.cleanupBatch(cutoff, 2);
        Assertions.assertAll(
                () -> Assertions.assertEquals(0, emptyBatch.eligibleOrderCount()),
                () -> Assertions.assertEquals(0, emptyBatch.deletedIssuePhotoCount()),
                () -> Assertions.assertEquals(0, emptyBatch.deletedCompletionPhotoCount()),
                () -> Assertions.assertEquals(0, emptyBatch.deletedAuditEventCount()),
                () -> Assertions.assertEquals(0, emptyBatch.deletedMediaAssetCount()),
                () -> Assertions.assertFalse(emptyBatch.hasMoreWork())
        );
    }

    private ActiveIssue createActiveIssue(Instant createdAt) {
        return transactionTemplate.execute(status -> {
            CleaningOrder order = acceptedOrder(createdAt.minus(Duration.ofHours(2)));
            order.reportOnsiteIssue(CLEANER_ID);
            orderRepository.save(order);

            CleaningOrderIssueReport report = new CleaningOrderIssueReport(
                    order,
                    CLEANER_ID,
                    OnsiteIssueReason.ACCESS_PROBLEM,
                    createdAt.minus(Duration.ofHours(1))
            );
            report.updateComment("Access is unavailable");
            report.submit(createdAt);
            report = issueReportRepository.save(report);
            issuePhotoRepository.save(issuePhoto(
                    report,
                    "active-issue-file",
                    "active-issue-unique",
                    createdAt
            ));
            eventRepository.save(new CleaningOrderEvent(
                    order,
                    OrderEventType.ONSITE_ISSUE_REPORTED,
                    CleaningOrderStatus.ACCEPTED,
                    CleaningOrderStatus.ONSITE_ISSUE_REPORTED,
                    OrderActorType.CLEANER,
                    CLEANER_ID,
                    null,
                    createdAt
            ));
            return new ActiveIssue(order.getId(), report.getId());
        });
    }

    private long createCompletedOrder(Instant completedAt) {
        return transactionTemplate.execute(status -> {
            CleaningOrder order = acceptedOrder(completedAt.minus(Duration.ofDays(1)));
            order.startReportCollection(CLEANER_ID);
            order.complete(CLEANER_ID, "Completed", completedAt);
            orderRepository.save(order);
            completionPhotoRepository.save(completionPhoto(order, completedAt));
            eventRepository.save(new CleaningOrderEvent(
                    order,
                    OrderEventType.COMPLETED,
                    CleaningOrderStatus.AWAITING_REPORT,
                    CleaningOrderStatus.COMPLETED,
                    OrderActorType.CLEANER,
                    CLEANER_ID,
                    null,
                    completedAt
            ));
            return order.getId();
        });
    }

    private long createCancelledOrder(Instant cancelledAt) {
        return transactionTemplate.execute(status -> {
            CleaningOrder order = acceptedOrder(cancelledAt.minus(Duration.ofDays(1)));
            order.startReportCollection(CLEANER_ID);
            completionPhotoRepository.save(completionPhoto(order, cancelledAt));
            order.cancelByCleaner(CLEANER_ID);
            orderRepository.save(order);
            eventRepository.save(new CleaningOrderEvent(
                    order,
                    OrderEventType.CANCELLED_BY_CLEANER,
                    CleaningOrderStatus.AWAITING_REPORT,
                    CleaningOrderStatus.CANCELLED,
                    OrderActorType.CLEANER,
                    CLEANER_ID,
                    null,
                    cancelledAt
            ));
            return order.getId();
        });
    }

    private ResolvedIssue createResolvedIssue(Instant resolvedAt) {
        return transactionTemplate.execute(status -> {
            CleaningOrder order = acceptedOrder(resolvedAt.minus(Duration.ofDays(1)));
            order.reportOnsiteIssue(CLEANER_ID);
            orderRepository.save(order);

            CleaningOrderIssueReport report = new CleaningOrderIssueReport(
                    order,
                    CLEANER_ID,
                    OnsiteIssueReason.OTHER,
                    resolvedAt.minus(Duration.ofHours(2))
            );
            report.updateComment("Issue evidence");
            report.submit(resolvedAt.minus(Duration.ofHours(1)));
            report.resolve(ADMIN_ID, "Resolved incident", resolvedAt);
            report = issueReportRepository.save(report);
            issuePhotoRepository.save(issuePhoto(
                    report,
                    "issue-file",
                    "issue-unique",
                    resolvedAt.minus(Duration.ofHours(1))
            ));

            order.resolveOnsiteIssue();
            orderRepository.save(order);
            eventRepository.save(new CleaningOrderEvent(
                    order,
                    OrderEventType.ISSUE_RESOLVED,
                    CleaningOrderStatus.ONSITE_ISSUE_REPORTED,
                    CleaningOrderStatus.CANCELLED,
                    OrderActorType.ADMIN,
                    ADMIN_ID,
                    "Resolved incident",
                    resolvedAt
            ));
            return new ResolvedIssue(order.getId(), report.getId());
        });
    }

    private CleaningOrder acceptedOrder(Instant createdAt) {
        CleaningOrder order = orderRepository.save(newOrder(createdAt));
        int updated = orderRepository.claimNewOrder(
                order.getId(),
                CLEANER_ID,
                createdAt.plus(Duration.ofHours(1)),
                CleaningOrderStatus.NEW,
                CleaningOrderStatus.ACCEPTED
        );
        if (updated != 1) {
            throw new IllegalStateException("Test order could not be accepted");
        }
        return orderRepository.findById(order.getId()).orElseThrow();
    }

    private CleaningOrderPhoto completionPhoto(CleaningOrder order, Instant createdAt) {
        var providerMedia = mediaProviderReferenceService.resolveOrStore(
                new MediaUpload(JPEG, "image/jpeg"),
                MediaProvider.TELEGRAM,
                "completion-file-" + order.getId(),
                "completion-unique-" + order.getId()
        );
        return new CleaningOrderPhoto(
                order,
                providerMedia.media().mediaId(),
                createdAt
        );
    }

    private CleaningOrderIssuePhoto issuePhoto(
            CleaningOrderIssueReport report,
            String telegramFileId,
            String telegramFileUniqueId,
            Instant createdAt
    ) {
        var providerMedia = mediaProviderReferenceService.resolveOrStore(
                new MediaUpload(JPEG, "image/jpeg"),
                MediaProvider.TELEGRAM,
                telegramFileId,
                telegramFileUniqueId
        );
        return new CleaningOrderIssuePhoto(
                report,
                providerMedia.media().mediaId(),
                createdAt
        );
    }

    private CleaningOrder newOrder(Instant createdAt) {
        var customer = CustomerIdentityTestFixture.telegramIdentity(
                customerAccountRepository,
                customerIdentityRepository,
                createdAt
        );
        BigDecimal basePrice = BigDecimal.valueOf(1100);
        BigDecimal commission = basePrice.multiply(new BigDecimal("0.15")).setScale(2);
        return new CleaningOrder(
                customer.customerId(),
                customer.externalIdentityId(),
                "Customer " + customer.customerId(),
                "+90 555 123 45 67",
                ServiceArea.MAHMUTLAR,
                "Barbaros Cd. 24",
                ApartmentType.TWO_PLUS_ONE,
                false,
                CleaningType.REGULAR,
                new OrderFinancialSnapshot(
                        basePrice.setScale(2),
                        new BigDecimal("0.15"),
                        commission,
                        BigDecimal.ZERO.setScale(2),
                        BigDecimal.ZERO.setScale(2),
                        basePrice.setScale(2),
                        commission,
                        AcquisitionSource.ORGANIC,
                        CustomerDiscountType.NONE
                ),
                null,
                null,
                null,
                null,
                "TRY",
                LocalDate.of(2026, 8, 22),
                null,
                createdAt
        );
    }

    private record ResolvedIssue(long orderId, long reportId) {
    }

    private record ActiveIssue(long orderId, long reportId) {
    }
}
