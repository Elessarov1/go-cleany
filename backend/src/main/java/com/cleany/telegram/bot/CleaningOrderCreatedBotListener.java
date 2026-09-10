package com.cleany.telegram.bot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.configuration.CleanerProperties;
import com.cleany.communication.OperationalTelegramDeliveryQueue;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderCreatedEvent;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Component
public class CleaningOrderCreatedBotListener {

    private final CleanerProperties cleanerProperties;
    private final CleaningOrderBotMessageFactory messageFactory;
    private final OperationalTelegramDeliveryQueue deliveryQueue;

    public CleaningOrderCreatedBotListener(
            CleanerProperties cleanerProperties,
            CleaningOrderBotMessageFactory messageFactory,
            OperationalTelegramDeliveryQueue deliveryQueue
    ) {
        this.cleanerProperties = cleanerProperties;
        this.messageFactory = messageFactory;
        this.deliveryQueue = deliveryQueue;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void broadcast(CleaningOrderCreatedEvent event) {
        CleaningOrder order = event.order();
        String text = messageFactory.newOrder(order);
        var keyboard = messageFactory.newOrderKeyboard(order.getId());

        for (long cleanerId : cleanerProperties.telegramIds()) {
            deliveryQueue.enqueue("cleaning:new:" + order.getId() + ":" + cleanerId,
                    cleanerId, text, keyboard);
        }
    }
}
