package com.cleany.rental;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

class RentalPropertyControllerTest {

    @Test
    void versionedPublicMediaIsImmutableAndRejectsStaleVersion() {
        RentalPropertyMediaService mediaService = Mockito.mock(RentalPropertyMediaService.class);
        RentalPropertyController controller = controller(mediaService);
        RentalMediaContent content = new RentalMediaContent(
                37,
                "image/jpeg",
                "content".getBytes(StandardCharsets.UTF_8)
        );
        Mockito.when(mediaService.getPublicContent(3, 5)).thenReturn(content);

        var response = controller.getMedia(3, 5, 37L);

        Assertions.assertAll(
                () -> Assertions.assertTrue(response.getHeaders()
                        .getCacheControl()
                        .contains("immutable")),
                () -> Assertions.assertEquals(
                        "nosniff",
                        response.getHeaders().getFirst("X-Content-Type-Options")
                ),
                () -> Assertions.assertThrows(
                        RentalPropertyMediaNotFoundException.class,
                        () -> controller.getMedia(3, 5, 36L)
                )
        );
    }

    @Test
    void legacyUnversionedPublicMediaMustBeRevalidated() {
        RentalPropertyMediaService mediaService = Mockito.mock(RentalPropertyMediaService.class);
        RentalPropertyController controller = controller(mediaService);
        Mockito.when(mediaService.getPublicContent(3, 5)).thenReturn(new RentalMediaContent(
                37,
                "image/jpeg",
                "content".getBytes(StandardCharsets.UTF_8)
        ));

        var response = controller.getMedia(3, 5, null);

        Assertions.assertEquals(
                "no-cache",
                response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL)
        );
    }

    private static RentalPropertyController controller(RentalPropertyMediaService mediaService) {
        return new RentalPropertyController(
                Mockito.mock(RentalPropertyService.class),
                mediaService,
                Mockito.mock(RentalOccupancyService.class),
                Mockito.mock(RentalProperties.class)
        );
    }
}
