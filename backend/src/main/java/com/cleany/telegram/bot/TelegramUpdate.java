package com.cleany.telegram.bot;

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
            @JsonProperty("last_name") String lastName
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
            @JsonProperty("message_id") long messageId,
            TelegramUser from,
            Chat chat,
            String text,
            String caption,
            List<PhotoSize> photo
    ) {

        public Message {
            photo = photo == null ? List.of() : List.copyOf(photo);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Chat(long id, String type) {
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
