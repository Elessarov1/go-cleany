package com.cleany.admin;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.configuration.CleaningProperties;
import com.cleany.media.MediaStorage;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderEventRepository;
import com.cleany.order.CleaningOrderIssuePhotoRepository;
import com.cleany.order.CleaningOrderIssueReportRepository;
import com.cleany.order.CleaningOrderPhotoRepository;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderResponse;
import com.cleany.order.CleaningOrderStatistics;
import com.cleany.order.OrderNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminQueryService {

    private static final int MAX_RECENT_ORDERS = 100;

    private final AdminAccessService accessService;
    private final CleaningOrderRepository orderRepository;
    private final CleaningOrderEventRepository eventRepository;
    private final CleaningOrderPhotoRepository photoRepository;
    private final CleaningOrderIssueReportRepository issueReportRepository;
    private final CleaningOrderIssuePhotoRepository issuePhotoRepository;
    private final MediaStorage mediaStorage;
    private final CleaningProperties cleaningProperties;
    private final Clock clock;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getCurrentAdminDashboard(int requestedLimit) {
        return getDashboard(accessService.requireCurrentAdmin(), requestedLimit);
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard(long adminTelegramId, int requestedLimit) {
        accessService.requireAdmin(adminTelegramId);
        int limit = Math.max(1, Math.min(requestedLimit, MAX_RECENT_ORDERS));
        LocalDate today = LocalDate.now(clock.withZone(cleaningProperties.zoneId()));
        var todayStart = today.atStartOfDay(cleaningProperties.zoneId()).toInstant();
        var tomorrowStart = today.plusDays(1)
                .atStartOfDay(cleaningProperties.zoneId())
                .toInstant();
        CleaningOrderStatistics statistics = orderRepository.calculateStatistics(
                todayStart,
                tomorrowStart
        );
        List<AdminOrderSummaryResponse> recentOrders = orderRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit))
                .stream()
                .map(AdminOrderSummaryResponse::from)
                .toList();
        return new AdminDashboardResponse(stats(statistics), recentOrders);
    }

    @Transactional(readOnly = true)
    public AdminOrderDetailsResponse getCurrentAdminOrder(long orderId) {
        return getOrder(accessService.requireCurrentAdmin(), orderId);
    }

    @Transactional(readOnly = true)
    public AdminOrderDetailsResponse getOrder(long adminTelegramId, long orderId) {
        accessService.requireAdmin(adminTelegramId);
        CleaningOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        var events = eventRepository.findAllByOrderIdOrderByOccurredAtAscIdAsc(orderId).stream()
                .map(AdminOrderEventResponse::from)
                .toList();
        AdminOnsiteIssueResponse onsiteIssue = issueReportRepository
                .findByOrder_IdAndSubmittedAtIsNotNull(orderId)
                .map(report -> AdminOnsiteIssueResponse.from(
                        report,
                        issuePhotoRepository.findMetadataByIssueReportId(report.getId())
                ))
                .orElse(null);
        return new AdminOrderDetailsResponse(
                CleaningOrderResponse.from(order),
                AdminOrderFinancialResponse.from(order),
                photoRepository.countByOrderId(orderId),
                onsiteIssue,
                events
        );
    }

    @Transactional(readOnly = true)
    public AdminIssuePhotoContent getCurrentAdminIssuePhoto(long orderId, long photoId) {
        accessService.requireCurrentAdmin();
        var photo = issuePhotoRepository
                .findByIdAndIssueReport_Order_IdAndIssueReport_SubmittedAtIsNotNull(photoId, orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        var media = mediaStorage.get(photo.getMediaAssetId());
        return new AdminIssuePhotoContent(media.contentType(), media.content());
    }

    private AdminStatsResponse stats(CleaningOrderStatistics statistics) {
        return new AdminStatsResponse(
                statistics.getTotalOrders(),
                statistics.getOrdersToday(),
                statistics.getNewOrders(),
                statistics.getActiveOrders(),
                statistics.getCompletedOrders(),
                statistics.getCancelledOrders(),
                statistics.getCompletedAmount(),
                cleaningProperties.currency().getCurrencyCode()
        );
    }
}
