package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.cleany.crossservice.rentalcleaning.RentalCleaningBenefitCustomerNotification;
import com.cleany.finance.ReferralFinancialProperties;
import com.cleany.rental.RentalBookingCustomerNotification;
import com.cleany.reminder.ReminderCustomerNotification;
import com.cleany.transfer.TransferAdminNewRequestNotification;
import com.cleany.transfer.TransferBookingCustomerNotification;
import com.cleany.transfer.TransferDirection;
import com.cleany.support.SupportCaseAdminNotification;
import com.cleany.support.SupportCaseCategory;

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

                You now have a personal loco-cleaning benefit for checkout cleaning.
                Promo code: %s

                Use it for a cleaning scheduled between %s and %s.
                Open loco-cleaning in Loco Place to book your cleaning.
                """.formatted(
                        notification.rentalBookingId(),
                        notification.code(),
                        DATE_FORMAT.format(notification.earliestCleaningDate()),
                        DATE_FORMAT.format(notification.checkOutDate())
                ).strip()
                : """
                🧹 Ваша аренда №%d началась

                Вам доступна персональная выгода loco-cleaning для уборки перед выездом.
                Промокод: %s

                Используйте его для уборки с %s по %s.
                Откройте loco-cleaning в Loco Place, чтобы оформить заказ.
                """.formatted(
                        notification.rentalBookingId(),
                        notification.code(),
                        DATE_FORMAT.format(notification.earliestCleaningDate()),
                        DATE_FORMAT.format(notification.checkOutDate())
                ).strip();
    }

    public String reminder(ReminderCustomerNotification notification, String languageCode) {
        boolean english = isEnglish(languageCode);
        if (notification instanceof ReminderCustomerNotification.CleaningRepeat cleaning) {
            return english
                    ? "🧹 Cleaning reminder\n\nYou chose to repeat your cleaning on %s. Open Loco Place to review the details."
                            .formatted(DATE_FORMAT.format(cleaning.scheduledDate()))
                    : "🧹 Напоминание об уборке\n\nВы выбрали повтор уборки на %s. Откройте Loco Place, чтобы проверить детали."
                            .formatted(DATE_FORMAT.format(cleaning.scheduledDate()));
        }
        if (notification instanceof ReminderCustomerNotification.RentalCheckoutTransfer rental) {
            return english
                    ? "🚘 Transfer for rental checkout\n\nYour checkout is on %s. You can arrange an airport transfer in Loco Place."
                            .formatted(DATE_FORMAT.format(rental.checkOutDate()))
                    : "🚘 Трансфер к выезду из аренды\n\nВыезд запланирован на %s. В Loco Place можно оформить трансфер в аэропорт."
                            .formatted(DATE_FORMAT.format(rental.checkOutDate()));
        }
        if (notification instanceof ReminderCustomerNotification.TransferUpcoming transfer) {
            String route = route(transfer.direction(), transfer.airportCode(), english);
            return english
                    ? "🚘 Upcoming transfer\n\n%s at %s\n%s"
                            .formatted(
                                    DATE_FORMAT.format(transfer.pickupDate()),
                                    transfer.pickupTime(),
                                    route
                            )
                    : "🚘 Скоро ваш трансфер\n\n%s в %s\n%s"
                            .formatted(
                                    DATE_FORMAT.format(transfer.pickupDate()),
                                    transfer.pickupTime(),
                                    route
                            );
        }
        throw new IllegalArgumentException("Unsupported reminder notification: " + notification);
    }

    public String transferCustomer(
            TransferBookingCustomerNotification notification,
            String languageCode
    ) {
        boolean english = isEnglish(languageCode);
        String route = route(
                notification.direction(),
                notification.airportCode(),
                english
        );
        String state = english
                ? switch (notification.status()) {
                    case REQUESTED -> "Request received";
                    case CONFIRMED -> "Transfer confirmed";
                    case REJECTED -> "Transfer rejected";
                    case CANCELLED -> "Transfer cancelled";
                    case COMPLETED -> "Transfer completed";
                }
                : switch (notification.status()) {
                    case REQUESTED -> "Заявка получена";
                    case CONFIRMED -> "Трансфер подтверждён";
                    case REJECTED -> "Трансфер отклонён";
                    case CANCELLED -> "Трансфер отменён";
                    case COMPLETED -> "Трансфер завершён";
                };
        return """
                🚘 %s · №%d

                %s · %s
                %s
                """.formatted(
                state,
                notification.bookingId(),
                DATE_FORMAT.format(notification.pickupDate()),
                notification.pickupTime(),
                route
        ).strip();
    }

    public String transferAdminRequested(
            TransferAdminNewRequestNotification notification,
            String languageCode
    ) {
        boolean english = isEnglish(languageCode);
        return """
                🚘 %s · №%d

                %s · %s
                %s
                """.formatted(
                english ? "New transfer request" : "Новая заявка на трансфер",
                notification.bookingId(),
                DATE_FORMAT.format(notification.pickupDate()),
                notification.pickupTime(),
                route(notification.direction(), notification.airportCode(), english)
        ).strip();
    }

    public String supportCaseCreated(
            SupportCaseAdminNotification notification,
            String languageCode
    ) {
        boolean english = isEnglish(languageCode);
        String service = english
                ? switch (notification.service()) {
                    case CLEANING -> "Cleaning";
                    case RENTAL -> "Rental";
                    case TRANSFER -> "Transfer";
                }
                : switch (notification.service()) {
                    case CLEANING -> "Уборка";
                    case RENTAL -> "Аренда";
                    case TRANSFER -> "Трансфер";
                };
        return english
                ? "New support case #%d\n\n%s · transaction #%d\nCategory: %s".formatted(
                        notification.caseId(),
                        service,
                        notification.sourceEntityId(),
                        supportCategory(notification.category(), true)
                )
                : "Новое обращение #%d\n\n%s · задача №%d\nКатегория: %s".formatted(
                        notification.caseId(),
                        service,
                        notification.sourceEntityId(),
                        supportCategory(notification.category(), false)
                );
    }

    private static String supportCategory(SupportCaseCategory category, boolean english) {
        return english
                ? switch (category) {
                    case PROVIDER_LATE -> "Provider is late";
                    case PROVIDER_NO_SHOW -> "Provider did not arrive";
                    case QUALITY_PROBLEM -> "Quality problem";
                    case BOOKING_PROBLEM -> "Booking problem";
                    case OTHER -> "Other";
                }
                : switch (category) {
                    case PROVIDER_LATE -> "Исполнитель опаздывает";
                    case PROVIDER_NO_SHOW -> "Исполнитель не приехал";
                    case QUALITY_PROBLEM -> "Проблема с качеством";
                    case BOOKING_PROBLEM -> "Проблема с бронированием";
                    case OTHER -> "Другое";
                };
    }

    private static String route(TransferDirection direction, String airportCode, boolean english) {
        String alanya = english ? "Alanya" : "Аланья";
        return direction == TransferDirection.FROM_AIRPORT
                ? airportCode + " → " + alanya
                : alanya + " → " + airportCode;
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
