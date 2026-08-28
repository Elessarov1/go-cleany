package com.cleany.order;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.media.MediaContent;
import com.cleany.media.MediaStorage;
import com.cleany.retention.DataRetentionProperties;

@ExtendWith(MockitoExtension.class)
class CustomerCleaningReportServiceTest {

    private static final Instant COMPLETED_AT = Instant.parse("2026-09-01T10:00:00Z");

    @Mock
    private CustomerAccountService customerAccountService;

    @Mock
    private CleaningOrderRepository orderRepository;

    @Mock
    private CleaningOrderPhotoRepository photoRepository;

    @Mock
    private MediaStorage mediaStorage;

    @Test
    void availableReportUsesCustomerOwnershipAndReturnsAuthenticatedMediaContent() {
        CleaningOrder order = completedOrder();
        CleaningOrderPhoto photo = Mockito.mock(CleaningOrderPhoto.class);
        Mockito.when(photo.getMediaAssetId()).thenReturn(71L);
        prepareOwnedOrder(order);
        Mockito.when(photoRepository.findAllByOrderIdOrderByCreatedAtAscIdAsc(43L))
                .thenReturn(List.of(photo));
        byte[] jpeg = {(byte) 0xFF, (byte) 0xD8, 1, 2};
        Mockito.when(mediaStorage.get(71L)).thenReturn(new MediaContent(
                71L,
                jpeg,
                "image/jpeg",
                jpeg.length,
                "hash",
                COMPLETED_AT
        ));
        CustomerCleaningReportService service = service(COMPLETED_AT.plusSeconds(1));

        CustomerCleaningReportResponse report = service.currentCustomerReport(43L);
        CustomerCleaningReportPhotoContent content = service.currentCustomerPhoto(43L, 71L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(CleaningReportStatus.AVAILABLE, report.status()),
                () -> Assertions.assertEquals(COMPLETED_AT.plusSeconds(7L * 24 * 60 * 60), report.expiresAt()),
                () -> Assertions.assertEquals(7, report.retentionDays()),
                () -> Assertions.assertEquals("Done", report.cleanerComment()),
                () -> Assertions.assertEquals(71L, report.photos().getFirst().id()),
                () -> Assertions.assertEquals(
                        "/api/v1/cleaning/orders/43/report/photos/71",
                        report.photos().getFirst().url()
                ),
                () -> Assertions.assertEquals("image/jpeg", content.contentType()),
                () -> Assertions.assertArrayEquals(jpeg, content.content())
        );
    }

    @Test
    void logicalExpiryStartsAtExactCompletedAtPlusRetentionAndBlocksMediaBeforeCleanup() {
        CleaningOrder order = completedOrder();
        prepareOwnedOrder(order);
        CustomerCleaningReportService service = service(COMPLETED_AT.plusSeconds(7L * 24 * 60 * 60));

        CustomerCleaningReportResponse report = service.currentCustomerReport(43L);

        Assertions.assertAll(
                () -> Assertions.assertEquals(CleaningReportStatus.EXPIRED, report.status()),
                () -> Assertions.assertTrue(report.photos().isEmpty()),
                () -> Assertions.assertEquals("Done", report.cleanerComment()),
                () -> Assertions.assertThrows(
                        CleaningReportExpiredException.class,
                        () -> service.currentCustomerPhoto(43L, 71L)
                )
        );
        Mockito.verifyNoInteractions(mediaStorage);
    }

    @Test
    void nonOwnerCannotReadReportOrGuessMediaId() {
        Mockito.when(customerAccountService.currentCustomer()).thenReturn(currentCustomer());
        Mockito.when(orderRepository.findByIdAndCustomerId(43L, 10L)).thenReturn(Optional.empty());
        CustomerCleaningReportService service = service(COMPLETED_AT.plusSeconds(1));

        Assertions.assertThrows(OrderNotFoundException.class, () -> service.currentCustomerReport(43L));
        Assertions.assertThrows(OrderNotFoundException.class, () -> service.currentCustomerPhoto(43L, 71L));
        Mockito.verifyNoInteractions(mediaStorage);
    }

    private void prepareOwnedOrder(CleaningOrder order) {
        Mockito.when(customerAccountService.currentCustomer()).thenReturn(currentCustomer());
        Mockito.when(orderRepository.findByIdAndCustomerId(43L, 10L)).thenReturn(Optional.of(order));
    }

    private CustomerCleaningReportService service(Instant now) {
        return new CustomerCleaningReportService(
                customerAccountService,
                orderRepository,
                photoRepository,
                mediaStorage,
                new DataRetentionProperties(true, 7, "0 0 3 * * *", 100, 10),
                Clock.fixed(now, ZoneOffset.UTC)
        );
    }

    private static CleaningOrder completedOrder() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.lenient().when(order.getId()).thenReturn(43L);
        Mockito.when(order.getStatus()).thenReturn(CleaningOrderStatus.COMPLETED);
        Mockito.when(order.getCompletedAt()).thenReturn(COMPLETED_AT);
        Mockito.when(order.getCleanerComment()).thenReturn("Done");
        return order;
    }

    private static CurrentCustomer currentCustomer() {
        return new CurrentCustomer(
                10L,
                100L,
                ExternalIdentityProvider.GOOGLE,
                "google-sub",
                null,
                "Customer",
                "en"
        );
    }
}
