package com.cleany.rental;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cleany.admin.AdminAccessService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/rental/properties")
@RequiredArgsConstructor
public class AdminRentalPropertyController {

    private final AdminAccessService accessService;
    private final RentalPropertyService propertyService;
    private final RentalPropertyMediaService mediaService;
    private final RentalOccupancyService occupancyService;

    @GetMapping
    public List<RentalPropertyResponse> getProperties() {
        accessService.requireCurrentAdmin();
        return propertyService.getAdminProperties();
    }

    @PostMapping
    public ResponseEntity<RentalPropertyResponse> createDraft() {
        accessService.requireCurrentAdmin();
        RentalPropertyResponse property = propertyService.createDraft();
        return ResponseEntity
                .created(URI.create("/api/v1/admin/rental/properties/" + property.id()))
                .body(property);
    }

    @GetMapping("/{propertyId}")
    public RentalPropertyResponse getProperty(@PathVariable long propertyId) {
        accessService.requireCurrentAdmin();
        return propertyService.getAdminProperty(propertyId);
    }

    @PutMapping("/{propertyId}")
    public RentalPropertyResponse updateProperty(
            @PathVariable long propertyId,
            @Valid @RequestBody UpdateRentalPropertyRequest request
    ) {
        accessService.requireCurrentAdmin();
        return propertyService.update(propertyId, request.toDetails());
    }

    @PostMapping("/{propertyId}/publish")
    public RentalPropertyResponse publish(@PathVariable long propertyId) {
        accessService.requireCurrentAdmin();
        return propertyService.publish(propertyId);
    }

    @PostMapping("/{propertyId}/archive")
    public RentalPropertyResponse archive(@PathVariable long propertyId) {
        accessService.requireCurrentAdmin();
        return propertyService.archive(propertyId);
    }

    @PostMapping(path = "/{propertyId}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RentalPropertyResponse addMedia(
            @PathVariable long propertyId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean cover
    ) throws IOException {
        accessService.requireCurrentAdmin();
        mediaService.add(propertyId, file.getBytes(), cover);
        return propertyService.getAdminProperty(propertyId);
    }

    @DeleteMapping("/{propertyId}/media/{mediaId}")
    public RentalPropertyResponse removeMedia(
            @PathVariable long propertyId,
            @PathVariable long mediaId
    ) {
        accessService.requireCurrentAdmin();
        mediaService.remove(propertyId, mediaId);
        return propertyService.getAdminProperty(propertyId);
    }

    @PostMapping("/{propertyId}/media/{mediaId}/cover")
    public RentalPropertyResponse setCover(
            @PathVariable long propertyId,
            @PathVariable long mediaId
    ) {
        accessService.requireCurrentAdmin();
        mediaService.setCover(propertyId, mediaId);
        return propertyService.getAdminProperty(propertyId);
    }

    @PutMapping("/{propertyId}/media/order")
    public RentalPropertyResponse reorderMedia(
            @PathVariable long propertyId,
            @Valid @RequestBody ReorderRentalPropertyMediaRequest request
    ) {
        accessService.requireCurrentAdmin();
        mediaService.reorder(propertyId, request.mediaIds());
        return propertyService.getAdminProperty(propertyId);
    }

    @GetMapping("/{propertyId}/media/{mediaId}")
    public ResponseEntity<byte[]> getMedia(
            @PathVariable long propertyId,
            @PathVariable long mediaId
    ) {
        accessService.requireCurrentAdmin();
        RentalMediaContent media = mediaService.getAdminContent(propertyId, mediaId);
        byte[] content = media.content();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(media.contentType()))
                .contentLength(content.length)
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .body(content);
    }

    @GetMapping("/{propertyId}/occupancies")
    public List<RentalOccupancyResponse> getOccupancies(
            @PathVariable long propertyId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate
    ) {
        accessService.requireCurrentAdmin();
        return occupancyService.adminOccupancies(propertyId, fromDate, toDate);
    }

    @PostMapping("/{propertyId}/occupancies")
    public ResponseEntity<RentalOccupancyResponse> createOccupancy(
            @PathVariable long propertyId,
            @Valid @RequestBody UpsertRentalOccupancyRequest request
    ) {
        long adminActorId = accessService.requireCurrentAdmin();
        RentalOccupancyResponse occupancy = occupancyService.createManual(
                propertyId,
                request,
                adminActorId
        );
        return ResponseEntity
                .created(URI.create(
                        "/api/v1/admin/rental/properties/"
                                + propertyId
                                + "/occupancies/"
                                + occupancy.id()
                ))
                .body(occupancy);
    }

    @PutMapping("/{propertyId}/occupancies/{occupancyId}")
    public RentalOccupancyResponse updateOccupancy(
            @PathVariable long propertyId,
            @PathVariable long occupancyId,
            @Valid @RequestBody UpsertRentalOccupancyRequest request
    ) {
        accessService.requireCurrentAdmin();
        return occupancyService.updateManual(propertyId, occupancyId, request);
    }

    @DeleteMapping("/{propertyId}/occupancies/{occupancyId}")
    public ResponseEntity<Void> deleteOccupancy(
            @PathVariable long propertyId,
            @PathVariable long occupancyId
    ) {
        accessService.requireCurrentAdmin();
        occupancyService.deleteManual(propertyId, occupancyId);
        return ResponseEntity.noContent().build();
    }
}
