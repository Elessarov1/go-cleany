package com.cleany.rental;

import java.time.Clock;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RentalPropertyServiceTest {

    @Test
    void publicPropertyList_loadsOnlyCoverMediaInOneBatch() {
        RentalPropertyRepository propertyRepository = Mockito.mock(RentalPropertyRepository.class);
        RentalPropertyMediaRepository mediaRepository = Mockito.mock(
                RentalPropertyMediaRepository.class
        );
        RentalProperty first = property(2L);
        RentalProperty second = property(1L);
        RentalPropertyMedia firstMedia = media(first, 21L);
        RentalPropertyMedia secondMedia = media(second, 11L);
        Mockito.when(propertyRepository.findAllByStatusOrderByCreatedAtDesc(
                RentalPropertyStatus.PUBLISHED
        )).thenReturn(List.of(first, second));
        Mockito.when(mediaRepository
                .findAllByProperty_IdInAndCoverTrueOrderByProperty_IdAscIdAsc(List.of(2L, 1L)))
                .thenReturn(List.of(secondMedia, firstMedia));
        RentalPropertyService service = new RentalPropertyService(
                propertyRepository,
                mediaRepository,
                Mockito.mock(RentalPropertySlugGenerator.class),
                Mockito.mock(RentalBookingRepository.class),
                Mockito.mock(RentalOccupancyRepository.class),
                Mockito.mock(RentalPropertyMediaService.class),
                Mockito.mock(org.springframework.context.ApplicationEventPublisher.class),
                Clock.systemUTC()
        );

        List<RentalPropertyResponse> result = service.getPublishedProperties();

        Assertions.assertAll(
                () -> Assertions.assertEquals(List.of(2L, 1L), result.stream()
                        .map(RentalPropertyResponse::id)
                        .toList()),
                () -> Assertions.assertEquals(21L, result.getFirst().media().getFirst().id()),
                () -> Assertions.assertEquals(11L, result.get(1).media().getFirst().id())
        );
        Mockito.verify(mediaRepository)
                .findAllByProperty_IdInAndCoverTrueOrderByProperty_IdAscIdAsc(List.of(2L, 1L));
        Mockito.verify(mediaRepository, Mockito.never())
                .findAllByProperty_IdInOrderByProperty_IdAscSortOrderAscIdAsc(Mockito.anyList());
        Mockito.verify(mediaRepository, Mockito.never())
                .findAllByProperty_IdOrderBySortOrderAscIdAsc(Mockito.anyLong());
    }

    private static RentalProperty property(long id) {
        RentalProperty property = Mockito.mock(RentalProperty.class);
        Mockito.when(property.getId()).thenReturn(id);
        Mockito.when(property.getStatus()).thenReturn(RentalPropertyStatus.PUBLISHED);
        Mockito.when(property.getAmenities()).thenReturn(Collections.emptySet());
        return property;
    }

    private static RentalPropertyMedia media(RentalProperty property, long id) {
        RentalPropertyMedia media = Mockito.mock(RentalPropertyMedia.class);
        Mockito.when(media.getId()).thenReturn(id);
        Mockito.when(media.getProperty()).thenReturn(property);
        return media;
    }
}
