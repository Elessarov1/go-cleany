package com.cleany.order;

public class CleanerNotAuthorizedException extends RuntimeException {

    public CleanerNotAuthorizedException(long cleanerTelegramUserId) {
        super("Telegram user %d is not authorized for this cleaner action".formatted(cleanerTelegramUserId));
    }
}

