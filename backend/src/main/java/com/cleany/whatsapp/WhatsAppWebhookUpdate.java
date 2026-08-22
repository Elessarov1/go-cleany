package com.cleany.whatsapp;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WhatsAppWebhookUpdate(
        String object,
        List<Entry> entry
) {

    public WhatsAppWebhookUpdate {
        entry = entry == null ? List.of() : List.copyOf(entry);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Entry(String id, List<Change> changes) {

        public Entry {
            changes = changes == null ? List.of() : List.copyOf(changes);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Change(String field, Value value) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Value(
            Metadata metadata,
            List<Contact> contacts,
            List<Message> messages
    ) {

        public Value {
            contacts = contacts == null ? List.of() : List.copyOf(contacts);
            messages = messages == null ? List.of() : List.copyOf(messages);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metadata(
            @JsonProperty("display_phone_number") String displayPhoneNumber,
            @JsonProperty("phone_number_id") String phoneNumberId
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Contact(Profile profile, @JsonProperty("wa_id") String waId) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(String name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
            String from,
            String id,
            String timestamp,
            String type,
            Text text
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Text(String body) {
    }
}
