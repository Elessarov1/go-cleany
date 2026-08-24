package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitCustomerNotification;
import com.cleany.finance.ReferralFinancialProperties;
import com.cleany.rental.RentalBookingCustomerNotification;

@Component
public class TelegramCustomerNotificationMessageFactory {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final ReferralFinancialProperties financialProperties;

    public TelegramCustomerNotificationMessageFactory(ReferralFinancialProperties financialProperties) {
        this.financialProperties = financialProperties;
    }

    public String referralUnlocked(String referralCode, String languageCode) {
        var customer = financialProperties.customer();
        String friendDiscount = percentage(customer.friendDiscountRate());
        String referrerReward = percentage(customer.referrerRewardRate());
        return isEnglish(languageCode)
                ? english(referralCode, friendDiscount, referrerReward)
                : russian(referralCode, friendDiscount, referrerReward);
    }

    public String rentalConfirmed(
            RentalBookingCustomerNotification.Confirmed notification,
            String languageCode
    ) {
        String title = localizedTitle(notification.titleRu(), notification.titleEn(), languageCode);
        return isEnglish(languageCode)
                ? """
                🏠 Booking #%d confirmed

                %s
                %s — %s
                Total: %s %s
                """.formatted(
                        notification.bookingId(),
                        title,
                        DATE_FORMAT.format(notification.checkInDate()),
                        DATE_FORMAT.format(notification.checkOutDate()),
                        notification.totalPrice().stripTrailingZeros().toPlainString(),
                        notification.currency()
                ).strip()
                : """
                🏠 Бронирование №%d подтверждено

                %s
                %s — %s
                Итого: %s %s
                """.formatted(
                        notification.bookingId(),
                        title,
                        DATE_FORMAT.format(notification.checkInDate()),
                        DATE_FORMAT.format(notification.checkOutDate()),
                        notification.totalPrice().stripTrailingZeros().toPlainString(),
                        notification.currency()
                ).strip();
    }

    public String rentalCancelled(
            RentalBookingCustomerNotification.Cancelled notification,
            String languageCode
    ) {
        String title = localizedTitle(notification.titleRu(), notification.titleEn(), languageCode);
        return isEnglish(languageCode)
                ? """
                Booking #%d cancelled

                %s
                %s — %s
                """.formatted(
                        notification.bookingId(),
                        title,
                        DATE_FORMAT.format(notification.checkInDate()),
                        DATE_FORMAT.format(notification.checkOutDate())
                ).strip()
                : """
                Бронирование №%d отменено

                %s
                %s — %s
                """.formatted(
                        notification.bookingId(),
                        title,
                        DATE_FORMAT.format(notification.checkInDate()),
                        DATE_FORMAT.format(notification.checkOutDate())
                ).strip();
    }

    public String rentalCleaningBenefit(
            RentalCleaningBenefitCustomerNotification notification,
            String languageCode
    ) {
        return isEnglish(languageCode)
                ? """
                🧹 Your rental #%d has started

                You now have a personal go-cleany benefit for checkout cleaning.
                Promo code: %s

                Use it for a cleaning scheduled between %s and %s.
                Open go-cleany in the application to book your cleaning.
                """.formatted(
                        notification.rentalBookingId(),
                        notification.code(),
                        DATE_FORMAT.format(notification.earliestCleaningDate()),
                        DATE_FORMAT.format(notification.checkOutDate())
                ).strip()
                : """
                🧹 Ваша аренда №%d началась

                Вам доступна персональная выгода go-cleany для уборки перед выездом.
                Промокод: %s

                Используйте его для уборки с %s по %s.
                Откройте go-cleany в приложении, чтобы оформить заказ.
                """.formatted(
                        notification.rentalBookingId(),
                        notification.code(),
                        DATE_FORMAT.format(notification.earliestCleaningDate()),
                        DATE_FORMAT.format(notification.checkOutDate())
                ).strip();
    }

    private static String russian(String code, String friendDiscount, String referrerReward) {
        return """
                🎁 Подарите другу скидку на уборку

                Ваш персональный код: %s

                Друг получит скидку %s на свою первую уборку.

                После того как его первая уборка будет успешно завершена,
                вы получите скидку %s на свой следующий заказ.

                Просто отправьте этот код другу.
                """.formatted(code, friendDiscount, referrerReward).strip();
    }

    private static String english(String code, String friendDiscount, String referrerReward) {
        return """
                🎁 Give a friend a cleaning discount

                Your personal code: %s

                Your friend will receive %s off their first cleaning.

                After their first cleaning is successfully completed,
                you will receive %s off your next order.

                Simply send this code to your friend.
                """.formatted(code, friendDiscount, referrerReward).strip();
    }

    private static String percentage(BigDecimal rate) {
        return rate.movePointRight(2).stripTrailingZeros().toPlainString() + "%";
    }

    private static boolean isEnglish(String languageCode) {
        return languageCode != null
                && languageCode.toLowerCase(Locale.ROOT).startsWith("en");
    }

    private static String localizedTitle(String titleRu, String titleEn, String languageCode) {
        return isEnglish(languageCode) ? titleEn : titleRu;
    }
}
