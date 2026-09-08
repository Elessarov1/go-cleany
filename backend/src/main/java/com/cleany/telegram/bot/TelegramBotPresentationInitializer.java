package com.cleany.telegram.bot;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.cleany.configuration.PublicApplicationProperties;
import com.cleany.configuration.TelegramProperties;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Component
public class TelegramBotPresentationInitializer {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotPresentationInitializer.class);
    private static final int MAX_ATTEMPTS = 3;

    private static final String DESCRIPTION_RU = """
            Поручите задачу Loco Place: уборка, аренда квартир и трансфер в Аланье. \
            Откройте приложение, чтобы выбрать сервис.
            """.strip();
    private static final String DESCRIPTION_EN = """
            Delegate a task to Loco Place: cleaning, apartment rentals and airport transfers in Alanya. \
            Open the app to choose a service.
            """.strip();
    private static final String SHORT_DESCRIPTION_RU =
            "Loco Place — уборка, аренда квартир и трансфер в Аланье.";
    private static final String SHORT_DESCRIPTION_EN =
            "Loco Place — cleaning, apartment rentals and airport transfers in Alanya.";

    private final TelegramBotClient botClient;
    private final PublicApplicationProperties publicApplicationProperties;
    private final Duration retryDelay;

    public TelegramBotPresentationInitializer(
            TelegramBotClient botClient,
            PublicApplicationProperties publicApplicationProperties,
            TelegramProperties telegramProperties
    ) {
        this.botClient = botClient;
        this.publicApplicationProperties = publicApplicationProperties;
        retryDelay = telegramProperties.pollingRetryDelay();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        Thread.ofVirtual()
                .name("telegram-bot-presentation")
                .start(this::configureWithRetry);
    }

    void configureWithRetry() {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                configure();
                log.info("Telegram bot profile, commands and menu are configured");
                return;
            } catch (RuntimeException exception) {
                if (attempt == MAX_ATTEMPTS) {
                    log.error(
                            "Telegram bot presentation configuration failed after {} attempts ({})",
                            MAX_ATTEMPTS,
                            exception.getClass().getSimpleName()
                    );
                    return;
                }
                log.warn(
                        "Telegram bot presentation configuration attempt {} failed ({}); retrying",
                        attempt,
                        exception.getClass().getSimpleName()
                );
                if (!waitBeforeRetry()) {
                    return;
                }
            }
        }
    }

    private void configure() {
        botClient.setName("Loco Place", null);
        botClient.setDescription(DESCRIPTION_RU, null);
        botClient.setDescription(DESCRIPTION_EN, "en");
        botClient.setShortDescription(SHORT_DESCRIPTION_RU, null);
        botClient.setShortDescription(SHORT_DESCRIPTION_EN, "en");
        botClient.setCommands(List.of(
                new TelegramBotClient.BotCommand("start", "Открыть Loco Place"),
                new TelegramBotClient.BotCommand("help", "Помощь и поддержка")
        ), null);
        botClient.setCommands(List.of(
                new TelegramBotClient.BotCommand("start", "Open Loco Place"),
                new TelegramBotClient.BotCommand("help", "Help and support")
        ), "en");
        botClient.setDefaultMenuButton("Loco Place", publicApplicationProperties.baseUrl() + "/");
    }

    private boolean waitBeforeRetry() {
        try {
            Thread.sleep(retryDelay);
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.info("Telegram bot presentation configuration was interrupted");
            return false;
        }
    }
}
