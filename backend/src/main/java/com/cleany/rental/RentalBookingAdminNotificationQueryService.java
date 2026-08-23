package com.cleany.rental;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalBookingAdminNotificationQueryService {

    private final RentalBookingRepository bookingRepository;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public RentalBookingAdminNotification get(long bookingId) {
        RentalBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RentalBookingNotFoundException(bookingId));
        RentalProperty property = booking.getProperty();
        return new RentalBookingAdminNotification(
                booking.getId(),
                apartmentTitle(property),
                booking.getCustomerName(),
                booking.getPhone(),
                booking.getTermType(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRentalMonths(),
                booking.getDurationDays(),
                booking.getBaseDailyPriceSnapshot(),
                booking.getMonthlyPriceSnapshot(),
                booking.getTotalPrice(),
                booking.getCurrency()
        );
    }

    private static String apartmentTitle(RentalProperty property) {
        return property.getTitleRu() == null || property.getTitleRu().isBlank()
                ? property.getTitleEn()
                : property.getTitleRu();
    }
}
