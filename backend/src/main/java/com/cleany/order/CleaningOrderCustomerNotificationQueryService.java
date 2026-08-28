package com.cleany.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.retention.DataRetentionProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CleaningOrderCustomerNotificationQueryService {

    private final CleaningOrderRepository orderRepository;
    private final CleaningOrderPhotoRepository completionPhotoRepository;
    private final CleaningOrderIssueReportRepository issueReportRepository;
    private final CleaningOrderIssuePhotoRepository issuePhotoRepository;
    private final DataRetentionProperties dataRetentionProperties;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public CleaningOrderCustomerNotification.Completed completed(long orderId) {
        CleaningOrder order = findOrder(orderId);
        var mediaIds = completionPhotoRepository.findAllByOrderIdOrderByCreatedAt(orderId).stream()
                .map(CleaningOrderPhoto::getMediaAssetId)
                .toList();
        if (mediaIds.isEmpty()) {
            throw new PhotoReportEmptyException(orderId);
        }
        return new CleaningOrderCustomerNotification.Completed(
                order.getId(),
                order.getApartmentType(),
                order.isDuplex(),
                order.getArea(),
                order.getRequestedDate(),
                order.getCleanerComment(),
                mediaIds,
                dataRetentionProperties.days()
        );
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public CleaningOrderCustomerNotification.OnsiteIssueReported onsiteIssue(long orderId) {
        CleaningOrderIssueReport report = issueReportRepository
                .findByOrder_IdAndSubmittedAtIsNotNull(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        var mediaIds = issuePhotoRepository.findAllByIssueReport_IdOrderByCreatedAtAscIdAsc(report.getId()).stream()
                .map(CleaningOrderIssuePhoto::getMediaAssetId)
                .toList();
        return new CleaningOrderCustomerNotification.OnsiteIssueReported(
                orderId,
                report.getReason(),
                report.getComment(),
                mediaIds
        );
    }

    private CleaningOrder findOrder(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
