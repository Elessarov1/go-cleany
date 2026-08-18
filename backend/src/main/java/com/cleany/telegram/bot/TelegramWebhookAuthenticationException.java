package com.cleany.telegram.bot;

public class TelegramWebhookAuthenticationException extends RuntimeException {

    public TelegramWebhookAuthenticationException() {
        super("Valid Telegram webhook authentication is required");
    }
}
