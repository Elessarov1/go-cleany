package com.cleany.customer;

public class TelegramIdentityNotLinkedException extends RuntimeException {

    public TelegramIdentityNotLinkedException() {
        super("Telegram identity is not linked to this account");
    }
}
