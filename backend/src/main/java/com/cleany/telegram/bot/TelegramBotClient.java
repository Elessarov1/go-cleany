package com.cleany.telegram.bot;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public interface TelegramBotClient {

    void deleteWebhook(boolean dropPendingUpdates);

    List<TelegramUpdate> getUpdates(long offset, int timeoutSeconds);

    void sendMessage(long chatId, String text, InlineKeyboard keyboard);

    void sendPhoto(long chatId, String telegramFileId);

    void answerCallbackQuery(String callbackQueryId, String text, boolean showAlert);

    default void sendMessage(long chatId, String text) {
        sendMessage(chatId, text, InlineKeyboard.empty());
    }

    record InlineButton(String text, String callbackData, String url) {

        private static final int MAX_CALLBACK_DATA_BYTES = 64;

        public InlineButton {
            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException("Telegram button text must not be blank");
            }
            boolean hasCallback = callbackData != null && !callbackData.isBlank();
            boolean hasUrl = url != null && !url.isBlank();
            if (hasCallback == hasUrl) {
                throw new IllegalArgumentException("Telegram button must have exactly one action");
            }
            if (hasCallback
                    && callbackData.getBytes(StandardCharsets.UTF_8).length > MAX_CALLBACK_DATA_BYTES) {
                throw new IllegalArgumentException("Telegram callback data must not exceed 64 bytes");
            }
        }

        public static InlineButton callback(String text, String callbackData) {
            return new InlineButton(text, callbackData, null);
        }

        public static InlineButton url(String text, String url) {
            return new InlineButton(text, null, url);
        }
    }

    record InlineKeyboard(List<List<InlineButton>> rows) {

        public InlineKeyboard {
            rows = rows == null
                    ? List.of()
                    : rows.stream().map(List::copyOf).toList();
        }

        public static InlineKeyboard empty() {
            return new InlineKeyboard(List.of());
        }

        @SafeVarargs
        public static InlineKeyboard ofRows(List<InlineButton>... rows) {
            return new InlineKeyboard(Arrays.stream(rows).map(List::copyOf).toList());
        }
    }
}
