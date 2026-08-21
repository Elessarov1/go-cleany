package com.cleany.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.notification.ExternalMediaReference;

@Service
public class CleaningOrderCustomerNotificationQueryService {

    private final CleaningOrderRepository orderRepository;
    private final CleaningOrderPhotoRepository completionPhotoRepository;
    private final CleaningOrderIssueReportRepository issueReportRepository;
    private final CleaningOrderIssuePhotoRepository issuePhotoRepository;

    public CleaningOrderCustomerNotificationQueryService(
            CleaningOrderRepository orderRepository,
            CleaningOrderPhotoRepository completionPhotoRepository,
            CleaningOrderIssueReportRepository issueReportRepository,
            CleaningOrderIssuePhotoRepository issuePhotoRepository
    ) {
        this.orderRepository = orderRepository;
        this.completionPhotoRepository = completionPhotoRepository;
        this.issueReportRepository = issueReportRepository;
        this.issuePhotoRepository = issuePhotoRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public CleaningOrderCustomerNotification.Completed completed(long orderId) {
        CleaningOrder order = findOrder(orderId);
        var photos = completionPhotoRepository.findAllByOrderIdOrderByCreatedAt(orderId).stream()
                .map(CleaningOrderPhoto::getTelegramFileId)
                .map(ExternalMediaReference::telegram)
                .toList();
        if (photos.isEmpty()) {
            throw new PhotoReportEmptyException(orderId);
        }
        return new CleaningOrderCustomerNotification.Completed(
                order.getId(),
                order.getApartmentType(),
                order.isDuplex(),
                order.getArea(),
                order.getRequestedDate(),
                order.getCleanerComment(),
                photos
        );
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public CleaningOrderCustomerNotification.OnsiteIssueReported onsiteIssue(long orderId) {
        CleaningOrderIssueReport report = issueReportRepository
                .findByOrder_IdAndSubmittedAtIsNotNull(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        var photos = issuePhotoRepository.findAllByIssueReport_IdOrderByCreatedAtAscIdAsc(report.getId()).stream()
                .map(CleaningOrderIssuePhoto::getTelegramFileId)
                .map(ExternalMediaReference::telegram)
                .toList();
        return new CleaningOrderCustomerNotification.OnsiteIssueReported(
                orderId,
                report.getReason(),
                report.getComment(),
                photos
        );
    }

    private CleaningOrder findOrder(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
