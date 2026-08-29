package com.cleany.transfer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferBookingNotificationQueryService {

    private final TransferBookingRepository bookingRepository;

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public TransferBookingCustomerNotification customer(
            long bookingId,
            TransferBookingCustomerNotification.Type type
    ) {
        TransferBooking booking = requireBooking(bookingId);
        return new TransferBookingCustomerNotification(
                booking.getId(), type, booking.getDirection(), booking.getAirportCodeSnapshot(),
                booking.getVehicleNameRuSnapshot(), booking.getVehicleNameEnSnapshot(),
                booking.getPickupDate(), booking.getPickupTime(), booking.getPriceAmount(),
                booking.getPriceCurrency()
        );
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public TransferAdminNewRequestNotification adminRequested(long bookingId) {
        TransferBooking booking = requireBooking(bookingId);
        return new TransferAdminNewRequestNotification(
                booking.getId(), booking.getDirection(), booking.getAirportCodeSnapshot(),
                booking.getPickupDate(), booking.getPickupTime()
        );
    }

    private TransferBooking requireBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new TransferBookingNotFoundException(bookingId));
    }
}
