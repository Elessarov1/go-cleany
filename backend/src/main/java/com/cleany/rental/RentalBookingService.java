package com.cleany.rental;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitCancellationService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.order.PhoneNumberNormalizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalBookingService {

    private final RentalBookingRepository bookingRepository;
    private final RentalOccupancyRepository occupancyRepository;
    private final RentalPropertyService propertyService;
    private final RentalPriceService priceService;
    private final RentalStayPolicy stayPolicy;
    private final RentalProperties properties;
    private final RentalCleaningBenefitCancellationService benefitCancellationService;
    private final CustomerAccountService customerAccountService;
    private final PhoneNumberNormalizer phoneNumberNormalizer;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public RentalBookingQuoteResponse quote(RentalBookingQuoteRequest request) {
        RentalProperty property = propertyService.requirePublishedProperty(request.propertyId());
        ResolvedRentalTerm term = stayPolicy.resolve(
                request.termType(),
                request.checkInDate(),
                request.checkOutDate(),
                request.months()
        );
        requireAvailable(request.propertyId(), term.checkInDate(), term.checkOutDate());
        return RentalBookingQuoteResponse.from(
                property,
                term.checkInDate(),
                term.checkOutDate(),
                priceService.calculate(property, term)
        );
    }

    @Transactional
    public RentalBookingResponse create(CreateRentalBookingRequest request) {
        return create(customerAccountService.currentCustomer(), request);
    }

    @Transactional
    public RentalBookingResponse create(CurrentCustomer customer, CreateRentalBookingRequest request) {
        // This lock serializes the per-customer active-booking limit. Date overlap is enforced
        // independently by the rental_occupancy exclusion constraint.
        customerAccountService.lock(customer.customerId());
        RentalProperty property = propertyService.requirePublishedPropertyForUpdate(request.propertyId());
        ResolvedRentalTerm term = stayPolicy.resolve(
                request.termType(),
                request.checkInDate(),
                request.checkOutDate(),
                request.months()
        );
        validateGuests(property, request.guests());
        enforceActiveBookingLimit(customer.customerId(), stayPolicy.today());
        requireAvailable(property.getId(), term.checkInDate(), term.checkOutDate());

        String normalizedPhone = phoneNumberNormalizer.normalize(request.phone());
        customerAccountService.updateNormalizedPhone(customer.customerId(), normalizedPhone);
        RentalPriceQuote quote = priceService.calculate(property, term);
        RentalBooking booking = bookingRepository.saveAndFlush(new RentalBooking(
                customer.customerId(),
                customer.externalIdentityId(),
                property,
                term,
                customer.displayName(),
                normalizedPhone,
                request.guests(),
                request.comment(),
                quote,
                clock.instant()
        ));

        try {
            occupancyRepository.create(
                    property.getId(),
                    term.checkInDate(),
                    term.checkOutDate(),
                    RentalOccupancyType.BOOKING,
                    booking.getId(),
                    null,
                    clock.instant(),
                    null
            );
        } catch (DataIntegrityViolationException exception) {
            throw new RentalDatesNotAvailableException();
        }

        eventPublisher.publishEvent(new RentalBookingCustomerEvent.Confirmed(
                booking.getId(),
                booking.getCustomerId(),
                booking.getCommunicationIdentityId()
        ));
        eventPublisher.publishEvent(new RentalBookingAdminEvent(
                booking.getId(),
                RentalBookingAdminEvent.Type.CREATED
        ));
        return RentalBookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public List<RentalBookingResponse> currentCustomerBookings() {
        long customerId = customerAccountService.currentCustomer().customerId();
        return bookingRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(RentalBookingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RentalBookingResponse currentCustomerBooking(long bookingId) {
        long customerId = customerAccountService.currentCustomer().customerId();
        return RentalBookingResponse.from(requireCustomerBooking(bookingId, customerId));
    }

    @Transactional
    public RentalBookingResponse cancelCurrentCustomerBooking(long bookingId) {
        return cancel(customerAccountService.currentCustomer(), bookingId);
    }

    @Transactional
    public RentalBookingResponse cancel(CurrentCustomer customer, long bookingId) {
        customerAccountService.lock(customer.customerId());
        RentalBooking booking = requireCustomerBooking(bookingId, customer.customerId());
        propertyService.requirePropertyForUpdate(booking.getProperty().getId());
        booking.cancelByCustomer(stayPolicy.today(), clock.instant());
        benefitCancellationService.revokeAvailableFor(booking);
        deleteBookingOccupancy(bookingId);
        eventPublisher.publishEvent(new RentalBookingCustomerEvent.Cancelled(
                booking.getId(),
                booking.getCustomerId(),
                booking.getCommunicationIdentityId(),
                booking.getStatus()
        ));
        eventPublisher.publishEvent(new RentalBookingAdminEvent(
                booking.getId(),
                RentalBookingAdminEvent.Type.CANCELLED_BY_CUSTOMER
        ));
        return RentalBookingResponse.from(booking);
    }

    private RentalBooking requireCustomerBooking(long bookingId, long customerId) {
        return bookingRepository.findByIdAndCustomerId(bookingId, customerId)
                .orElseThrow(() -> new RentalBookingNotFoundException(bookingId));
    }

    private void enforceActiveBookingLimit(long customerId, LocalDate today) {
        long activeBookings = bookingRepository.countByCustomerIdAndStatusAndCheckOutDateAfter(
                customerId,
                RentalBookingStatus.CONFIRMED,
                today
        );
        if (activeBookings >= properties.maxActiveBookingsPerCustomer()) {
            throw new RentalActiveBookingLimitExceededException(
                    properties.maxActiveBookingsPerCustomer()
            );
        }
    }

    private void requireAvailable(long propertyId, LocalDate startDate, LocalDate endDate) {
        if (occupancyRepository.overlaps(propertyId, startDate, endDate)) {
            throw new RentalDatesNotAvailableException();
        }
    }

    private static void validateGuests(RentalProperty property, int guests) {
        if (guests <= 0 || property.getMaxGuests() == null || guests > property.getMaxGuests()) {
            throw new InvalidRentalBookingException(
                    "Guest count exceeds the rental property capacity"
            );
        }
    }

    private void deleteBookingOccupancy(long bookingId) {
        if (occupancyRepository.deleteByBookingId(bookingId) != 1) {
            throw new IllegalStateException("Booking occupancy not found: " + bookingId);
        }
    }
}
