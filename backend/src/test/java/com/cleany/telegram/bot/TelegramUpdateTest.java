package com.cleany.telegram.bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

class TelegramUpdateTest {

    @Test
    void callbackQueryJson_requiredFieldsDeserialized() {
        String json = """
                {
                  "update_id": 10001,
                  "callback_query": {
                    "id": "callback-1",
                    "from": {
                      "id": 101,
                      "is_bot": false,
                      "first_name": "Cleaner"
                    },
                    "message": {"message_id": 55},
                    "data": "order:accept:43"
                  }
                }
                """;

        TelegramUpdate update = new ObjectMapper().readValue(json, TelegramUpdate.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(10001L, update.updateId()),
                () -> Assertions.assertEquals("callback-1", update.callbackQuery().id()),
                () -> Assertions.assertEquals(101L, update.callbackQuery().from().id()),
                () -> Assertions.assertEquals("Cleaner", update.callbackQuery().from().firstName()),
                () -> Assertions.assertEquals("order:accept:43", update.callbackQuery().data())
        );
    }

    @Test
    void photoMessageJson_fileIdentifiersAndCaptionDeserialized() {
        String json = """
                {
                  "update_id": 10002,
                  "message": {
                    "message_id": 56,
                    "from": {"id": 101, "first_name": "Cleaner"},
                    "chat": {"id": 101, "type": "private"},
                    "caption": "Everything is ready",
                    "photo": [
                      {
                        "file_id": "small-file",
                        "file_unique_id": "small-unique",
                        "width": 90,
                        "height": 90,
                        "file_size": 1200
                      },
                      {
                        "file_id": "large-file",
                        "file_unique_id": "large-unique",
                        "width": 1280,
                        "height": 960,
                        "file_size": 250000
                      }
                    ]
                  }
                }
                """;

        TelegramUpdate update = new ObjectMapper().readValue(json, TelegramUpdate.class);

        Assertions.assertAll(
                () -> Assertions.assertNull(update.callbackQuery()),
                () -> Assertions.assertEquals(101L, update.message().from().id()),
                () -> Assertions.assertEquals("private", update.message().chat().type()),
                () -> Assertions.assertEquals("Everything is ready", update.message().caption()),
                () -> Assertions.assertEquals("large-file", update.message().photo().get(1).fileId()),
                () -> Assertions.assertEquals(
                        250000L,
                        update.message().photo().get(1).fileSize().longValue()
                )
        );
    }

    @Test
    void contactMessageJson_phoneAndOwnerDeserialized() {
        String json = """
                {
                  "update_id": 10003,
                  "message": {
                    "message_id": 57,
                    "from": {
                      "id": 777,
                      "first_name": "Alex",
                      "language_code": "en"
                    },
                    "chat": {"id": 777, "type": "private"},
                    "contact": {
                      "phone_number": "+905551234567",
                      "first_name": "Alex",
                      "user_id": 777
                    }
                  }
                }
                """;

        TelegramUpdate update = new ObjectMapper().readValue(json, TelegramUpdate.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals("en", update.message().from().languageCode()),
                () -> Assertions.assertEquals("+905551234567", update.message().contact().phoneNumber()),
                () -> Assertions.assertEquals(Long.valueOf(777L), update.message().contact().userId())
        );
    }
}
