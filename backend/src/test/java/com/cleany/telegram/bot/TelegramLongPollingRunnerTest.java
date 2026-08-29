package com.cleany.telegram.bot;

import java.net.URI;
import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.configuration.TelegramProperties;
import com.cleany.configuration.TelegramUpdateMode;

class TelegramLongPollingRunnerTest {

    private TelegramBotUpdateRouter updateRouter;
    private TelegramLongPollingRunner runner;

    @BeforeEach
    void setUp() {
        updateRouter = Mockito.mock(TelegramBotUpdateRouter.class);
        runner = new TelegramLongPollingRunner(
                new TelegramProperties(
                        "123456789:test-token",
                        "",
                        Duration.ofHours(1),
                        Duration.ofSeconds(30),
                        true,
                        URI.create("https://api.telegram.org"),
                        TelegramUpdateMode.POLLING,
                        25,
                        Duration.ofSeconds(3),
                        false
                ),
                Mockito.mock(TelegramBotClient.class),
                updateRouter
        );
    }

    @Test
    void successfullyHandledUpdate_advancesOffset() {
        TelegramUpdate update = new TelegramUpdate(42L, null, null);

        long nextOffset = runner.processUpdate(0L, update);

        Assertions.assertEquals(43L, nextOffset);
        Mockito.verify(updateRouter).handle(update);
    }

    @Test
    void handlerFailure_doesNotReturnAdvancedOffset() {
        TelegramUpdate update = new TelegramUpdate(42L, null, null);
        Mockito.doThrow(new IllegalStateException("failed"))
                .when(updateRouter)
                .handle(update);

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> runner.processUpdate(0L, update)
        );
    }

    @Test
    void updateBelowCurrentOffset_isIgnored() {
        long nextOffset = runner.processUpdate(43L, new TelegramUpdate(42L, null, null));

        Assertions.assertEquals(43L, nextOffset);
        Mockito.verifyNoInteractions(updateRouter);
    }
}
