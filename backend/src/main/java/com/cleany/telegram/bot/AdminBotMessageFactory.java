package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.cleany.admin.AdminDashboardResponse;
import com.cleany.admin.AdminOrderDetailsResponse;
import com.cleany.admin.AdminOrderEventResponse;
import com.cleany.admin.AdminOrderSummaryResponse;
import com.cleany.admin.AdminStatsResponse;
import com.cleany.configuration.CleaningProperties;
import com.cleany.order.CleaningOrderStatus;
import com.cleany.order.OnsiteIssueReason;
import com.cleany.order.OrderActorType;
import com.cleany.order.OrderEventType;

@Component
public class AdminBotMessageFactory {

    private static final int MAX_HISTORY_EVENTS = 15;
    private static final int MAX_TELEGRAM_MESSAGE_LENGTH = 4000;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.forLanguageTag("ru"));

    private final ZoneId zoneId;

    public AdminBotMessageFactory(CleaningProperties cleaningProperties) {
        zoneId = cleaningProperties.zoneId();
    }

    public String help() {
        return """
                🛠 go-cleany · администратор

                /stats — общая статистика
                /orders — последние 10 заказов
                /order <номер> — заказ и история событий
                """.strip();
    }

    public String stats(AdminStatsResponse stats) {
        return """
                📊 go-cleany · статистика

                Всего заказов: %d
                Создано сегодня: %d
                Ожидают клинера: %d
                В работе: %d
                Выполнено: %d
                Отменено: %d

                Сумма выполненных заказов: %s
                """.formatted(
                stats.totalOrders(),
                stats.ordersToday(),
                stats.newOrders(),
                stats.activeOrders(),
                stats.completedOrders(),
                stats.cancelledOrders(),
                price(stats.completedAmount(), stats.currency())
        ).strip();
    }

    public String recentOrders(AdminDashboardResponse dashboard) {
        if (dashboard.recentOrders().isEmpty()) {
            return "🗂 Заказов пока нет.";
        }

        StringBuilder message = new StringBuilder("🗂 Последние заказы\n");
        for (AdminOrderSummaryResponse order : dashboard.recentOrders()) {
            message.append("\n№")
                    .append(order.id())
                    .append(" · ")
                    .append(status(order.status()))
                    .append(" · ")
                    .append(DATE_FORMATTER.format(order.requestedDate()))
                    .append("\n")
                    .append(order.customerName())
                    .append(" · ")
                    .append(area(order.area().name()))
                    .append(" · ")
                    .append(price(order.price(), order.currency()))
                    .append("\n/order ")
                    .append(order.id())
                    .append("\n");
        }
        return limit(message.toString().strip());
    }

    public String onsiteIssueAlert(long orderId, OnsiteIssueReason reason) {
        return """
                ⚠️ Заказ №%d приостановлен

                Клинер сообщил о проблеме на объекте.
                Причина: %s

                Посмотрите подробности и фотографии в карточке заказа в админке.
                Для просмотра истории в боте: /order %d
                """.formatted(orderId, onsiteIssueReason(reason), orderId).strip();
    }

    public String order(AdminOrderDetailsResponse details) {
        var order = details.order();
        var financial = details.financial();
        StringBuilder message = new StringBuilder()
                .append("🧾 Заказ №").append(order.id()).append("\n\n")
                .append("Статус: ").append(status(order.status())).append("\n")
                .append("Клиент: ").append(order.customerName()).append("\n")
                .append("Телефон: ").append(order.phone()).append("\n")
                .append("Район: ").append(area(order.area().name())).append("\n")
                .append("Адрес: ").append(order.address()).append("\n")
                .append("Дата: ").append(DATE_FORMATTER.format(order.requestedDate())).append("\n")
                .append("К оплате клиентом: ").append(price(financial.finalCustomerPrice(), order.currency())).append("\n")
                .append("Базовая цена: ").append(price(financial.basePrice(), order.currency())).append("\n")
                .append("Скидка клиенту: ").append(price(financial.customerDiscount(), order.currency())).append("\n")
                .append("Выплата партнёру: ").append(price(financial.partnerPayout(), order.currency())).append("\n")
                .append("Доход платформы: ").append(price(financial.platformNet(), order.currency())).append("\n")
                .append("Источник: ").append(financial.acquisitionSource()).append("\n")
                .append("Клинер: ")
                .append(order.cleanerTelegramUserId() == null ? "не назначен" : order.cleanerTelegramUserId())
                .append("\n")
                .append("Фотографий: ").append(details.photoCount()).append("\n\n")
                .append("История:");

        List<AdminOrderEventResponse> events = details.events();
        int firstEvent = Math.max(0, events.size() - MAX_HISTORY_EVENTS);
        for (AdminOrderEventResponse event : events.subList(firstEvent, events.size())) {
            message.append("\n• ")
                    .append(DATE_TIME_FORMATTER.format(event.occurredAt().atZone(zoneId)))
                    .append(" — ")
                    .append(event(event.eventType()))
                    .append(" (")
                    .append(actor(event.actorType()))
                    .append(")");
        }
        return limit(message.toString());
    }

    private static String status(CleaningOrderStatus status) {
        return switch (status) {
            case NEW -> "ожидает клинера";
            case ACCEPTED -> "принят";
            case AWAITING_REPORT -> "ожидает отчёт";
            case ONSITE_ISSUE_REPORTED -> "проблема на объекте";
            case COMPLETED -> "выполнен";
            case REJECTED -> "отклонён";
            case CANCELLED -> "отменён";
        };
    }

    private static String event(OrderEventType eventType) {
        return switch (eventType) {
            case IMPORTED -> "добавлен в журнал";
            case CREATED -> "заказ создан";
            case ACCEPTED -> "заказ принят";
            case REPORT_STARTED -> "начат фотоотчёт";
            case PHOTO_ADDED -> "добавлена фотография";
            case COMMENT_UPDATED -> "обновлён комментарий";
            case ONSITE_ISSUE_REPORTED -> "сообщено о проблеме на объекте";
            case ISSUE_PHOTO_ADDED -> "добавлено фотодоказательство";
            case ISSUE_REPORT_SUBMITTED -> "отчёт о проблеме подтверждён";
            case ISSUE_CUSTOMER_NOTIFIED -> "клиент уведомлён о проблеме";
            case ISSUE_RESOLVED -> "проблема закрыта администратором";
            case COMPLETED -> "заказ выполнен";
            case CANCELLED_BY_CUSTOMER -> "отменён клиентом";
            case CANCELLED_BY_CLEANER -> "отменён клинером";
        };
    }

    private static String actor(OrderActorType actorType) {
        return switch (actorType) {
            case CUSTOMER -> "клиент";
            case CLEANER -> "клинер";
            case ADMIN -> "администратор";
            case SYSTEM -> "система";
        };
    }

    private static String onsiteIssueReason(OnsiteIssueReason reason) {
        return switch (reason) {
            case APARTMENT_SIZE_MISMATCH -> "размер квартиры не соответствует заявке";
            case CLEANING_TYPE_MISMATCH -> "требуется другой тип уборки";
            case HEAVY_CONTAMINATION -> "сильное загрязнение или пост-ремонтное состояние";
            case ACCESS_PROBLEM -> "нет доступа в квартиру";
            case ADDRESS_MISMATCH -> "адрес или объект не соответствует заявке";
            case OTHER -> "другие существенные условия";
        };
    }

    private static String area(String area) {
        return switch (area) {
            case "MAHMUTLAR" -> "Махмутлар";
            case "KARGICAK" -> "Каргыджак";
            case "KESTEL" -> "Кестель";
            default -> area;
        };
    }

    private static String price(BigDecimal amount, String currency) {
        String value = amount.stripTrailingZeros().toPlainString();
        return "TRY".equals(currency) ? value + " ₺" : value + " " + currency;
    }

    private static String limit(String message) {
        return message.length() <= MAX_TELEGRAM_MESSAGE_LENGTH
                ? message
                : message.substring(0, MAX_TELEGRAM_MESSAGE_LENGTH - 1) + "…";
    }
}
