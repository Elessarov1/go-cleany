package com.cleany.rental;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rental")
@RequiredArgsConstructor
public class RentalPropertyController {

    private final RentalPropertyService propertyService;
    private final RentalPropertyMediaService mediaService;
    private final RentalOccupancyService occupancyService;
    private final RentalProperties properties;

    @GetMapping("/configuration")
    public RentalConfigurationResponse getConfiguration() {
        return RentalConfigurationResponse.from(properties);
    }

    @GetMapping("/properties")
    public List<RentalPropertyResponse> getProperties() {
        return propertyService.getPublishedProperties();
    }

    @GetMapping("/properties/{slug}")
    public RentalPropertyResponse getProperty(@PathVariable String slug) {
        return propertyService.getPublishedProperty(slug);
    }

    @GetMapping("/properties/{propertyId}/availability")
    public RentalAvailabilityResponse getAvailability(
            @PathVariable long propertyId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate
    ) {
        return occupancyService.publicAvailability(propertyId, fromDate, toDate);
    }

    @GetMapping("/properties/{propertyId}/media/{mediaId}")
    public ResponseEntity<byte[]> getMedia(
            @PathVariable long propertyId,
            @PathVariable long mediaId
    ) {
        RentalMediaContent media = mediaService.getPublicContent(propertyId, mediaId);
        byte[] content = media.content();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(media.contentType()))
                .contentLength(content.length)
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
                .header("X-Content-Type-Options", "nosniff")
                .body(content);
    }
}
