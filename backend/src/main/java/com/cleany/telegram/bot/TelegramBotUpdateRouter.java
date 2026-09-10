package com.cleany.telegram.bot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Component
@RequiredArgsConstructor
public class TelegramBotUpdateRouter {

    private final TransferDriverBotService transferDriverBotService;
    private final TelegramNativeLoginBotService nativeLoginBotService;
    private final TelegramCustomerBotService customerBotService;
    private final TelegramCleanerBotService cleanerBotService;

    public void handle(TelegramUpdate update) {
        if (!transferDriverBotService.handleIfSupported(update)
                && !nativeLoginBotService.handleIfSupported(update)
                && !customerBotService.handleIfSupported(update)) {
            cleanerBotService.handle(update);
        }
    }
}
