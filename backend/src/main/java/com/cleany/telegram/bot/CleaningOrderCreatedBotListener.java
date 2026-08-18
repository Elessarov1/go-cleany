package com.cleany.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.configuration.CleanerProperties;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderCreatedEvent;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Component
public class CleaningOrderCreatedBotListener {

    private static final Logger log = LoggerFactory.getLogger(CleaningOrderCreatedBotListener.class);

    private final CleanerProperties cleanerProperties;
    private final CleaningOrderBotMessageFactory messageFactory;
    private final TelegramBotClient botClient;

    public CleaningOrderCreatedBotListener(
            CleanerProperties cleanerProperties,
            CleaningOrderBotMessageFactory messageFactory,
            TelegramBotClient botClient
    ) {
        this.cleanerProperties = cleanerProperties;
        this.messageFactory = messageFactory;
        this.botClient = botClient;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void broadcast(CleaningOrderCreatedEvent event) {
        CleaningOrder order = event.order();
        String text = messageFactory.newOrder(order);
        var keyboard = messageFactory.newOrderKeyboard(order.getId());

        for (long cleanerId : cleanerProperties.telegramIds()) {
            try {
                botClient.sendMessage(cleanerId, text, keyboard);
            } catch (RuntimeException exception) {
                log.error(
                        "New order notification failed for order {} and cleaner {}",
                        order.getId(),
                        cleanerId,
                        exception
                );
            }
        }
    }
}
