package com.cleany.telegram.bot;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.configuration.CleanerProperties;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderCreatedEvent;
import com.cleany.telegram.bot.TelegramBotClient.InlineButton;
import com.cleany.telegram.bot.TelegramBotClient.InlineKeyboard;

class CleaningOrderCreatedBotListenerTest {

    @Test
    void newOrder_notificationSentToEveryCleaner_evenWhenOneDeliveryFails() {
        var cleanerProperties = new CleanerProperties(List.of(101L, 102L));
        var messageFactory = Mockito.mock(CleaningOrderBotMessageFactory.class);
        var botClient = Mockito.mock(TelegramBotClient.class);
        var listener = new CleaningOrderCreatedBotListener(
                cleanerProperties,
                messageFactory,
                botClient
        );
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(43L);
        InlineKeyboard keyboard = InlineKeyboard.ofRows(List.of(
                InlineButton.callback("Accept", "order:accept:43")
        ));
        Mockito.when(messageFactory.newOrder(order)).thenReturn("new-order");
        Mockito.when(messageFactory.newOrderKeyboard(43L)).thenReturn(keyboard);
        Mockito.doThrow(new TelegramBotApiException("delivery failed"))
                .when(botClient).sendMessage(101L, "new-order", keyboard);

        listener.broadcast(new CleaningOrderCreatedEvent(order));

        Mockito.verify(botClient).sendMessage(101L, "new-order", keyboard);
        Mockito.verify(botClient).sendMessage(102L, "new-order", keyboard);
    }
}
