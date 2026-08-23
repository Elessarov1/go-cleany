package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.cleany.rental.RentalBookingAdminEvent;
import com.cleany.rental.RentalBookingAdminNotification;
import com.cleany.rental.RentalTermType;

@Component
public class TelegramRentalAdminMessageFactory {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public String format(
            RentalBookingAdminEvent.Type type,
            RentalBookingAdminNotification booking
    ) {
        String title = type == RentalBookingAdminEvent.Type.CREATED
                ? "🏠 Новое бронирование go-renty"
                : "❌ Бронирование go-renty отменено клиентом";
        return booking.termType() == RentalTermType.MONTHLY
                ? monthly(title, booking)
                : dateRange(title, booking);
    }

    private static String dateRange(
            String title,
            RentalBookingAdminNotification booking
    ) {
        return """
                %s

                Бронирование №%d
                Квартира: %s
                Клиент: %s
                Телефон: %s
                Период: %s — %s
                Длительность: %d дн.
                Цена: %s / сутки
                Итого: %s
                """.formatted(
                title,
                booking.bookingId(),
                booking.apartment(),
                booking.customerName(),
                booking.phone(),
                DATE_FORMATTER.format(booking.checkInDate()),
                DATE_FORMATTER.format(booking.checkOutDate()),
                booking.durationDays(),
                money(booking.dailyPrice(), booking.currency()),
                money(booking.totalPrice(), booking.currency())
        ).strip();
    }

    private static String monthly(
            String title,
            RentalBookingAdminNotification booking
    ) {
        return """
                %s

                Бронирование №%d
                Квартира: %s
                Клиент: %s
                Телефон: %s
                Начало: %s
                Срок: %d мес.
                Ожидаемый выезд: %s
                Цена: %s / месяц
                Итого: %s
                """.formatted(
                title,
                booking.bookingId(),
                booking.apartment(),
                booking.customerName(),
                booking.phone(),
                DATE_FORMATTER.format(booking.checkInDate()),
                booking.rentalMonths(),
                DATE_FORMATTER.format(booking.checkOutDate()),
                money(booking.monthlyPrice(), booking.currency()),
                money(booking.totalPrice(), booking.currency())
        ).strip();
    }

    private static String money(BigDecimal amount, String currency) {
        String[] parts = amount.stripTrailingZeros().toPlainString().split("\\.", 2);
        String integer = parts[0].replaceAll("\\B(?=(\\d{3})+(?!\\d))", " ");
        String decimal = parts.length == 2 ? "," + parts[1] : "";
        return integer + decimal + " " + currency;
    }
}
