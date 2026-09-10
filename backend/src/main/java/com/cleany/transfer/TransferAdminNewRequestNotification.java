package com.cleany.transfer;

import java.time.LocalDate;
import java.time.LocalTime;

import com.cleany.action.ActionTarget;
import com.cleany.catalog.PlatformService;
import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationType;

public record TransferAdminNewRequestNotification(
        long bookingId,
        TransferDirection direction,
        String airportCode,
        LocalDate pickupDate,
        LocalTime pickupTime
) implements CustomerNotification {

    @Override
    public CustomerNotificationType type() {
        return CustomerNotificationType.TRANSFER_ADMIN_REQUESTED;
    }

    @Override
    public ActionTarget action() {
        return new ActionTarget.OpenAdminTransaction(PlatformService.TRANSFER, bookingId);
    }

    @Override
    public String deduplicationKey() {
        return "transfer-booking:" + bookingId + ":admin-requested";
    }
}
