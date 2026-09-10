package com.cleany.telegram.bot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.cleany.authentication.NativeAuthenticationException;
import com.cleany.authentication.TelegramNativeLoginService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
public class TelegramNativeLoginBotService {
    private final TelegramNativeLoginService loginService;
    private final TelegramBotClient botClient;

    public boolean handleIfSupported(TelegramUpdate update) {
        TelegramUpdate.Message message = update == null ? null : update.message();
        String parameter = startParameter(message == null ? null : message.text());
        if (parameter == null || !parameter.matches("^(login|link)_.+")) return false;
        if (message.from() == null || message.chat() == null
                || !"private".equals(message.chat().type())
                || message.chat().id() != message.from().id()) return true;
        try {
            boolean approved = loginService.approve(parameter, message.from());
            boolean linking = parameter.startsWith("link_");
            botClient.sendMessage(message.chat().id(), approved
                    ? linking
                        ? "✅ Telegram подтверждён. Вернитесь в Loco Place и завершите привязку."
                        : "✅ Вход подтверждён. Вернитесь в приложение Loco Place."
                    : "Ссылка недействительна или уже использована.");
        } catch (NativeAuthenticationException exception) {
            botClient.sendMessage(message.chat().id(), "Ссылка входа истекла или уже использована.");
        }
        return true;
    }

    private static String startParameter(String text) {
        if (text == null || text.isBlank()) return null;
        String[] parts = text.trim().split("\\s+", 2);
        String command = parts[0];
        int separator = command.indexOf('@');
        if (separator > 0) command = command.substring(0, separator);
        return "/start".equals(command) && parts.length == 2 ? parts[1].trim() : null;
    }
}
