package com.cleany.rental;

import java.util.Objects;

import com.cleany.action.ActionTarget;
import com.cleany.catalog.PlatformService;
import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationType;

public record RentalBookingAdminCustomerNotification(
        RentalBookingAdminEvent.Type eventType,
        RentalBookingAdminNotification booking
) implements CustomerNotification {
    public RentalBookingAdminCustomerNotification {
        eventType = Objects.requireNonNull(eventType);
        booking = Objects.requireNonNull(booking);
    }

    @Override
    public CustomerNotificationType type() {
        return CustomerNotificationType.RENTAL_ADMIN_BOOKING_CHANGED;
    }

    @Override
    public ActionTarget action() {
        return new ActionTarget.OpenAdminTransaction(PlatformService.RENTAL, booking.bookingId());
    }

    @Override
    public String deduplicationKey() {
        return "rental-admin:" + booking.bookingId() + ":" + eventType;
    }
}
