package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitCustomerNotification;
import com.cleany.finance.ReferralFinancialProperties;
import com.cleany.rental.RentalBookingCustomerNotification;
import com.cleany.rental.RentalBookingStatus;

class TelegramCustomerNotificationMessageFactoryTest {

    private TelegramCustomerNotificationMessageFactory factory;

    @BeforeEach
    void setUp() {
        var properties = new ReferralFinancialProperties(
                new BigDecimal("0.15"),
                new ReferralFinancialProperties.Customer(
                        new BigDecimal("0.15"),
                        new BigDecimal("2000"),
                        new BigDecimal("0.10"),
                        new BigDecimal("2000")
                ),
                new ReferralFinancialProperties.Partner(
                        new BigDecimal("0.05"),
                        new BigDecimal("2000"),
                        new BigDecimal("0.10"),
                        new BigDecimal("2000")
                )
        );
        factory = new TelegramCustomerNotificationMessageFactory(properties);
    }

    @Test
    void russianCustomer_messageExplainsBothReferralBenefits() {
        String message = factory.referralUnlocked("ALEX7K2", "ru");

        Assertions.assertAll(
                () -> Assertions.assertTrue(message.contains("ALEX7K2")),
                () -> Assertions.assertTrue(message.contains("скидку 15%")),
                () -> Assertions.assertTrue(message.contains("скидку 10%")),
                () -> Assertions.assertTrue(message.contains("успешно завершена"))
        );
    }

    @Test
    void englishCustomer_messageExplainsBothReferralBenefits() {
        String message = factory.referralUnlocked("ALEX7K2", "en-US");

        Assertions.assertAll(
                () -> Assertions.assertTrue(message.contains("ALEX7K2")),
                () -> Assertions.assertTrue(message.contains("15% off")),
                () -> Assertions.assertTrue(message.contains("10% off")),
                () -> Assertions.assertTrue(message.contains("successfully completed"))
        );
    }

    @Test
    void rentalMessages_areLocalizedAndContainBookingSnapshot() {
        var confirmed = new RentalBookingCustomerNotification.Confirmed(
                42L,
                "Квартира у моря",
                "Seaside apartment",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 15),
                new BigDecimal("3150.00"),
                "TRY"
        );
        var cancelled = new RentalBookingCustomerNotification.Cancelled(
                42L,
                "Квартира у моря",
                "Seaside apartment",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 15),
                RentalBookingStatus.CANCELLED_BY_ADMIN
        );

        Assertions.assertAll(
                () -> Assertions.assertTrue(factory.rentalConfirmed(confirmed, "ru").contains(
                        "Бронирование №42 подтверждено"
                )),
                () -> Assertions.assertTrue(factory.rentalConfirmed(confirmed, "en").contains(
                        "Seaside apartment"
                )),
                () -> Assertions.assertTrue(factory.rentalConfirmed(confirmed, "ru").contains(
                        "3150 TRY"
                )),
                () -> Assertions.assertTrue(factory.rentalCancelled(cancelled, "ru").contains(
                        "Бронирование №42 отменено"
                ))
        );
    }

    @Test
    void rentalCleaningBenefitMessage_containsCodeAndCheckoutWindow() {
        var notification = new RentalCleaningBenefitCustomerNotification(
                999L,
                42L,
                "RC23456789",
                LocalDate.of(2026, 9, 12),
                LocalDate.of(2026, 9, 15)
        );

        Assertions.assertAll(
                () -> Assertions.assertTrue(
                        factory.rentalCleaningBenefit(notification, "ru").contains("RC23456789")
                ),
                () -> Assertions.assertTrue(
                        factory.rentalCleaningBenefit(notification, "en").contains("12.09.2026")
                ),
                () -> Assertions.assertTrue(
                        factory.rentalCleaningBenefit(notification, "en").contains("15.09.2026")
                )
        );
    }
}
