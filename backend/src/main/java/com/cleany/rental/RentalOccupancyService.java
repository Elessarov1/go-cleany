package com.cleany.rental;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalOccupancyService {

    private final RentalPropertyService propertyService;
    private final RentalOccupancyRepository occupancyRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public RentalAvailabilityResponse publicAvailability(
            long propertyId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        propertyService.requirePublishedProperty(propertyId);
        validateRange(fromDate, toDate);
        List<RentalAvailabilityRangeResponse> unavailable = occupancyRepository
                .findOverlapping(propertyId, fromDate, toDate)
                .stream()
                .map(RentalAvailabilityRangeResponse::from)
                .toList();
        return new RentalAvailabilityResponse(propertyId, fromDate, toDate, unavailable);
    }

    @Transactional(readOnly = true)
    public List<RentalOccupancyResponse> adminOccupancies(
            long propertyId,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        propertyService.requireProperty(propertyId);
        validateRange(fromDate, toDate);
        return occupancyRepository.findOverlapping(propertyId, fromDate, toDate).stream()
                .map(RentalOccupancyResponse::from)
                .toList();
    }

    @Transactional
    public RentalOccupancyResponse createManual(
            long propertyId,
            UpsertRentalOccupancyRequest request,
            long adminActorId
    ) {
        propertyService.requirePropertyForUpdate(propertyId);
        requireManualType(request.type());
        validateRange(request.startDate(), request.endDate());
        try {
            RentalOccupancy occupancy = occupancyRepository.create(
                    propertyId,
                    request.startDate(),
                    request.endDate(),
                    request.type(),
                    null,
                    normalizeOptional(request.note()),
                    clock.instant(),
                    adminActorId
            );
            return RentalOccupancyResponse.from(occupancy);
        } catch (DataIntegrityViolationException exception) {
            throw new RentalDatesNotAvailableException();
        }
    }

    @Transactional
    public RentalOccupancyResponse updateManual(
            long propertyId,
            long occupancyId,
            UpsertRentalOccupancyRequest request
    ) {
        propertyService.requirePropertyForUpdate(propertyId);
        requireManualType(request.type());
        validateRange(request.startDate(), request.endDate());
        RentalOccupancy existing = occupancyRepository
                .findByIdAndPropertyId(occupancyId, propertyId)
                .orElseThrow(() -> new RentalOccupancyNotFoundException(occupancyId));
        if (!existing.type().manuallyManaged()) {
            throw new InvalidRentalOccupancyException("Booking occupancy cannot be edited manually");
        }
        try {
            return RentalOccupancyResponse.from(occupancyRepository.updateManual(
                    occupancyId,
                    propertyId,
                    request.startDate(),
                    request.endDate(),
                    request.type(),
                    normalizeOptional(request.note())
            ));
        } catch (DataIntegrityViolationException exception) {
            throw new RentalDatesNotAvailableException();
        }
    }

    @Transactional
    public void deleteManual(long propertyId, long occupancyId) {
        propertyService.requirePropertyForUpdate(propertyId);
        RentalOccupancy existing = occupancyRepository
                .findByIdAndPropertyId(occupancyId, propertyId)
                .orElseThrow(() -> new RentalOccupancyNotFoundException(occupancyId));
        if (!existing.type().manuallyManaged()) {
            throw new InvalidRentalOccupancyException("Booking occupancy cannot be deleted manually");
        }
        if (occupancyRepository.deleteManual(occupancyId, propertyId) != 1) {
            throw new RentalOccupancyNotFoundException(occupancyId);
        }
    }

    private static void requireManualType(RentalOccupancyType type) {
        if (type == null || !type.manuallyManaged()) {
            throw new InvalidRentalOccupancyException(
                    "Admin may create only OWNER_BLOCK, EXTERNAL_BOOKING, or MAINTENANCE occupancy"
            );
        }
    }

    private static void validateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            throw new InvalidRentalDateRangeException();
        }
    }

    private static String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
