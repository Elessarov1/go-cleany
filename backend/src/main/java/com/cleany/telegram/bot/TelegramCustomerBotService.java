package com.cleany.telegram.bot;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.cleany.configuration.PublicApplicationProperties;

import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Service
@RequiredArgsConstructor
public class TelegramCustomerBotService {

    private static final Logger log = LoggerFactory.getLogger(TelegramCustomerBotService.class);
    private static final String SUPPORT_EMAIL = "hello@loco-place.com";

    private final TelegramBotClient botClient;
    private final PublicApplicationProperties publicApplicationProperties;

    public boolean handleIfSupported(TelegramUpdate update) {
        TelegramUpdate.Message message = update == null ? null : update.message();
        if (!isAuthenticatedPrivateMessage(message)) {
            return false;
        }

        String command = command(message.text());
        if (!"/start".equals(command) && !"/help".equals(command)) {
            return false;
        }

        boolean english = isEnglish(message.from().languageCode());
        safeSend(
                message.chat().id(),
                "/help".equals(command) ? helpMessage(english) : startMessage(english),
                keyboard(english)
        );
        return true;
    }

    private TelegramBotClient.InlineKeyboard keyboard(boolean english) {
        String baseUrl = publicApplicationProperties.baseUrl();
        return TelegramBotClient.InlineKeyboard.ofRows(
                List.of(TelegramBotClient.InlineButton.webApp(
                        english ? "Open Loco Place" : "Открыть Loco Place",
                        baseUrl + "/"
                )),
                List.of(TelegramBotClient.InlineButton.webApp(
                        english ? "Help" : "Помощь",
                        baseUrl + "/support"
                ))
        );
    }

    private void safeSend(long chatId, String text, TelegramBotClient.InlineKeyboard keyboard) {
        try {
            botClient.sendMessage(chatId, text, keyboard);
        } catch (RuntimeException exception) {
            log.error("Telegram customer command response failed for chat {}", chatId, exception);
        }
    }

    private static boolean isAuthenticatedPrivateMessage(TelegramUpdate.Message message) {
        return message != null
                && message.from() != null
                && message.chat() != null
                && "private".equals(message.chat().type())
                && message.chat().id() == message.from().id();
    }

    private static String command(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String name = text.trim().split("\\s+", 2)[0];
        int botNameSeparator = name.indexOf('@');
        return botNameSeparator > 0 ? name.substring(0, botNameSeparator) : name;
    }

    private static boolean isEnglish(String languageCode) {
        return languageCode != null && languageCode.toLowerCase(Locale.ROOT).startsWith("en");
    }

    private static String startMessage(boolean english) {
        if (english) {
            return """
                    Welcome to Loco Place 👋

                    Cleaning, apartment rentals and airport transfers in Alanya — all in one app.
                    Choose a service or view your current tasks.

                    For general questions: %s
                    """.formatted(SUPPORT_EMAIL).strip();
        }
        return """
                Добро пожаловать в Loco Place 👋

                Уборка, аренда квартир и трансфер в Аланье — в одном приложении.
                Выберите услугу или посмотрите свои текущие задачи.

                По общим вопросам: %s
                """.formatted(SUPPORT_EMAIL).strip();
    }

    private static String helpMessage(boolean english) {
        if (english) {
            return """
                    Open the app using the Loco Place button in the chat menu.

                    For a question about an existing order or booking, open it in Activity and contact support from the task details.

                    For general questions: %s
                    """.formatted(SUPPORT_EMAIL).strip();
        }
        return """
                Откройте приложение кнопкой Loco Place в меню чата.

                Если вопрос связан с заказом или бронированием, откройте его в разделе «История» и обратитесь в поддержку из карточки задачи.

                По общим вопросам: %s
                """.formatted(SUPPORT_EMAIL).strip();
    }
}
