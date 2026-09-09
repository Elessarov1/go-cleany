package com.cleany.rental;

import java.time.Duration;
import java.time.LocalDate;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import com.cleany.catalog.PlatformService;
import com.cleany.catalog.PlatformServiceAccessService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rental")
@RequiredArgsConstructor
@Validated
public class RentalPropertyController {

    private static final Duration PUBLIC_MEDIA_CACHE_DURATION = Duration.ofDays(365);

    private final RentalPropertyService propertyService;
    private final RentalPropertyMediaService mediaService;
    private final RentalOccupancyService occupancyService;
    private final RentalProperties properties;
    private final RentalStayPolicy stayPolicy;
    private final RentalBookingService bookingService;
    private final PlatformServiceAccessService serviceAccessService;

    @GetMapping("/configuration")
    public RentalConfigurationResponse getConfiguration() {
        return RentalConfigurationResponse.from(properties, stayPolicy.today());
    }

    @GetMapping("/properties/{slug}")
    public RentalPropertyResponse getProperty(@PathVariable String slug) {
        requirePublicFlow();
        return propertyService.getPublishedProperty(slug);
    }

    @GetMapping("/properties/{propertyId}/availability")
    public RentalAvailabilityResponse getAvailability(
            @PathVariable long propertyId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate
    ) {
        requirePublicFlow();
        return occupancyService.publicAvailability(propertyId, fromDate, toDate);
    }

    @GetMapping("/properties/{propertyId}/quote")
    public ResponseEntity<RentalQuoteResponse> quote(
            @PathVariable long propertyId,
            @RequestParam RentalTermType termType,
            @RequestParam LocalDate checkInDate,
            @RequestParam(required = false) LocalDate checkOutDate,
            @RequestParam(required = false) Integer months,
            @RequestParam @Min(1) @Max(100) int guests
    ) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(bookingService.quote(
                        propertyId,
                        termType,
                        checkInDate,
                        checkOutDate,
                        months,
                        guests
                ));
    }

    @GetMapping("/properties/{propertyId}/media/{mediaId}")
    public ResponseEntity<byte[]> getMedia(
            @PathVariable long propertyId,
            @PathVariable long mediaId,
            @RequestParam(name = "v", required = false) Long version
    ) {
        RentalMediaContent media = mediaService.getPublicContent(propertyId, mediaId);
        return mediaResponse(propertyId, mediaId, version, media);
    }

    @GetMapping("/properties/{propertyId}/media/{mediaId}/{variant:card|thumbnail}")
    public ResponseEntity<byte[]> getMediaVariant(
            @PathVariable long propertyId,
            @PathVariable long mediaId,
            @PathVariable String variant,
            @RequestParam(name = "v", required = false) Long version
    ) {
        RentalMediaContent media = mediaService.getPublicContent(
                propertyId,
                mediaId,
                variant.equals("card") ? RentalMediaVariant.CARD : RentalMediaVariant.THUMBNAIL
        );
        return mediaResponse(propertyId, mediaId, version, media);
    }

    private static ResponseEntity<byte[]> mediaResponse(
            long propertyId,
            long mediaId,
            Long version,
            RentalMediaContent media
    ) {
        if (version != null && version != media.mediaAssetId()) {
            throw new RentalPropertyMediaNotFoundException(propertyId, mediaId);
        }
        byte[] content = media.content();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(media.contentType()))
                .contentLength(content.length)
                .cacheControl(version == null
                        ? CacheControl.noCache()
                        : CacheControl.maxAge(PUBLIC_MEDIA_CACHE_DURATION)
                                .cachePublic()
                                .immutable())
                .header("X-Content-Type-Options", "nosniff")
                .body(content);
    }

    private void requirePublicFlow() {
        serviceAccessService.requireCanStartCurrentCustomerFlow(PlatformService.RENTAL);
    }
}
