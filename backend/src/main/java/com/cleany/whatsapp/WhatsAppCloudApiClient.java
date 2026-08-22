package com.cleany.whatsapp;

public interface WhatsAppCloudApiClient {

    void sendText(String recipientWaId, String text);
}
