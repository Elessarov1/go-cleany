package com.cleany.retention;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.media.MediaOrphanCleanupService;
import com.cleany.order.CleaningOrderEventRepository;
import com.cleany.order.CleaningOrderIssuePhotoRepository;
import com.cleany.order.CleaningOrderPhotoRepository;
import com.cleany.order.CleaningOrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataRetentionCleanupService {

    private final CleaningOrderRepository orderRepository;
    private final CleaningOrderIssuePhotoRepository issuePhotoRepository;
    private final CleaningOrderPhotoRepository completionPhotoRepository;
    private final CleaningOrderEventRepository eventRepository;
    private final MediaOrphanCleanupService mediaOrphanCleanupService;

    @Transactional
    public DataRetentionCleanupResult cleanupBatch(Instant cutoff, int batchSize) {
        Objects.requireNonNull(cutoff, "cutoff");
        if (batchSize < 1) {
            throw new IllegalArgumentException("batchSize must be positive");
        }
        List<Long> orderIds = orderRepository.findRetentionEligibleOrderIds(cutoff, batchSize);
        int completionPhotos = 0;
        int issuePhotos = 0;
        int auditEvents = 0;
        if (!orderIds.isEmpty()) {
            completionPhotos = completionPhotoRepository.deleteByOrderIds(orderIds);
            issuePhotos = issuePhotoRepository.deleteResolvedByOrderIds(orderIds);
            auditEvents = eventRepository.deleteByOrderIds(orderIds);
        }
        int mediaAssets = mediaOrphanCleanupService.deleteUnreferencedBatch(batchSize);
        return new DataRetentionCleanupResult(
                cutoff,
                orderIds.size(),
                issuePhotos,
                completionPhotos,
                auditEvents,
                mediaAssets,
                orderIds.size() == batchSize || mediaAssets == batchSize
        );
    }
}
