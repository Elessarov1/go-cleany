package com.cleany.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.cleany.admin.AdminAccessService;
import com.cleany.admin.AdminQueryService;
import com.cleany.configuration.AdminProperties;
import com.cleany.order.OnsiteIssueReason;
import com.cleany.order.OrderNotFoundException;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Service
public class TelegramAdminBotService {

    private static final Logger log = LoggerFactory.getLogger(TelegramAdminBotService.class);

    private final AdminAccessService accessService;
    private final AdminProperties adminProperties;
    private final AdminQueryService queryService;
    private final AdminBotMessageFactory messageFactory;
    private final TelegramBotClient botClient;

    public TelegramAdminBotService(
            AdminAccessService accessService,
            AdminProperties adminProperties,
            AdminQueryService queryService,
            AdminBotMessageFactory messageFactory,
            TelegramBotClient botClient
    ) {
        this.accessService = accessService;
        this.adminProperties = adminProperties;
        this.queryService = queryService;
        this.messageFactory = messageFactory;
        this.botClient = botClient;
    }

    public void notifyOnsiteIssue(long orderId, OnsiteIssueReason reason) {
        String message = messageFactory.onsiteIssueAlert(orderId, reason);
        adminProperties.telegramIds().forEach(adminId -> safeSend(adminId, message));
    }

    public boolean handleIfSupported(long telegramUserId, String text) {
        AdminCommand command = parse(text);
        if (command == null) {
            return false;
        }
        if (!accessService.isAdmin(telegramUserId)) {
            safeSend(telegramUserId, "Эта команда доступна только администратору.");
            return true;
        }

        switch (command.name()) {
            case "/admin" -> safeSend(telegramUserId, messageFactory.help());
            case "/stats" -> safeSend(
                    telegramUserId,
                    messageFactory.stats(queryService.getDashboard(telegramUserId, 1).stats())
            );
            case "/orders" -> safeSend(
                    telegramUserId,
                    messageFactory.recentOrders(queryService.getDashboard(telegramUserId, 10))
            );
            case "/order" -> sendOrder(telegramUserId, command.argument());
            default -> throw new IllegalStateException("Unsupported admin command: " + command.name());
        }
        return true;
    }

    private void sendOrder(long telegramUserId, String argument) {
        if (argument == null || !argument.matches("[1-9][0-9]*")) {
            safeSend(telegramUserId, "Используйте команду в формате /order <номер заказа>.");
            return;
        }
        try {
            long orderId = Long.parseLong(argument);
            safeSend(telegramUserId, messageFactory.order(queryService.getOrder(telegramUserId, orderId)));
        } catch (NumberFormatException | OrderNotFoundException exception) {
            safeSend(telegramUserId, "Заказ с таким номером не найден.");
        }
    }

    private void safeSend(long chatId, String text) {
        try {
            botClient.sendMessage(chatId, text, TelegramBotClient.InlineKeyboard.empty());
        } catch (TelegramBotApiException exception) {
            log.error("Telegram admin response delivery failed for chat {}", chatId, exception);
        }
    }

    private static AdminCommand parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String[] parts = text.trim().split("\\s+", 2);
        String name = parts[0];
        int botNameSeparator = name.indexOf('@');
        if (botNameSeparator > 0) {
            name = name.substring(0, botNameSeparator);
        }
        if (!name.equals("/admin")
                && !name.equals("/stats")
                && !name.equals("/orders")
                && !name.equals("/order")) {
            return null;
        }
        return new AdminCommand(name, parts.length == 2 ? parts[1].trim() : null);
    }

    private record AdminCommand(String name, String argument) {
    }
}
