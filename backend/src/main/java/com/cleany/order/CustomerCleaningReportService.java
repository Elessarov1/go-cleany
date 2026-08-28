package com.cleany.order;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.customer.CustomerAccountService;
import com.cleany.media.MediaStorage;
import com.cleany.retention.DataRetentionProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerCleaningReportService {

    private final CustomerAccountService customerAccountService;
    private final CleaningOrderRepository orderRepository;
    private final CleaningOrderPhotoRepository photoRepository;
    private final MediaStorage mediaStorage;
    private final DataRetentionProperties retentionProperties;
    private final Clock clock;

    @Transactional(readOnly = true)
    public CustomerCleaningReportResponse currentCustomerReport(long orderId) {
        long customerId = customerAccountService.currentCustomer().customerId();
        CleaningOrder order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return report(order);
    }

    @Transactional(readOnly = true)
    public CustomerCleaningReportPhotoContent currentCustomerPhoto(long orderId, long mediaId) {
        CustomerCleaningReportResponse report = currentCustomerReport(orderId);
        if (report.status() == CleaningReportStatus.EXPIRED) {
            throw new CleaningReportExpiredException(orderId);
        }
        if (report.status() != CleaningReportStatus.AVAILABLE
                || report.photos().stream().noneMatch(photo -> photo.id() == mediaId)) {
            throw new OrderNotFoundException(orderId);
        }
        var media = mediaStorage.get(mediaId);
        return new CustomerCleaningReportPhotoContent(media.contentType(), media.content());
    }

    @Transactional(readOnly = true)
    public CustomerCleaningReportResponse summary(CleaningOrder order) {
        return report(order);
    }

    private CustomerCleaningReportResponse report(CleaningOrder order) {
        if (order.getStatus() != CleaningOrderStatus.COMPLETED || order.getCompletedAt() == null) {
            return response(CleaningReportStatus.NOT_READY, null, order.getCleanerComment(), List.of());
        }
        Instant expiresAt = order.getCompletedAt().plus(Duration.ofDays(retentionProperties.days()));
        if (!clock.instant().isBefore(expiresAt)) {
            return response(CleaningReportStatus.EXPIRED, expiresAt, order.getCleanerComment(), List.of());
        }
        List<CustomerCleaningReportPhotoResponse> photos = photoRepository
                .findAllByOrderIdOrderByCreatedAtAscIdAsc(order.getId())
                .stream()
                .map(photo -> new CustomerCleaningReportPhotoResponse(
                        photo.getMediaAssetId(),
                        "/api/v1/cleaning/orders/" + order.getId()
                                + "/report/photos/" + photo.getMediaAssetId()
                ))
                .toList();
        return response(
                photos.isEmpty() ? CleaningReportStatus.NOT_READY : CleaningReportStatus.AVAILABLE,
                expiresAt,
                order.getCleanerComment(),
                photos
        );
    }

    private CustomerCleaningReportResponse response(
            CleaningReportStatus status,
            Instant expiresAt,
            String cleanerComment,
            List<CustomerCleaningReportPhotoResponse> photos
    ) {
        return new CustomerCleaningReportResponse(
                status,
                expiresAt,
                retentionProperties.days(),
                cleanerComment,
                photos
        );
    }
}
