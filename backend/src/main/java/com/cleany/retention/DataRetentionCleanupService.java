package com.cleany.retention;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.order.CleaningOrderEventRepository;
import com.cleany.order.CleaningOrderIssuePhotoRepository;
import com.cleany.order.CleaningOrderPhotoRepository;
import com.cleany.order.CleaningOrderRepository;

@Service
public class DataRetentionCleanupService {

    private final CleaningOrderRepository orderRepository;
    private final CleaningOrderIssuePhotoRepository issuePhotoRepository;
    private final CleaningOrderPhotoRepository completionPhotoRepository;
    private final CleaningOrderEventRepository eventRepository;

    public DataRetentionCleanupService(
            CleaningOrderRepository orderRepository,
            CleaningOrderIssuePhotoRepository issuePhotoRepository,
            CleaningOrderPhotoRepository completionPhotoRepository,
            CleaningOrderEventRepository eventRepository
    ) {
        this.orderRepository = orderRepository;
        this.issuePhotoRepository = issuePhotoRepository;
        this.completionPhotoRepository = completionPhotoRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public DataRetentionCleanupResult cleanup(Instant cutoff) {
        Objects.requireNonNull(cutoff, "cutoff");
        List<Long> orderIds = orderRepository.findRetentionEligibleOrderIds(cutoff);
        if (orderIds.isEmpty()) {
            return new DataRetentionCleanupResult(cutoff, 0, 0, 0, 0);
        }

        int issuePhotos = issuePhotoRepository.deleteResolvedByOrderIds(orderIds);
        int completionPhotos = completionPhotoRepository.deleteByOrderIds(orderIds);
        int auditEvents = eventRepository.deleteByOrderIds(orderIds);
        return new DataRetentionCleanupResult(
                cutoff,
                orderIds.size(),
                issuePhotos,
                completionPhotos,
                auditEvents
        );
    }
}
