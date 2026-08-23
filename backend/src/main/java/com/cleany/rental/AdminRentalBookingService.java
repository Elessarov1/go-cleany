package com.cleany.rental;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.admin.AdminAccessService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminRentalBookingService {

    private final AdminAccessService accessService;
    private final RentalBookingRepository bookingRepository;
    private final RentalOccupancyRepository occupancyRepository;
    private final RentalPropertyService propertyService;
    private final RentalStayPolicy stayPolicy;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<AdminRentalBookingResponse> getBookings(
            RentalBookingStatus status,
            Long propertyId,
            RentalBookingTimeFilter timeFilter
    ) {
        return getBookings(accessService.requireCurrentAdmin(), status, propertyId, timeFilter);
    }

    @Transactional(readOnly = true)
    public List<AdminRentalBookingResponse> getBookings(
            long adminActorId,
            RentalBookingStatus status,
            Long propertyId,
            RentalBookingTimeFilter timeFilter
    ) {
        accessService.requireAdmin(adminActorId);
        RentalBookingTimeFilter effectiveTimeFilter = timeFilter == null
                ? RentalBookingTimeFilter.ALL
                : timeFilter;
        LocalDate today = stayPolicy.today();
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(booking -> status == null || booking.getStatus() == status)
                .filter(booking -> propertyId == null
                        || booking.getProperty().getId().equals(propertyId))
                .filter(booking -> matchesTimeFilter(booking, effectiveTimeFilter, today))
                .map(AdminRentalBookingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminRentalBookingResponse getBooking(long bookingId) {
        return getBooking(accessService.requireCurrentAdmin(), bookingId);
    }

    @Transactional(readOnly = true)
    public AdminRentalBookingResponse getBooking(long adminActorId, long bookingId) {
        accessService.requireAdmin(adminActorId);
        return AdminRentalBookingResponse.from(requireBooking(bookingId));
    }

    @Transactional
    public AdminRentalBookingResponse cancel(
            long bookingId,
            AdminCancelRentalBookingRequest request
    ) {
        return cancel(accessService.requireCurrentAdmin(), bookingId, request);
    }

    @Transactional
    public AdminRentalBookingResponse cancel(
            long adminActorId,
            long bookingId,
            AdminCancelRentalBookingRequest request
    ) {
        accessService.requireAdmin(adminActorId);
        RentalBooking booking = requireBooking(bookingId);
        propertyService.requirePropertyForUpdate(booking.getProperty().getId());
        booking.cancelByAdmin(request.reason(), clock.instant());
        if (occupancyRepository.deleteByBookingId(bookingId) != 1) {
            throw new IllegalStateException("Booking occupancy not found: " + bookingId);
        }
        if (request.keepDatesUnavailable()) {
            try {
                occupancyRepository.create(
                        booking.getProperty().getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        RentalOccupancyType.OWNER_BLOCK,
                        null,
                        ownerBlockNote(bookingId, request.reason()),
                        clock.instant(),
                        adminActorId
                );
            } catch (DataIntegrityViolationException exception) {
                throw new RentalDatesNotAvailableException();
            }
        }
        eventPublisher.publishEvent(new RentalBookingCustomerEvent.Cancelled(
                booking.getId(),
                booking.getCustomerId(),
                booking.getCommunicationIdentityId(),
                booking.getStatus()
        ));
        return AdminRentalBookingResponse.from(booking);
    }

    @Transactional
    public AdminRentalBookingResponse complete(long bookingId) {
        return complete(accessService.requireCurrentAdmin(), bookingId);
    }

    @Transactional
    public AdminRentalBookingResponse complete(long adminActorId, long bookingId) {
        accessService.requireAdmin(adminActorId);
        RentalBooking booking = requireBooking(bookingId);
        booking.complete(stayPolicy.today(), clock.instant());
        return AdminRentalBookingResponse.from(booking);
    }

    private RentalBooking requireBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RentalBookingNotFoundException(bookingId));
    }

    private static boolean matchesTimeFilter(
            RentalBooking booking,
            RentalBookingTimeFilter timeFilter,
            LocalDate today
    ) {
        return switch (timeFilter) {
            case ALL -> true;
            case FUTURE -> booking.getCheckOutDate().isAfter(today);
            case PAST -> !booking.getCheckOutDate().isAfter(today);
        };
    }

    private static String ownerBlockNote(long bookingId, String reason) {
        String normalizedReason = reason == null || reason.isBlank() ? null : reason.trim();
        return normalizedReason == null
                ? "Dates retained after admin cancellation of booking " + bookingId
                : normalizedReason;
    }
}
