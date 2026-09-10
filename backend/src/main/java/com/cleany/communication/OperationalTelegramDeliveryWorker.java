package com.cleany.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cleany.telegram.bot.TelegramBotApiException;
import com.cleany.telegram.bot.TelegramBotClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
class OperationalTelegramDeliveryWorker {
    private static final Logger log = LoggerFactory.getLogger(OperationalTelegramDeliveryWorker.class);

    private final OperationalTelegramDeliveryQueue queue;
    private final TelegramBotClient botClient;

    @Scheduled(fixedDelayString = "${communications.worker-delay:2s}")
    void deliverBatch() {
        for (Long id : queue.claim(20)) deliver(id);
    }

    private void deliver(long id) {
        try {
            var work = queue.load(id);
            botClient.sendMessage(work.chatId(), work.message(), work.keyboard());
            queue.delivered(id);
        } catch (TelegramBotApiException exception) {
            queue.failed(id, "telegram_provider_error", exception.retryable(), exception.retryAfter());
        } catch (RuntimeException exception) {
            log.warn("Unexpected operational Telegram delivery failure for job {}", id, exception);
            queue.failed(id, "unexpected_delivery_error", true, null);
        }
    }
}
