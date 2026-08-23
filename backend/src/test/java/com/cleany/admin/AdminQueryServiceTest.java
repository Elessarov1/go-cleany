package com.cleany.admin;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;

import com.cleany.configuration.CleaningProperties;
import com.cleany.media.MediaContent;
import com.cleany.media.MediaStorage;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderEventRepository;
import com.cleany.order.CleaningOrderIssuePhoto;
import com.cleany.order.CleaningOrderIssuePhotoRepository;
import com.cleany.order.CleaningOrderIssueReportRepository;
import com.cleany.order.CleaningOrderPhotoRepository;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderStatistics;
import com.cleany.order.CleaningOrderStatus;

class AdminQueryServiceTest {

    private static final long ADMIN_ID = 900001L;
    private static final Instant NOW = Instant.parse("2026-08-18T09:00:00Z");

    private AdminAccessService accessService;
    private CleaningOrderRepository orderRepository;
    private CleaningOrderIssuePhotoRepository issuePhotoRepository;
    private MediaStorage mediaStorage;
    private AdminQueryService queryService;

    @BeforeEach
    void setUp() {
        accessService = Mockito.mock(AdminAccessService.class);
        orderRepository = Mockito.mock(CleaningOrderRepository.class);
        issuePhotoRepository = Mockito.mock(CleaningOrderIssuePhotoRepository.class);
        mediaStorage = Mockito.mock(MediaStorage.class);
        CleaningProperties properties = Mockito.mock(CleaningProperties.class);
        Mockito.when(properties.zoneId()).thenReturn(ZoneId.of("Europe/Istanbul"));
        Mockito.when(properties.currency()).thenReturn(Currency.getInstance("TRY"));
        queryService = new AdminQueryService(
                accessService,
                orderRepository,
                Mockito.mock(CleaningOrderEventRepository.class),
                Mockito.mock(CleaningOrderPhotoRepository.class),
                Mockito.mock(CleaningOrderIssueReportRepository.class),
                issuePhotoRepository,
                mediaStorage,
                properties,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void issueEvidenceDownload_readsCanonicalMediaStorageContent() {
        CleaningOrderIssuePhoto photo = Mockito.mock(CleaningOrderIssuePhoto.class);
        Mockito.when(accessService.requireCurrentAdmin()).thenReturn(ADMIN_ID);
        Mockito.when(issuePhotoRepository
                .findByIdAndIssueReport_Order_IdAndIssueReport_SubmittedAtIsNotNull(55L, 43L))
                .thenReturn(Optional.of(photo));
        Mockito.when(photo.getMediaAssetId()).thenReturn(71L);
        Mockito.when(mediaStorage.get(71L)).thenReturn(new MediaContent(
                71L,
                new byte[]{1, 2, 3},
                "image/jpeg",
                3L,
                "a".repeat(64),
                NOW
        ));

        AdminIssuePhotoContent result = queryService.getCurrentAdminIssuePhoto(43L, 55L);

        Assertions.assertAll(
                () -> Assertions.assertEquals("image/jpeg", result.contentType()),
                () -> Assertions.assertArrayEquals(new byte[]{1, 2, 3}, result.content()),
                () -> Mockito.verify(mediaStorage).get(71L)
        );
    }

    @Test
    void dashboard_authorizedAdmin_receivesLimitedOrdersAndAggregatedStats() {
        var completed = order(1L, CleaningOrderStatus.COMPLETED, "1100", NOW.minusSeconds(3600));
        var active = order(2L, CleaningOrderStatus.ACCEPTED, "900", NOW.minusSeconds(7200));
        CleaningOrderStatistics statistics = Mockito.mock(CleaningOrderStatistics.class);
        Mockito.when(statistics.getTotalOrders()).thenReturn(5L);
        Mockito.when(statistics.getOrdersToday()).thenReturn(4L);
        Mockito.when(statistics.getNewOrders()).thenReturn(0L);
        Mockito.when(statistics.getActiveOrders()).thenReturn(3L);
        Mockito.when(statistics.getCompletedOrders()).thenReturn(1L);
        Mockito.when(statistics.getCancelledOrders()).thenReturn(1L);
        Mockito.when(statistics.getCompletedAmount()).thenReturn(BigDecimal.valueOf(1100));
        Mockito.when(orderRepository.calculateStatistics(Mockito.any(), Mockito.any()))
                .thenReturn(statistics);
        Mockito.when(orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 2)))
                .thenReturn(List.of(completed, active));

        AdminDashboardResponse result = queryService.getDashboard(ADMIN_ID, 2);

        Mockito.verify(accessService).requireAdmin(ADMIN_ID);
        Mockito.verify(orderRepository).findAllByOrderByCreatedAtDesc(PageRequest.of(0, 2));
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.recentOrders().size()),
                () -> Assertions.assertEquals(5, result.stats().totalOrders()),
                () -> Assertions.assertEquals(4, result.stats().ordersToday()),
                () -> Assertions.assertEquals(3, result.stats().activeOrders()),
                () -> Assertions.assertEquals(1, result.stats().completedOrders()),
                () -> Assertions.assertEquals(1, result.stats().cancelledOrders()),
                () -> Assertions.assertEquals(0, result.stats().completedAmount().compareTo(BigDecimal.valueOf(1100))),
                () -> Assertions.assertEquals("TRY", result.stats().currency())
        );
    }

    private static CleaningOrder order(
            long id,
            CleaningOrderStatus status,
            String price,
            Instant createdAt
    ) {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(id);
        Mockito.when(order.getStatus()).thenReturn(status);
        Mockito.when(order.getPrice()).thenReturn(new BigDecimal(price));
        Mockito.when(order.getCurrency()).thenReturn("TRY");
        Mockito.when(order.getCreatedAt()).thenReturn(createdAt);
        return order;
    }
}
