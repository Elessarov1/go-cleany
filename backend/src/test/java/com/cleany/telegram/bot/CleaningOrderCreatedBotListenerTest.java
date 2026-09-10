package com.cleany.telegram.bot;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.communication.OperationalTelegramDeliveryQueue;
import com.cleany.configuration.CleanerProperties;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderCreatedEvent;
import com.cleany.telegram.bot.TelegramBotClient.InlineButton;
import com.cleany.telegram.bot.TelegramBotClient.InlineKeyboard;

class CleaningOrderCreatedBotListenerTest {

    @Test
    void newOrder_notificationIsPersistedForEveryCleaner() {
        var cleanerProperties = new CleanerProperties(List.of(101L, 102L));
        var messageFactory = Mockito.mock(CleaningOrderBotMessageFactory.class);
        var deliveryQueue = Mockito.mock(OperationalTelegramDeliveryQueue.class);
        var listener = new CleaningOrderCreatedBotListener(
                cleanerProperties,
                messageFactory,
                deliveryQueue
        );
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(43L);
        InlineKeyboard keyboard = InlineKeyboard.ofRows(List.of(
                InlineButton.callback("Accept", "order:accept:43")
        ));
        Mockito.when(messageFactory.newOrder(order)).thenReturn("new-order");
        Mockito.when(messageFactory.newOrderKeyboard(43L)).thenReturn(keyboard);
        listener.broadcast(new CleaningOrderCreatedEvent(order));

        Mockito.verify(deliveryQueue).enqueue("cleaning:new:43:101", 101L, "new-order", keyboard);
        Mockito.verify(deliveryQueue).enqueue("cleaning:new:43:102", 102L, "new-order", keyboard);
    }
}
