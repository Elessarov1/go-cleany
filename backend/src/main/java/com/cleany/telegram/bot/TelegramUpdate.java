package com.cleany.telegram.bot;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramUpdate(
        @JsonProperty("update_id") long updateId,
        @JsonProperty("callback_query") CallbackQuery callbackQuery,
        Message message
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CallbackQuery(
            String id,
            TelegramUser from,
            String data
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TelegramUser(
            long id,
            String username,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("last_name") String lastName,
            @JsonProperty("language_code") String languageCode
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
            @JsonProperty("message_id") long messageId,
            TelegramUser from,
            Chat chat,
            String text,
            String caption,
            List<PhotoSize> photo,
            Contact contact,
            @JsonProperty("write_access_allowed") WriteAccessAllowed writeAccessAllowed
    ) {

        public Message {
            photo = photo == null ? Collections.emptyList() : List.copyOf(photo);
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WriteAccessAllowed(@JsonProperty("from_request") boolean fromRequest) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Chat(long id, String type) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Contact(
            @JsonProperty("phone_number") String phoneNumber,
            @JsonProperty("user_id") Long userId
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PhotoSize(
            @JsonProperty("file_id") String fileId,
            @JsonProperty("file_unique_id") String fileUniqueId,
            int width,
            int height,
            @JsonProperty("file_size") Long fileSize
    ) {
    }
}
