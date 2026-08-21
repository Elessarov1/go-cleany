package com.cleany.retention;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.media.MediaOrphanCleanupService;
import com.cleany.order.CleaningOrderEventRepository;
import com.cleany.order.CleaningOrderIssuePhotoRepository;
import com.cleany.order.CleaningOrderPhotoRepository;
import com.cleany.order.CleaningOrderRepository;

class DataRetentionCleanupServiceTest {

    private static final Instant CUTOFF = Instant.parse("2026-08-14T12:00:00Z");

    @Test
    void eligibleOrders_payloadDeletedInSafeOrder() {
        CleaningOrderRepository orderRepository = Mockito.mock(CleaningOrderRepository.class);
        CleaningOrderIssuePhotoRepository issuePhotoRepository =
                Mockito.mock(CleaningOrderIssuePhotoRepository.class);
        CleaningOrderPhotoRepository completionPhotoRepository =
                Mockito.mock(CleaningOrderPhotoRepository.class);
        CleaningOrderEventRepository eventRepository = Mockito.mock(CleaningOrderEventRepository.class);
        MediaOrphanCleanupService mediaOrphanCleanupService = Mockito.mock(MediaOrphanCleanupService.class);
        List<Long> orderIds = List.of(10L, 11L);
        Mockito.when(orderRepository.findRetentionEligibleOrderIds(CUTOFF)).thenReturn(orderIds);
        Mockito.when(issuePhotoRepository.deleteResolvedByOrderIds(orderIds)).thenReturn(3);
        Mockito.when(completionPhotoRepository.deleteByOrderIds(orderIds)).thenReturn(2);
        Mockito.when(eventRepository.deleteByOrderIds(orderIds)).thenReturn(8);
        Mockito.when(mediaOrphanCleanupService.deleteUnreferenced()).thenReturn(4);
        var service = new DataRetentionCleanupService(
                orderRepository,
                issuePhotoRepository,
                completionPhotoRepository,
                eventRepository,
                mediaOrphanCleanupService
        );

        DataRetentionCleanupResult result = service.cleanup(CUTOFF);

        InOrder deletionOrder = Mockito.inOrder(
                issuePhotoRepository,
                completionPhotoRepository,
                eventRepository,
                mediaOrphanCleanupService
        );
        deletionOrder.verify(completionPhotoRepository).deleteByOrderIds(orderIds);
        deletionOrder.verify(issuePhotoRepository).deleteResolvedByOrderIds(orderIds);
        deletionOrder.verify(eventRepository).deleteByOrderIds(orderIds);
        deletionOrder.verify(mediaOrphanCleanupService).deleteUnreferenced();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.eligibleOrderCount()),
                () -> Assertions.assertEquals(3, result.deletedIssuePhotoCount()),
                () -> Assertions.assertEquals(2, result.deletedCompletionPhotoCount()),
                () -> Assertions.assertEquals(8, result.deletedAuditEventCount()),
                () -> Assertions.assertEquals(4, result.deletedMediaAssetCount())
        );
    }

    @Test
    void noEligibleOrders_existingOrphanMediaStillRemoved() {
        CleaningOrderRepository orderRepository = Mockito.mock(CleaningOrderRepository.class);
        CleaningOrderIssuePhotoRepository issuePhotoRepository =
                Mockito.mock(CleaningOrderIssuePhotoRepository.class);
        CleaningOrderPhotoRepository completionPhotoRepository =
                Mockito.mock(CleaningOrderPhotoRepository.class);
        CleaningOrderEventRepository eventRepository = Mockito.mock(CleaningOrderEventRepository.class);
        MediaOrphanCleanupService mediaOrphanCleanupService = Mockito.mock(MediaOrphanCleanupService.class);
        Mockito.when(orderRepository.findRetentionEligibleOrderIds(CUTOFF)).thenReturn(List.of());
        Mockito.when(mediaOrphanCleanupService.deleteUnreferenced()).thenReturn(2);
        var service = new DataRetentionCleanupService(
                orderRepository,
                issuePhotoRepository,
                completionPhotoRepository,
                eventRepository,
                mediaOrphanCleanupService
        );

        DataRetentionCleanupResult result = service.cleanup(CUTOFF);

        Mockito.verifyNoInteractions(issuePhotoRepository, completionPhotoRepository, eventRepository);
        Mockito.verify(mediaOrphanCleanupService).deleteUnreferenced();
        Assertions.assertAll(
                () -> Assertions.assertEquals(0, result.eligibleOrderCount()),
                () -> Assertions.assertEquals(2, result.deletedMediaAssetCount())
        );
    }

    @Test
    void cleanupBoundary_isTransactional() throws NoSuchMethodException {
        var method = DataRetentionCleanupService.class.getDeclaredMethod("cleanup", Instant.class);

        Assertions.assertNotNull(method.getAnnotation(Transactional.class));
    }
}
