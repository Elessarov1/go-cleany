package com.cleany.notification;

import org.springframework.stereotype.Component;

import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitCustomerNotification;
import com.cleany.order.CleaningOrderCustomerNotification;
import com.cleany.reminder.ReminderCustomerNotification;
import com.cleany.rental.RentalBookingCustomerNotification;
import com.cleany.rental.RentalBookingAdminCustomerNotification;
import com.cleany.support.SupportCaseAdminNotification;
import com.cleany.transfer.TransferAdminNewRequestNotification;
import com.cleany.transfer.TransferBookingCustomerNotification;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class CustomerNotificationPayloadCodec {
    private final ObjectMapper objectMapper;

    public CustomerNotification decode(CustomerNotificationType type, String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("Notification payload is unavailable");
        }
        Class<? extends CustomerNotification> target = switch (type) {
            case CLEANING_ORDER_ACCEPTED -> CleaningOrderCustomerNotification.Accepted.class;
            case CLEANING_ORDER_CANCELLED -> CleaningOrderCustomerNotification.Cancelled.class;
            case CLEANING_ORDER_COMPLETED -> CleaningOrderCustomerNotification.Completed.class;
            case CLEANING_ONSITE_ISSUE_REPORTED -> CleaningOrderCustomerNotification.OnsiteIssueReported.class;
            case RENTAL_BOOKING_CONFIRMED -> RentalBookingCustomerNotification.Confirmed.class;
            case RENTAL_BOOKING_CANCELLED -> RentalBookingCustomerNotification.Cancelled.class;
            case RENTAL_ADMIN_BOOKING_CHANGED -> RentalBookingAdminCustomerNotification.class;
            case TRANSFER_REQUESTED, TRANSFER_CONFIRMED, TRANSFER_REJECTED,
                    TRANSFER_CANCELLED, TRANSFER_COMPLETED -> TransferBookingCustomerNotification.class;
            case TRANSFER_ADMIN_REQUESTED -> TransferAdminNewRequestNotification.class;
            case SUPPORT_CASE_CREATED -> SupportCaseAdminNotification.class;
            case REFERRAL_UNLOCKED -> ReferralUnlockedCustomerNotification.class;
            case RENTAL_CLEANING_BENEFIT_AVAILABLE -> RentalCleaningBenefitCustomerNotification.class;
            case CLEANING_REPEAT_REMINDER -> ReminderCustomerNotification.CleaningRepeat.class;
            case RENTAL_CHECKOUT_TRANSFER_REMINDER -> ReminderCustomerNotification.RentalCheckoutTransfer.class;
            case TRANSFER_UPCOMING_REMINDER -> ReminderCustomerNotification.TransferUpcoming.class;
        };
        try {
            return objectMapper.readValue(payload, target);
        } catch (JacksonException exception) {
            throw new IllegalArgumentException("Stored notification payload is invalid", exception);
        }
    }
}
