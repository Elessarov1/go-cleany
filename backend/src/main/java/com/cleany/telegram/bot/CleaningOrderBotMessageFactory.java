package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderCustomerNotification;
import com.cleany.order.CleaningOrderReportProgress;
import com.cleany.order.CleaningType;
import com.cleany.order.OnsiteIssueProgress;
import com.cleany.order.OnsiteIssueReason;
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

    public String customerOrderAccepted() {
        return "Ваш заказ на уборку подтверждён ✅";
    }

    public String customerOrderCancelled() {
        return "Заказ отменён.";
    }

    public InlineKeyboard acceptedOrderKeyboard(CleaningOrder order, long customerTelegramUserId) {
        return InlineKeyboard.ofRows(
                List.of(InlineButton.url(
                        "💬 Связаться с клиентом",
                        "tg://user?id=" + customerTelegramUserId
                )),
                List.of(InlineButton.callback(
                        "🧹 Завершить уборку",
                        callback("finish", order.getId())
                )),
                List.of(InlineButton.callback(
                        "⚠️ Сообщить о проблеме на объекте",
                        callback("issue", order.getId())
                )),
                List.of(InlineButton.callback(
                        "❌ Отменить заказ",
                        callback("cancel", order.getId())
                ))
        );
    }

    public InlineKeyboard onsiteIssueReasonKeyboard(long orderId) {
        return new InlineKeyboard(List.of(
                reasonRow(orderId, OnsiteIssueReason.APARTMENT_SIZE_MISMATCH),
                reasonRow(orderId, OnsiteIssueReason.CLEANING_TYPE_MISMATCH),
                reasonRow(orderId, OnsiteIssueReason.HEAVY_CONTAMINATION),
                reasonRow(orderId, OnsiteIssueReason.ACCESS_PROBLEM),
                reasonRow(orderId, OnsiteIssueReason.ADDRESS_MISMATCH),
                reasonRow(orderId, OnsiteIssueReason.OTHER)
        ));
    }

    public String onsiteIssueStarted(OnsiteIssueProgress progress) {
        return """
                ⚠️ Проблема на объекте для заказа №%d

                Причина: %s

                Отправьте от 3 до 8 подтверждающих фотографий.
                Затем отправьте обязательный комментарий отдельным сообщением
                или подписью к фотографии.

                До подтверждения заказ останется принятым.
                """.formatted(progress.orderId(), onsiteIssueReason(progress.reason())).strip();
    }

    public String onsiteIssueProgress(OnsiteIssueProgress progress) {
        String comment = progress.commentPresent() ? "сохранён" : "ещё не добавлен";
        String readiness = progress.readyToSubmit()
                ? "Отчёт готов к подтверждению."
                : "Для подтверждения нужны минимум 3 фотографии и комментарий.";
        return "Фото: " + progress.photoCount() + ". Комментарий: " + comment + ".\n" + readiness;
    }

    public InlineKeyboard onsiteIssueSubmitKeyboard(OnsiteIssueProgress progress) {
        if (!progress.readyToSubmit()) {
            return InlineKeyboard.empty();
        }
        return InlineKeyboard.ofRows(List.of(InlineButton.callback(
                "⚠️ Подтвердить отчёт о проблеме",
                callback("issue_submit", progress.orderId())
        )));
    }

    public String customerOnsiteIssueReport(OnsiteIssueReason reason, String comment) {
        return """
                Клинер сообщил о проблеме на объекте.

                Причина:
                %s

                Комментарий:
                %s
                """.formatted(onsiteIssueReason(reason), comment).strip();
    }

    public String customerOnsiteIssuePaused() {
        return "Заказ приостановлен. Мы рассмотрим ситуацию и свяжемся с вами.";
    }

    public String onsiteIssueReason(OnsiteIssueReason reason) {
        return switch (reason) {
            case APARTMENT_SIZE_MISMATCH -> "Размер квартиры не соответствует заявке";
            case CLEANING_TYPE_MISMATCH -> "Требуется другой тип уборки";
            case HEAVY_CONTAMINATION -> "Сильное загрязнение или пост-ремонтное состояние";
            case ACCESS_PROBLEM -> "Нет доступа в квартиру";
            case ADDRESS_MISMATCH -> "Адрес или объект не соответствует заявке";
            case OTHER -> "Другие существенные условия";
        };
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

    public String customerReportHeader(CleaningOrderCustomerNotification.Completed notification) {
        return """
                🧹 Уборка завершена ✅

                %s
                %s
                %s
                """.formatted(
                apartment(notification.apartmentType(), notification.duplex()),
                area(notification.area()),
                DATE_FORMATTER.format(notification.requestedDate())
        ).strip();
    }

    public String customerReportComment(CleaningOrderCustomerNotification.Completed notification) {
        return "Комментарий клинера:\n" + valueOrDash(notification.cleanerComment());
    }

    private static String callback(String action, long orderId) {
        return "order:" + action + ":" + orderId;
    }

    private List<InlineButton> reasonRow(long orderId, OnsiteIssueReason reason) {
        return List.of(InlineButton.callback(
                onsiteIssueReason(reason),
                "order:issue_reason:" + reason + ":" + orderId
        ));
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
