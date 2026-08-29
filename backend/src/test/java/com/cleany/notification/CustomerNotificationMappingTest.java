package com.cleany.notification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitCustomerNotification;
import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningOrderCustomerNotification;
import com.cleany.order.OnsiteIssueReason;
import com.cleany.order.ServiceArea;
import com.cleany.rental.RentalBookingCustomerNotification;
import com.cleany.rental.RentalBookingStatus;
import com.cleany.transfer.TransferAdminNewRequestNotification;
import com.cleany.transfer.TransferBookingCustomerNotification;
import com.cleany.transfer.TransferDirection;

class CustomerNotificationMappingTest {

    @ParameterizedTest
    @MethodSource("notifications")
    void notificationMapsToStableSemanticMetadata(
            CustomerNotification notification,
            CustomerNotificationType expectedType,
            String expectedPath,
            String expectedDeduplicationKey
    ) {
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedType, notification.type()),
                () -> Assertions.assertEquals(expectedPath, notification.targetPath()),
                () -> Assertions.assertEquals(expectedDeduplicationKey, notification.deduplicationKey())
        );
    }

    private static Stream<Arguments> notifications() {
        LocalDate date = LocalDate.of(2026, 9, 3);
        return Stream.of(
                Arguments.of(new CleaningOrderCustomerNotification.Accepted(11L),
                        CustomerNotificationType.CLEANING_ORDER_ACCEPTED,
                        "/cleaning/orders/11", "cleaning-order:11:accepted"),
                Arguments.of(new CleaningOrderCustomerNotification.Cancelled(11L),
                        CustomerNotificationType.CLEANING_ORDER_CANCELLED,
                        "/cleaning/orders/11", "cleaning-order:11:cancelled"),
                Arguments.of(new CleaningOrderCustomerNotification.Completed(
                                11L, ApartmentType.ONE_PLUS_ONE, false, ServiceArea.MAHMUTLAR,
                                date, null, Collections.emptyList(), 7),
                        CustomerNotificationType.CLEANING_ORDER_COMPLETED,
                        "/cleaning/orders/11", "cleaning-order:11:completed"),
                Arguments.of(new CleaningOrderCustomerNotification.OnsiteIssueReported(
                                11L, OnsiteIssueReason.ACCESS_PROBLEM, null, Collections.emptyList()),
                        CustomerNotificationType.CLEANING_ONSITE_ISSUE_REPORTED,
                        "/cleaning/orders/11", "cleaning-order:11:onsite-issue"),
                Arguments.of(new RentalBookingCustomerNotification.Confirmed(
                                22L, "Квартира", "Apartment", date, date.plusDays(2),
                                BigDecimal.TEN, "TRY"),
                        CustomerNotificationType.RENTAL_BOOKING_CONFIRMED,
                        "/rent/bookings/22", "rental-booking:22:confirmed"),
                Arguments.of(new RentalBookingCustomerNotification.Cancelled(
                                22L, "Квартира", "Apartment", date, date.plusDays(2),
                                RentalBookingStatus.CANCELLED_BY_CUSTOMER),
                        CustomerNotificationType.RENTAL_BOOKING_CANCELLED,
                        "/rent/bookings/22", "rental-booking:22:cancelled"),
                Arguments.of(new TransferBookingCustomerNotification(
                                44L, TransferBookingCustomerNotification.Type.CONFIRMED,
                                TransferDirection.TO_AIRPORT, "AYT", "Минивэн", "Minivan",
                                date, LocalTime.of(10, 30), new BigDecimal("3000.00"), "TRY"),
                        CustomerNotificationType.TRANSFER_CONFIRMED,
                        "/transfer/bookings/44", "transfer-booking:44:confirmed"),
                Arguments.of(new TransferAdminNewRequestNotification(
                                44L, TransferDirection.TO_AIRPORT, "AYT", date, LocalTime.of(10, 30)),
                        CustomerNotificationType.TRANSFER_ADMIN_REQUESTED,
                        "/admin/transfer/bookings/44", "transfer-booking:44:admin-requested"),
                Arguments.of(new ReferralUnlockedCustomerNotification("ALEX7K2"),
                        CustomerNotificationType.REFERRAL_UNLOCKED,
                        "/cleaning/orders", "referral:ALEX7K2:unlocked"),
                Arguments.of(new RentalCleaningBenefitCustomerNotification(
                                33L, 22L, "CLEAN7", date, date.plusDays(3)),
                        CustomerNotificationType.RENTAL_CLEANING_BENEFIT_AVAILABLE,
                        "/rent/bookings/22", "rental-cleaning-benefit:33:available")
        );
    }
}
