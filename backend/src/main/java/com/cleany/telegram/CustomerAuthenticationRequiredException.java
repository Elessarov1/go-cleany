package com.cleany.telegram;

public class CustomerAuthenticationRequiredException extends RuntimeException {

    public CustomerAuthenticationRequiredException() {
        super("Valid Telegram Mini App authentication is required");
    }
}
