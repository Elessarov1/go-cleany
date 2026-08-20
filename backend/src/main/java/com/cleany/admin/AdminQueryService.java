package com.cleany.admin;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.configuration.CleaningProperties;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderEventRepository;
import com.cleany.order.CleaningOrderIssuePhotoRepository;
import com.cleany.order.CleaningOrderIssueReportRepository;
import com.cleany.order.CleaningOrderPhotoRepository;
import com.cleany.order.CleaningOrderRepository;
import com.cleany.order.CleaningOrderResponse;
import com.cleany.order.CleaningOrderStatus;
import com.cleany.order.OrderNotFoundException;

@Service
public class AdminQueryService {

    private static final int MAX_RECENT_ORDERS = 100;

    private final AdminAccessService accessService;
    private final CleaningOrderRepository orderRepository;
    private final CleaningOrderEventRepository eventRepository;
    private final CleaningOrderPhotoRepository photoRepository;
    private final CleaningOrderIssueReportRepository issueReportRepository;
    private final CleaningOrderIssuePhotoRepository issuePhotoRepository;
    private final CleaningProperties cleaningProperties;
    private final Clock clock;

    public AdminQueryService(
            AdminAccessService accessService,
            CleaningOrderRepository orderRepository,
            CleaningOrderEventRepository eventRepository,
            CleaningOrderPhotoRepository photoRepository,
            CleaningOrderIssueReportRepository issueReportRepository,
            CleaningOrderIssuePhotoRepository issuePhotoRepository,
            CleaningProperties cleaningProperties,
            Clock clock
    ) {
        this.accessService = accessService;
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
        this.photoRepository = photoRepository;
        this.issueReportRepository = issueReportRepository;
        this.issuePhotoRepository = issuePhotoRepository;
        this.cleaningProperties = cleaningProperties;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getCurrentAdminDashboard(int requestedLimit) {
        return getDashboard(accessService.requireCurrentAdmin(), requestedLimit);
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard(long adminTelegramId, int requestedLimit) {
        accessService.requireAdmin(adminTelegramId);
        List<CleaningOrder> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        int limit = Math.max(1, Math.min(requestedLimit, MAX_RECENT_ORDERS));
        List<AdminOrderSummaryResponse> recentOrders = orders.stream()
                .limit(limit)
                .map(AdminOrderSummaryResponse::from)
                .toList();
        return new AdminDashboardResponse(stats(orders), recentOrders);
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
        return new AdminIssuePhotoContent(photo.getContentType(), photo.getContent());
    }

    private AdminStatsResponse stats(List<CleaningOrder> orders) {
        LocalDate today = LocalDate.now(clock.withZone(cleaningProperties.zoneId()));
        long ordersToday = orders.stream()
                .filter(order -> LocalDate.ofInstant(
                        order.getCreatedAt(),
                        cleaningProperties.zoneId()
                ).equals(today))
                .count();
        long newOrders = count(orders, CleaningOrderStatus.NEW);
        long activeOrders = orders.stream()
                .filter(order -> order.getStatus() == CleaningOrderStatus.ACCEPTED
                        || order.getStatus() == CleaningOrderStatus.AWAITING_REPORT
                        || order.getStatus() == CleaningOrderStatus.ONSITE_ISSUE_REPORTED)
                .count();
        long completedOrders = count(orders, CleaningOrderStatus.COMPLETED);
        long cancelledOrders = count(orders, CleaningOrderStatus.CANCELLED);
        BigDecimal completedAmount = orders.stream()
                .filter(order -> order.getStatus() == CleaningOrderStatus.COMPLETED)
                .map(CleaningOrder::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdminStatsResponse(
                orders.size(),
                ordersToday,
                newOrders,
                activeOrders,
                completedOrders,
                cancelledOrders,
                completedAmount,
                cleaningProperties.currency().getCurrencyCode()
        );
    }

    private static long count(List<CleaningOrder> orders, CleaningOrderStatus status) {
        return orders.stream().filter(order -> order.getStatus() == status).count();
    }
}
