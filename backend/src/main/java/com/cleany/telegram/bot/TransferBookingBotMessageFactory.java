package com.cleany.telegram.bot;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.cleany.transfer.TransferBookingResponse;
import com.cleany.transfer.TransferDirection;

@Component
public class TransferBookingBotMessageFactory {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    public String newBooking(TransferBookingResponse booking) {
        String route = booking.direction() == TransferDirection.FROM_AIRPORT
                ? booking.airportCode() + " → Аланья"
                : "Аланья → " + booking.airportCode();
        return """
                🚘 Новый Loco Transfer №%d

                %s · %s
                %s
                %s
                Пассажиров: %d · Багаж: %d
                Стоимость: %s %s
                """.formatted(
                booking.id(), DATE.format(booking.pickupDate()), TIME.format(booking.pickupTime()),
                route, booking.vehicleNameRu(), booking.passengerCount(), booking.luggageCount(),
                booking.priceAmount().stripTrailingZeros().toPlainString(), booking.priceCurrency()
        ).trim();
    }

    public TelegramBotClient.InlineKeyboard acceptKeyboard(long bookingId) {
        return TelegramBotClient.InlineKeyboard.ofRows(List.of(
                TelegramBotClient.InlineButton.callback(
                        "Принять трансфер",
                        "transfer:accept:" + bookingId
                )
        ));
    }

    public String accepted(TransferBookingResponse booking) {
        String flight = booking.flightNumber() == null ? "—" : booking.flightNumber();
        return """
                ✅ Трансфер №%d закреплён за вами

                Клиент: %s
                Телефон: %s
                Адрес: %s
                Рейс: %s
                """.formatted(
                booking.id(), booking.customerName(), booking.phone(), booking.address(), flight
        ).trim();
    }
}
