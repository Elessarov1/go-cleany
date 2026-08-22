package com.cleany.whatsapp;

public class WhatsAppWebhookAuthenticationException extends RuntimeException {

    public WhatsAppWebhookAuthenticationException() {
        super("Valid WhatsApp webhook authentication is required");
    }
}
