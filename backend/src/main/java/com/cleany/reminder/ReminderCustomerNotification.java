package com.cleany.reminder;

import java.time.LocalDate;
import java.time.LocalTime;

import com.cleany.action.ActionTarget;
import com.cleany.catalog.PlatformService;
import com.cleany.crossservice.rentaltransfer.RentalTransferContextType;
import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationType;
import com.cleany.transfer.TransferDirection;

public sealed interface ReminderCustomerNotification extends CustomerNotification {

    record CleaningRepeat(
            long orderId,
            LocalDate scheduledDate
    ) implements ReminderCustomerNotification {

        @Override
        public CustomerNotificationType type() {
            return CustomerNotificationType.CLEANING_REPEAT_REMINDER;
        }

        @Override
        public ActionTarget action() {
            return new ActionTarget.RepeatCleaning(orderId);
        }

        @Override
        public String deduplicationKey() {
            return "reminder:cleaning-repeat:" + orderId;
        }
    }

    record RentalCheckoutTransfer(
            long rentalBookingId,
            LocalDate checkOutDate
    ) implements ReminderCustomerNotification {

        @Override
        public CustomerNotificationType type() {
            return CustomerNotificationType.RENTAL_CHECKOUT_TRANSFER_REMINDER;
        }

        @Override
        public ActionTarget action() {
            return new ActionTarget.StartRentalTransfer(rentalBookingId, RentalTransferContextType.CHECKOUT);
        }

        @Override
        public String deduplicationKey() {
            return "reminder:rental-checkout-transfer:" + rentalBookingId;
        }
    }

    record TransferUpcoming(
            long bookingId,
            LocalDate pickupDate,
            LocalTime pickupTime,
            TransferDirection direction,
            String airportCode
    ) implements ReminderCustomerNotification {

        @Override
        public CustomerNotificationType type() {
            return CustomerNotificationType.TRANSFER_UPCOMING_REMINDER;
        }

        @Override
        public ActionTarget action() {
            return new ActionTarget.OpenTransaction(PlatformService.TRANSFER, bookingId);
        }

        @Override
        public String deduplicationKey() {
            return "reminder:transfer-upcoming:" + bookingId;
        }
    }
}
