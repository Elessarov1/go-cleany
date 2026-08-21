package com.cleany.order;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.notification.ExternalMediaReference;

class CleaningOrderCustomerNotificationQueryServiceTest {

    private final CleaningOrderRepository orderRepository = Mockito.mock(CleaningOrderRepository.class);
    private final CleaningOrderPhotoRepository completionPhotoRepository =
            Mockito.mock(CleaningOrderPhotoRepository.class);
    private final CleaningOrderIssueReportRepository issueReportRepository =
            Mockito.mock(CleaningOrderIssueReportRepository.class);
    private final CleaningOrderIssuePhotoRepository issuePhotoRepository =
            Mockito.mock(CleaningOrderIssuePhotoRepository.class);
    private final CleaningOrderCustomerNotificationQueryService service =
            new CleaningOrderCustomerNotificationQueryService(
                    orderRepository,
                    completionPhotoRepository,
                    issueReportRepository,
                    issuePhotoRepository
            );

    @Test
    void completedOrder_notificationSnapshotContainsOrderDetailsAndProviderMediaReferences() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        CleaningOrderPhoto firstPhoto = Mockito.mock(CleaningOrderPhoto.class);
        CleaningOrderPhoto secondPhoto = Mockito.mock(CleaningOrderPhoto.class);
        Mockito.when(orderRepository.findById(43L)).thenReturn(Optional.of(order));
        Mockito.when(order.getId()).thenReturn(43L);
        Mockito.when(order.getApartmentType()).thenReturn(ApartmentType.TWO_PLUS_ONE);
        Mockito.when(order.getArea()).thenReturn(ServiceArea.MAHMUTLAR);
        Mockito.when(order.getRequestedDate()).thenReturn(LocalDate.of(2026, 8, 18));
        Mockito.when(order.getCleanerComment()).thenReturn("Готово");
        Mockito.when(firstPhoto.getTelegramFileId()).thenReturn("photo-1");
        Mockito.when(secondPhoto.getTelegramFileId()).thenReturn("photo-2");
        Mockito.when(completionPhotoRepository.findAllByOrderIdOrderByCreatedAt(43L))
                .thenReturn(List.of(firstPhoto, secondPhoto));

        var notification = service.completed(43L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(43L, notification.orderId()),
                () -> Assertions.assertEquals(ApartmentType.TWO_PLUS_ONE, notification.apartmentType()),
                () -> Assertions.assertEquals(ServiceArea.MAHMUTLAR, notification.area()),
                () -> Assertions.assertEquals("Готово", notification.cleanerComment()),
                () -> Assertions.assertEquals(
                        List.of(
                                ExternalMediaReference.telegram("photo-1"),
                                ExternalMediaReference.telegram("photo-2")
                        ),
                        notification.photos()
                )
        );
    }

    @Test
    void onsiteIssue_notificationSnapshotContainsReasonCommentAndInternalMediaIds() {
        CleaningOrderIssueReport report = Mockito.mock(CleaningOrderIssueReport.class);
        CleaningOrderIssuePhoto photo = Mockito.mock(CleaningOrderIssuePhoto.class);
        Mockito.when(issueReportRepository.findByOrder_IdAndSubmittedAtIsNotNull(43L))
                .thenReturn(Optional.of(report));
        Mockito.when(report.getId()).thenReturn(55L);
        Mockito.when(report.getReason()).thenReturn(OnsiteIssueReason.ACCESS_PROBLEM);
        Mockito.when(report.getComment()).thenReturn("Нет ключа");
        Mockito.when(issuePhotoRepository.findAllByIssueReport_IdOrderByCreatedAtAscIdAsc(55L))
                .thenReturn(List.of(photo));
        Mockito.when(photo.getMediaAssetId()).thenReturn(71L);

        var notification = service.onsiteIssue(43L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(43L, notification.orderId()),
                () -> Assertions.assertEquals(OnsiteIssueReason.ACCESS_PROBLEM, notification.reason()),
                () -> Assertions.assertEquals("Нет ключа", notification.comment()),
                () -> Assertions.assertEquals(List.of(71L), notification.mediaIds())
        );
    }
}
