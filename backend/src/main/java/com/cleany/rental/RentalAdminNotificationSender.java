package com.cleany.rental;

public interface RentalAdminNotificationSender {

    void send(
            RentalBookingAdminEvent.Type type,
            RentalBookingAdminNotification notification
    );
}
