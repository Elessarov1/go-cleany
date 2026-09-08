package com.cleany.telegram.bot;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.cleany.configuration.PublicApplicationProperties;
import com.cleany.configuration.TelegramProperties;
import com.cleany.configuration.TelegramUpdateMode;

class TelegramBotPresentationInitializerTest {

    private TelegramBotClient botClient;
    private TelegramBotPresentationInitializer initializer;

    @BeforeEach
    void setUp() {
        botClient = Mockito.mock(TelegramBotClient.class);
        initializer = new TelegramBotPresentationInitializer(
                botClient,
                new PublicApplicationProperties("https://loco-place.com"),
                telegramProperties()
        );
    }

    @Test
    void configuration_setsLocalizedProfileCommandsAndMenu() {
        initializer.configureWithRetry();

        Mockito.verify(botClient).setName("Loco Place", null);
        Mockito.verify(botClient).setDescription(
                Mockito.contains("аренда квартир, трансфер и уборка"),
                Mockito.isNull()
        );
        Mockito.verify(botClient).setDescription(
                Mockito.contains("apartment rentals, airport transfers and cleaning"),
                Mockito.eq("en")
        );
        Mockito.verify(botClient).setShortDescription(Mockito.contains("Аланье"), Mockito.isNull());
        Mockito.verify(botClient).setShortDescription(Mockito.contains("Alanya"), Mockito.eq("en"));
        Mockito.verify(botClient).setDefaultMenuButton("Loco Place", "https://loco-place.com/");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<TelegramBotClient.BotCommand>> commands = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> language = ArgumentCaptor.forClass(String.class);
        Mockito.verify(botClient, Mockito.times(2)).setCommands(commands.capture(), language.capture());
        Assertions.assertAll(
                () -> Assertions.assertEquals(List.of("start", "help"), commands.getAllValues().getFirst()
                        .stream().map(TelegramBotClient.BotCommand::command).toList()),
                () -> Assertions.assertNull(language.getAllValues().getFirst()),
                () -> Assertions.assertEquals("en", language.getAllValues().get(1))
        );
    }

    @Test
    void transientFailure_retriedUntilSuccess() {
        Mockito.doThrow(new TelegramBotApiException("temporary"))
                .doThrow(new TelegramBotApiException("temporary"))
                .doNothing()
                .when(botClient).setName("Loco Place", null);

        initializer.configureWithRetry();

        Mockito.verify(botClient, Mockito.times(3)).setName("Loco Place", null);
        Mockito.verify(botClient).setDefaultMenuButton("Loco Place", "https://loco-place.com/");
    }

    @Test
    void permanentFailure_doesNotEscapeInitializer() {
        Mockito.doThrow(new TelegramBotApiException("unavailable"))
                .when(botClient).setName("Loco Place", null);

        Assertions.assertDoesNotThrow(initializer::configureWithRetry);
        Mockito.verify(botClient, Mockito.times(3)).setName("Loco Place", null);
        Mockito.verify(botClient, Mockito.never()).setDefaultMenuButton(Mockito.anyString(), Mockito.anyString());
    }

    private static TelegramProperties telegramProperties() {
        return new TelegramProperties(
                "123456789:test-token",
                "",
                Duration.ofHours(1),
                Duration.ofSeconds(30),
                true,
                URI.create("https://api.telegram.org"),
                TelegramUpdateMode.POLLING,
                25,
                Duration.ofMillis(1),
                false
        );
    }
}
