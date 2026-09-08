package com.cleany.telegram.bot;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cleany.configuration.PublicApplicationProperties;
import com.cleany.telegram.bot.TelegramUpdate.Chat;
import com.cleany.telegram.bot.TelegramUpdate.Message;
import com.cleany.telegram.bot.TelegramUpdate.TelegramUser;

class TelegramCustomerBotServiceTest {

    private TelegramBotClient botClient;
    private TelegramCustomerBotService service;

    @BeforeEach
    void setUp() {
        botClient = Mockito.mock(TelegramBotClient.class);
        service = new TelegramCustomerBotService(
                botClient,
                new PublicApplicationProperties("https://loco-place.com/")
        );
    }

    @Test
    void startCommand_russianWelcomeAndAppLinksSent() {
        Assertions.assertTrue(service.handleIfSupported(update(101L, "ru", "/start")));

        ArgumentCaptor<String> text = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TelegramBotClient.InlineKeyboard> keyboard =
                ArgumentCaptor.forClass(TelegramBotClient.InlineKeyboard.class);
        Mockito.verify(botClient).sendMessage(Mockito.eq(101L), text.capture(), keyboard.capture());

        Assertions.assertAll(
                () -> Assertions.assertTrue(text.getValue().startsWith("Добро пожаловать в Loco Place")),
                () -> Assertions.assertTrue(text.getValue().contains("Аренда квартир, трансфер и уборка")),
                () -> Assertions.assertTrue(text.getValue().contains("hello@loco-place.com")),
                () -> Assertions.assertEquals(2, keyboard.getValue().rows().size()),
                () -> Assertions.assertEquals(
                        "https://loco-place.com/",
                        keyboard.getValue().rows().getFirst().getFirst().webAppUrl()
                ),
                () -> Assertions.assertEquals(
                        "https://loco-place.com/support",
                        keyboard.getValue().rows().get(1).getFirst().webAppUrl()
                )
        );
    }

    @Test
    void startCommandWithUnknownParameter_englishWelcomeSent() {
        Assertions.assertTrue(service.handleIfSupported(update(101L, "en-US", "/start unknown_parameter")));

        ArgumentCaptor<String> text = ArgumentCaptor.forClass(String.class);
        Mockito.verify(botClient).sendMessage(
                Mockito.eq(101L),
                text.capture(),
                Mockito.argThat(keyboard -> "Open Loco Place".equals(
                        keyboard.rows().getFirst().getFirst().text()
                ))
        );
        Assertions.assertAll(
                () -> Assertions.assertTrue(text.getValue().startsWith("Welcome to Loco Place")),
                () -> Assertions.assertTrue(text.getValue().contains(
                        "Apartment rentals, airport transfers and cleaning"
                ))
        );
    }

    @Test
    void helpCommandWithBotName_explainsContextualSupport() {
        Assertions.assertTrue(service.handleIfSupported(update(101L, "ru", "/help@go_cleany_bot")));

        Mockito.verify(botClient).sendMessage(
                Mockito.eq(101L),
                Mockito.contains("разделе «История»"),
                Mockito.any()
        );
    }

    @Test
    void unrelatedCommand_notConsumed() {
        Assertions.assertFalse(service.handleIfSupported(update(101L, "ru", "/whoami")));

        Mockito.verifyNoInteractions(botClient);
    }

    private static TelegramUpdate update(long userId, String languageCode, String text) {
        return new TelegramUpdate(
                1L,
                null,
                new Message(
                        7L,
                        new TelegramUser(userId, "customer", "Alex", null, languageCode),
                        new Chat(userId, "private"),
                        text,
                        null,
                        Collections.emptyList(),
                        null,
                        null
                )
        );
    }
}
