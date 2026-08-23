package com.cleany.rental;

import java.time.Clock;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalPropertyService {

    private final RentalPropertyRepository propertyRepository;
    private final RentalPropertyMediaRepository mediaRepository;
    private final RentalPropertySlugGenerator slugGenerator;
    private final RentalBookingRepository bookingRepository;
    private final RentalOccupancyRepository occupancyRepository;
    private final RentalPropertyMediaService mediaService;
    private final Clock clock;

    @Transactional
    public RentalPropertyResponse createDraft() {
        RentalProperty property = propertyRepository.save(new RentalProperty(clock.instant()));
        return adminResponse(property);
    }

    @Transactional
    public RentalPropertyResponse update(long propertyId, RentalPropertyDetails details) {
        RentalProperty property = requirePropertyForUpdate(propertyId);
        boolean published = property.getStatus() == RentalPropertyStatus.PUBLISHED;
        if (property.getSlug() == null && details.titleEn() != null) {
            property.assignSlug(slugGenerator.generate(details.titleEn(), propertyId));
        }
        property.updateDetails(details, clock.instant());
        if (published) {
            property.publish(mediaRepository.existsByProperty_Id(propertyId), clock.instant());
        }
        return adminResponse(property);
    }

    @Transactional
    public RentalPropertyResponse publish(long propertyId) {
        RentalProperty property = requireProperty(propertyId);
        List<RentalPropertyMedia> media = media(propertyId);
        if (!media.isEmpty() && media.stream().noneMatch(RentalPropertyMedia::isCover)) {
            media.getFirst().setCover(true);
        }
        property.publish(!media.isEmpty(), clock.instant());
        return adminResponse(property, media);
    }

    @Transactional
    public RentalPropertyResponse archive(long propertyId) {
        RentalProperty property = requireProperty(propertyId);
        property.archive(clock.instant());
        return adminResponse(property);
    }

    @Transactional
    public RentalPropertyResponse unpublish(long propertyId) {
        RentalProperty property = requirePropertyForUpdate(propertyId);
        property.unpublish(clock.instant());
        return adminResponse(property);
    }

    @Transactional
    public void deleteDraft(long propertyId) {
        RentalProperty property = requirePropertyForUpdate(propertyId);
        if (property.getStatus() != RentalPropertyStatus.DRAFT) {
            throw new RentalPropertyCannotBeDeletedException(
                    propertyId,
                    "only an unpublished draft can be deleted"
            );
        }
        if (bookingRepository.existsByProperty_Id(propertyId)) {
            throw new RentalPropertyCannotBeDeletedException(
                    propertyId,
                    "booking history must be retained"
            );
        }
        occupancyRepository.deleteManualByPropertyId(propertyId);
        mediaService.deleteAllForProperty(propertyId);
        propertyRepository.delete(property);
    }

    @Transactional(readOnly = true)
    public List<RentalPropertyResponse> getPublishedProperties() {
        return propertyRepository.findAllByStatusOrderByCreatedAtDesc(RentalPropertyStatus.PUBLISHED)
                .stream()
                .map(this::publicResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RentalPropertyResponse getPublishedProperty(String slug) {
        RentalProperty property = propertyRepository
                .findBySlugAndStatus(slug.toLowerCase(java.util.Locale.ROOT), RentalPropertyStatus.PUBLISHED)
                .orElseThrow(() -> new RentalPropertyNotFoundException(slug));
        return publicResponse(property);
    }

    @Transactional(readOnly = true)
    public List<RentalPropertyResponse> getAdminProperties() {
        return propertyRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::adminResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RentalPropertyResponse getAdminProperty(long propertyId) {
        return adminResponse(requireProperty(propertyId));
    }

    RentalProperty requireProperty(long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RentalPropertyNotFoundException(propertyId));
    }

    RentalProperty requirePublishedProperty(long propertyId) {
        return propertyRepository.findByIdAndStatus(propertyId, RentalPropertyStatus.PUBLISHED)
                .orElseThrow(() -> new RentalPropertyNotAvailableException(propertyId));
    }

    RentalProperty requirePropertyForUpdate(long propertyId) {
        return propertyRepository.findByIdForUpdate(propertyId)
                .orElseThrow(() -> new RentalPropertyNotFoundException(propertyId));
    }

    RentalProperty requirePublishedPropertyForUpdate(long propertyId) {
        return propertyRepository
                .findByIdAndStatusForUpdate(propertyId, RentalPropertyStatus.PUBLISHED)
                .orElseThrow(() -> new RentalPropertyNotAvailableException(propertyId));
    }

    private RentalPropertyResponse publicResponse(RentalProperty property) {
        return response(property, media(property.getId()), false);
    }

    private RentalPropertyResponse adminResponse(RentalProperty property) {
        return adminResponse(property, media(property.getId()));
    }

    private RentalPropertyResponse adminResponse(
            RentalProperty property,
            List<RentalPropertyMedia> media
    ) {
        return response(property, media, true);
    }

    private RentalPropertyResponse response(
            RentalProperty property,
            List<RentalPropertyMedia> media,
            boolean admin
    ) {
        long propertyId = property.getId();
        List<RentalPropertyMediaResponse> responses = media.stream()
                .map(item -> RentalPropertyMediaResponse.from(
                        item,
                        (admin ? "/api/v1/admin/rental/properties/" : "/api/v1/rental/properties/")
                                + propertyId
                                + "/media/"
                                + item.getId()
                ))
                .toList();
        return RentalPropertyResponse.from(property, responses);
    }

    private List<RentalPropertyMedia> media(long propertyId) {
        return mediaRepository.findAllByProperty_IdOrderBySortOrderAscIdAsc(propertyId);
    }
}
