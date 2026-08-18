package com.cleany.admin;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.configuration.CleaningProperties;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderEventRepository;
import com.cleany.order.CleaningOrderPhotoRepository;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderStatus;

class AdminQueryServiceTest {

    private static final long ADMIN_ID = 900001L;
    private static final Instant NOW = Instant.parse("2026-08-18T09:00:00Z");

    private AdminAccessService accessService;
    private CleaningOrderRepository orderRepository;
    private AdminQueryService queryService;

    @BeforeEach
    void setUp() {
        accessService = Mockito.mock(AdminAccessService.class);
        orderRepository = Mockito.mock(CleaningOrderRepository.class);
        CleaningProperties properties = Mockito.mock(CleaningProperties.class);
        Mockito.when(properties.zoneId()).thenReturn(ZoneId.of("Europe/Istanbul"));
        Mockito.when(properties.currency()).thenReturn(Currency.getInstance("TRY"));
        queryService = new AdminQueryService(
                accessService,
                orderRepository,
                Mockito.mock(CleaningOrderEventRepository.class),
                Mockito.mock(CleaningOrderPhotoRepository.class),
                properties,
                Clock.fixed(NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void dashboard_authorizedAdmin_receivesLimitedOrdersAndAggregatedStats() {
        var completed = order(1L, CleaningOrderStatus.COMPLETED, "1100", NOW.minusSeconds(3600));
        var active = order(2L, CleaningOrderStatus.ACCEPTED, "900", NOW.minusSeconds(7200));
        var awaitingReport = order(3L, CleaningOrderStatus.AWAITING_REPORT, "800", NOW.minusSeconds(9000));
        var cancelled = order(4L, CleaningOrderStatus.CANCELLED, "1200", NOW.minusSeconds(172800));
        Mockito.when(orderRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(completed, active, awaitingReport, cancelled));

        AdminDashboardResponse result = queryService.getDashboard(ADMIN_ID, 2);

        Mockito.verify(accessService).requireAdmin(ADMIN_ID);
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.recentOrders().size()),
                () -> Assertions.assertEquals(4, result.stats().totalOrders()),
                () -> Assertions.assertEquals(3, result.stats().ordersToday()),
                () -> Assertions.assertEquals(2, result.stats().activeOrders()),
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
