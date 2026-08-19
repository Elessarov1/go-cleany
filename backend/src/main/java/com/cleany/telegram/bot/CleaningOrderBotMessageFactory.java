package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderReportProgress;
import com.cleany.order.CleaningType;
import com.cleany.order.ServiceArea;
import com.cleany.telegram.bot.TelegramBotClient.InlineButton;
import com.cleany.telegram.bot.TelegramBotClient.InlineKeyboard;

@Component
public class CleaningOrderBotMessageFactory {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"));

    public String newOrder(CleaningOrder order) {
        return """
                🧹 Заказ №%d

                📅 %s
                📍 %s
                🏠 %s
                🧽 %s

                💰 К оплате клиентом: %s
                Доход клинера: %s

                Адрес:
                %s

                Телефон:
                %s

                Комментарий:
                %s
                """.formatted(
                order.getId(),
                DATE_FORMATTER.format(order.getRequestedDate()),
                area(order.getArea()),
                apartment(order.getApartmentType(), order.isDuplex()),
                cleaning(order.getCleaningType()),
                price(order.getFinalCustomerPrice(), order.getCurrency()),
                price(
                        order.getBasePrice().subtract(order.getBaseCommission()),
                        order.getCurrency()
                ),
                order.getAddress(),
                order.getPhone(),
                valueOrDash(order.getCustomerComment())
        ).strip();
    }

    public InlineKeyboard newOrderKeyboard(long orderId) {
        return InlineKeyboard.ofRows(List.of(
                InlineButton.callback("✅ Принять", callback("accept", orderId)),
                InlineButton.callback("⏭ Пропустить", callback("skip", orderId))
        ));
    }

    public String acceptedOrder(CleaningOrder order) {
        return """
                ✅ Заказ №%d принят вами

                Клиент: %s
                Телефон: %s
                Адрес: %s
                """.formatted(
                order.getId(),
                order.getCustomerName(),
                order.getPhone(),
                order.getAddress()
        ).strip();
    }

    public InlineKeyboard acceptedOrderKeyboard(CleaningOrder order) {
        return InlineKeyboard.ofRows(
                List.of(InlineButton.url(
                        "💬 Связаться с клиентом",
                        "tg://user?id=" + order.getTelegramUserId()
                )),
                List.of(InlineButton.callback(
                        "🧹 Завершить уборку",
                        callback("finish", order.getId())
                )),
                List.of(InlineButton.callback(
                        "❌ Отменить заказ",
                        callback("cancel", order.getId())
                ))
        );
    }

    public String awaitingPhotoReport(CleaningOrder order) {
        return """
                🧹 Заказ №%d готов к фотоотчёту.

                Отправьте фотографии выполненной уборки.
                Можно отправить несколько фотографий.
                После этого можно добавить комментарий.
                """.formatted(order.getId()).strip();
    }

    public String photoSaved(CleaningOrderReportProgress progress) {
        String commentStatus = progress.commentPresent() ? " Комментарий клинера сохранён." : "";
        return "Фотография сохранена для заказа №"
                + progress.orderId()
                + ". Всего фотографий: "
                + progress.photoCount()
                + "."
                + commentStatus;
    }

    public String commentSaved(CleaningOrderReportProgress progress) {
        if (progress.photoCount() == 0) {
            return "Комментарий сохранён для заказа №"
                    + progress.orderId()
                    + ". Для отправки отчёта добавьте хотя бы одну фотографию.";
        }
        return "Комментарий сохранён для заказа №"
                + progress.orderId()
                + ". Отчёт готов к отправке.";
    }

    public InlineKeyboard reportReadyKeyboard(long orderId) {
        return InlineKeyboard.ofRows(List.of(InlineButton.callback(
                "✅ Отправить отчёт клиенту",
                callback("report", orderId)
        )));
    }

    public String customerReportHeader(CleaningOrder order) {
        return """
                🧹 Уборка завершена ✅

                %s
                %s
                %s
                """.formatted(
                apartment(order.getApartmentType(), order.isDuplex()),
                area(order.getArea()),
                DATE_FORMATTER.format(order.getRequestedDate())
        ).strip();
    }

    public String customerReportComment(CleaningOrder order) {
        return "Комментарий клинера:\n" + valueOrDash(order.getCleanerComment());
    }

    private static String callback(String action, long orderId) {
        return "order:" + action + ":" + orderId;
    }

    private static String area(ServiceArea area) {
        return switch (area) {
            case MAHMUTLAR -> "Махмутлар";
            case KARGICAK -> "Каргыджак";
            case KESTEL -> "Кестель";
        };
    }

    private static String apartment(ApartmentType apartmentType, boolean duplex) {
        String type = switch (apartmentType) {
            case STUDIO -> "1+0 / Студия";
            case ONE_PLUS_ONE -> "1+1";
            case TWO_PLUS_ONE -> "2+1";
            case THREE_PLUS_ONE -> "3+1";
            case FOUR_PLUS_ONE -> "4+1";
        };
        return duplex ? type + " · дуплекс" : type;
    }

    private static String cleaning(CleaningType cleaningType) {
        return switch (cleaningType) {
            case REGULAR -> "Обычная уборка";
            case DEEP -> "Генеральная уборка";
        };
    }

    private static String price(BigDecimal amount, String currency) {
        String value = amount.stripTrailingZeros().toPlainString();
        return "TRY".equals(currency) ? value + " ₺" : value + " " + currency;
    }

    private static String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}
