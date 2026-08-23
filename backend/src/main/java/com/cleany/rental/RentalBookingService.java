package com.cleany.rental;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final CustomerAccountService customerAccountService;
    private final PhoneNumberNormalizer phoneNumberNormalizer;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public RentalBookingQuoteResponse quote(RentalBookingQuoteRequest request) {
        RentalProperty property = propertyService.requirePublishedProperty(request.propertyId());
        int durationDays = stayPolicy.validate(request.checkInDate(), request.checkOutDate());
        requireAvailable(request.propertyId(), request.checkInDate(), request.checkOutDate());
        return RentalBookingQuoteResponse.from(
                property,
                request.checkInDate(),
                request.checkOutDate(),
                priceService.calculate(property, durationDays)
        );
    }

    @Transactional
    public RentalBookingResponse create(CreateRentalBookingRequest request) {
        return create(customerAccountService.currentCustomer(), request);
    }

    @Transactional
    public RentalBookingResponse create(CurrentCustomer customer, CreateRentalBookingRequest request) {
        customerAccountService.lock(customer.customerId());
        RentalProperty property = propertyService.requirePublishedPropertyForUpdate(request.propertyId());
        int durationDays = stayPolicy.validate(request.checkInDate(), request.checkOutDate());
        validateGuests(property, request.guests());
        enforceActiveBookingLimit(customer.customerId(), stayPolicy.today());
        requireAvailable(property.getId(), request.checkInDate(), request.checkOutDate());

        String normalizedPhone = phoneNumberNormalizer.normalize(request.phone());
        customerAccountService.updatePhone(customer.customerId(), normalizedPhone);
        RentalPriceQuote quote = priceService.calculate(property, durationDays);
        RentalBooking booking = bookingRepository.saveAndFlush(new RentalBooking(
                customer.customerId(),
                customer.externalIdentityId(),
                property,
                request.checkInDate(),
                request.checkOutDate(),
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
                    request.checkInDate(),
                    request.checkOutDate(),
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
        deleteBookingOccupancy(bookingId);
        eventPublisher.publishEvent(new RentalBookingCustomerEvent.Cancelled(
                booking.getId(),
                booking.getCustomerId(),
                booking.getCommunicationIdentityId(),
                booking.getStatus()
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
