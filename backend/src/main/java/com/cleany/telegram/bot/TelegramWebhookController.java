package com.cleany.telegram.bot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@ConditionalOnProperty(prefix = "telegram", name = "update-mode", havingValue = "webhook")
@RestController
@RequestMapping("/api/v1/telegram/webhook")
public class TelegramWebhookController {

    static final String SECRET_HEADER = "X-Telegram-Bot-Api-Secret-Token";

    private final TelegramWebhookSecretValidator secretValidator;
    private final TelegramCleanerBotService cleanerBotService;

    public TelegramWebhookController(
            TelegramWebhookSecretValidator secretValidator,
            TelegramCleanerBotService cleanerBotService
    ) {
        this.secretValidator = secretValidator;
        this.cleanerBotService = cleanerBotService;
    }

    @PostMapping
    public ResponseEntity<Void> receiveUpdate(
            @RequestHeader(name = SECRET_HEADER, required = false) String secret,
            @RequestBody TelegramUpdate update
    ) {
        secretValidator.validate(secret);
        cleanerBotService.handle(update);
        return ResponseEntity.ok().build();
    }
}
