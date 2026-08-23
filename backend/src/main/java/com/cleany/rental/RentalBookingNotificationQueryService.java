package com.cleany.rental;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalBookingNotificationQueryService {

    private final RentalBookingRepository bookingRepository;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public RentalBookingCustomerNotification confirmed(long bookingId) {
        RentalBooking booking = requireBooking(bookingId);
        RentalProperty property = booking.getProperty();
        return new RentalBookingCustomerNotification.Confirmed(
                booking.getId(),
                property.getTitleRu(),
                property.getTitleEn(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getTotalPrice(),
                booking.getCurrency()
        );
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public RentalBookingCustomerNotification cancelled(long bookingId) {
        RentalBooking booking = requireBooking(bookingId);
        RentalProperty property = booking.getProperty();
        return new RentalBookingCustomerNotification.Cancelled(
                booking.getId(),
                property.getTitleRu(),
                property.getTitleEn(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus()
        );
    }

    private RentalBooking requireBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RentalBookingNotFoundException(bookingId));
    }
}
