package com.cleany.communication;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.telegram.bot.TelegramBotApiException;
import com.cleany.telegram.bot.TelegramBotClient;

class OperationalTelegramDeliveryWorkerTest {

    @Test
    void successfulDeliveryIsCompletedByWorker() {
        var queue = Mockito.mock(OperationalTelegramDeliveryQueue.class);
        var botClient = Mockito.mock(TelegramBotClient.class);
        var keyboard = TelegramBotClient.InlineKeyboard.empty();
        Mockito.when(queue.claim(20)).thenReturn(List.of(7L));
        Mockito.when(queue.load(7L)).thenReturn(
                new OperationalTelegramDeliveryQueue.WorkItem(900001L, "message", keyboard));

        new OperationalTelegramDeliveryWorker(queue, botClient).deliverBatch();

        Mockito.verify(botClient).sendMessage(900001L, "message", keyboard);
        Mockito.verify(queue).delivered(7L);
    }

    @Test
    void providerRateLimitIsReturnedToDurableRetryPolicy() {
        var queue = Mockito.mock(OperationalTelegramDeliveryQueue.class);
        var botClient = Mockito.mock(TelegramBotClient.class);
        var keyboard = TelegramBotClient.InlineKeyboard.empty();
        Mockito.when(queue.claim(20)).thenReturn(List.of(8L));
        Mockito.when(queue.load(8L)).thenReturn(
                new OperationalTelegramDeliveryQueue.WorkItem(900002L, "message", keyboard));
        Mockito.doThrow(new TelegramBotApiException(
                        "rate limited", true, false, Duration.ofMinutes(4)))
                .when(botClient).sendMessage(900002L, "message", keyboard);

        new OperationalTelegramDeliveryWorker(queue, botClient).deliverBatch();

        Mockito.verify(queue).failed(8L, "telegram_provider_error", true,
                Duration.ofMinutes(4));
        Mockito.verify(queue, Mockito.never()).delivered(8L);
    }
}
