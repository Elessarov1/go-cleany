package com.cleany.telegram.bot;

import java.time.Duration;

public class TelegramBotApiException extends RuntimeException {
    private final boolean retryable;
    private final boolean invalidRecipient;
    private final Duration retryAfter;

    public TelegramBotApiException(String message) {
        this(message, true, false, null);
    }

    public TelegramBotApiException(String message, boolean retryable, boolean invalidRecipient,
                                   Duration retryAfter) {
        super(message);
        this.retryable = retryable;
        this.invalidRecipient = invalidRecipient;
        this.retryAfter = retryAfter;
    }

    public boolean retryable() { return retryable; }
    public boolean invalidRecipient() { return invalidRecipient; }
    public Duration retryAfter() { return retryAfter; }
}
