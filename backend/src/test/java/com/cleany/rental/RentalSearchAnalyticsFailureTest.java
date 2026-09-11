package com.cleany.rental;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.cleany.catalog.PlatformServiceAccessService;

class RentalSearchAnalyticsFailureTest {

    @Test
    void unpersistedExecutionStillPaginates() {
        RentalSearchRepository searchRepository = mock(RentalSearchRepository.class);
        RentalSearchTrackingRepository trackingRepository = mock(
                RentalSearchTrackingRepository.class
        );
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T09:00:00Z"), ZoneOffset.UTC);
        RentalSearchPropertyRow firstProperty = property(1, 1);
        RentalSearchPropertyRow secondProperty = property(2, 2);
        when(searchRepository.findPublished(null, null, 2))
                .thenReturn(List.of(firstProperty, secondProperty));
        when(searchRepository.findPublished(1, 1L, 2))
                .thenReturn(Collections.singletonList(secondProperty));
        when(searchRepository.findCovers(any())).thenReturn(Collections.emptyList());
        doThrow(new IllegalStateException("analytics unavailable"))
                .when(trackingRepository)
                .insertExecution(
                        any(UUID.class),
                        any(RentalSearchMode.class),
                        anyInt(),
                        anyLong(),
                        isNull(),
                        any(Instant.class)
                );
        RentalSearchTrackingService trackingService = new RentalSearchTrackingService(
                trackingRepository,
                clock
        );
        RentalSearchService service = new RentalSearchService(
                searchRepository,
                mock(RentalStayPolicy.class),
                mock(RentalPriceService.class),
                mock(PlatformServiceAccessService.class),
                trackingService,
                new RentalSearchCursorCodec(),
                clock
        );

        RentalSearchResponse first = service.search(
                null, null, null, null, null, null, 1, null
        );
        RentalSearchResponse second = service.search(
                null, null, null, null, null, first.nextCursor(), 1, null
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, first.properties().size()),
                () -> Assertions.assertTrue(first.hasMore()),
                () -> Assertions.assertEquals(2, second.properties().getFirst().id()),
                () -> Assertions.assertFalse(second.hasMore()),
                () -> Assertions.assertEquals(
                        first.searchExecutionId(),
                        second.searchExecutionId()
                )
        );
        verify(trackingRepository).insertExecution(
                any(UUID.class),
                any(RentalSearchMode.class),
                anyInt(),
                anyLong(),
                isNull(),
                any(Instant.class)
        );
    }

    private static RentalSearchPropertyRow property(long id, int displayOrder) {
        return new RentalSearchPropertyRow(
                id,
                displayOrder,
                "property-" + id,
                "Квартира " + id,
                "Property " + id,
                "Description",
                "Kestel",
                1,
                2,
                new BigDecimal("60.00"),
                new BigDecimal("100.00"),
                "TRY"
        );
    }
}
