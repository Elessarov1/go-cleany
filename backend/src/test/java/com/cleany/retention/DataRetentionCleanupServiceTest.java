package com.cleany.retention;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

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
        List<Long> orderIds = List.of(10L, 11L);
        Mockito.when(orderRepository.findRetentionEligibleOrderIds(CUTOFF)).thenReturn(orderIds);
        Mockito.when(issuePhotoRepository.deleteResolvedByOrderIds(orderIds)).thenReturn(3);
        Mockito.when(completionPhotoRepository.deleteByOrderIds(orderIds)).thenReturn(2);
        Mockito.when(eventRepository.deleteByOrderIds(orderIds)).thenReturn(8);
        var service = new DataRetentionCleanupService(
                orderRepository,
                issuePhotoRepository,
                completionPhotoRepository,
                eventRepository
        );

        DataRetentionCleanupResult result = service.cleanup(CUTOFF);

        InOrder deletionOrder = Mockito.inOrder(
                issuePhotoRepository,
                completionPhotoRepository,
                eventRepository
        );
        deletionOrder.verify(issuePhotoRepository).deleteResolvedByOrderIds(orderIds);
        deletionOrder.verify(completionPhotoRepository).deleteByOrderIds(orderIds);
        deletionOrder.verify(eventRepository).deleteByOrderIds(orderIds);
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.eligibleOrderCount()),
                () -> Assertions.assertEquals(3, result.deletedIssuePhotoCount()),
                () -> Assertions.assertEquals(2, result.deletedCompletionPhotoCount()),
                () -> Assertions.assertEquals(8, result.deletedAuditEventCount())
        );
    }

    @Test
    void cleanupBoundary_isTransactional() throws NoSuchMethodException {
        var method = DataRetentionCleanupService.class.getDeclaredMethod("cleanup", Instant.class);

        Assertions.assertNotNull(method.getAnnotation(Transactional.class));
    }
}
