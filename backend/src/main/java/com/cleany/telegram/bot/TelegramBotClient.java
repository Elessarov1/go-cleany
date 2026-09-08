package com.cleany.telegram.bot;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface TelegramBotClient {

    void deleteWebhook(boolean dropPendingUpdates);

    List<TelegramUpdate> getUpdates(long offset, int timeoutSeconds);

    void sendMessage(long chatId, String text, InlineKeyboard keyboard);

    void sendPhoto(long chatId, String telegramFileId);

    byte[] downloadFile(String telegramFileId);

    void answerCallbackQuery(String callbackQueryId, String text, boolean showAlert);

    void setName(String name, String languageCode);

    void setDescription(String description, String languageCode);

    void setShortDescription(String shortDescription, String languageCode);

    void setCommands(List<BotCommand> commands, String languageCode);

    void setDefaultMenuButton(String text, String webAppUrl);

    default void sendMessage(long chatId, String text) {
        sendMessage(chatId, text, InlineKeyboard.empty());
    }

    record BotCommand(String command, String description) {

        public BotCommand {
            if (command == null || !command.matches("[a-z0-9_]{1,32}")) {
                throw new IllegalArgumentException("Telegram bot command is invalid");
            }
            if (description == null || description.isBlank() || description.length() > 256) {
                throw new IllegalArgumentException("Telegram bot command description is invalid");
            }
        }
    }

    record InlineButton(String text, String callbackData, String url, String webAppUrl) {

        private static final int MAX_CALLBACK_DATA_BYTES = 64;

        public InlineButton {
            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException("Telegram button text must not be blank");
            }
            boolean hasCallback = callbackData != null && !callbackData.isBlank();
            boolean hasUrl = url != null && !url.isBlank();
            boolean hasWebApp = webAppUrl != null && !webAppUrl.isBlank();
            if ((hasCallback ? 1 : 0) + (hasUrl ? 1 : 0) + (hasWebApp ? 1 : 0) != 1) {
                throw new IllegalArgumentException("Telegram button must have exactly one action");
            }
            if (hasCallback
                    && callbackData.getBytes(StandardCharsets.UTF_8).length > MAX_CALLBACK_DATA_BYTES) {
                throw new IllegalArgumentException("Telegram callback data must not exceed 64 bytes");
            }
        }

        public static InlineButton callback(String text, String callbackData) {
            return new InlineButton(text, callbackData, null, null);
        }

        public static InlineButton url(String text, String url) {
            return new InlineButton(text, null, url, null);
        }

        public static InlineButton webApp(String text, String webAppUrl) {
            return new InlineButton(text, null, null, webAppUrl);
        }
    }

    record InlineKeyboard(List<List<InlineButton>> rows) {

        public InlineKeyboard {
            rows = rows == null
                    ? Collections.emptyList()
                    : rows.stream().map(List::copyOf).toList();
        }

        public static InlineKeyboard empty() {
            return new InlineKeyboard(Collections.emptyList());
        }

        @SafeVarargs
        public static InlineKeyboard ofRows(List<InlineButton>... rows) {
            return new InlineKeyboard(Arrays.stream(rows).map(List::copyOf).toList());
        }
    }
}
