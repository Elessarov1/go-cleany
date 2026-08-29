package com.cleany.telegram.bot;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import com.cleany.configuration.TelegramProperties;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@ConditionalOnProperty(
        prefix = "telegram",
        name = "update-mode",
        havingValue = "polling",
        matchIfMissing = true
)
@Component
public class TelegramLongPollingRunner implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(TelegramLongPollingRunner.class);

    private final TelegramProperties properties;
    private final TelegramBotClient botClient;
    private final TelegramBotUpdateRouter updateRouter;

    private volatile boolean running;
    private volatile Thread pollingThread;

    public TelegramLongPollingRunner(
            TelegramProperties properties,
            TelegramBotClient botClient,
            TelegramBotUpdateRouter updateRouter
    ) {
        this.properties = properties;
        this.botClient = botClient;
        this.updateRouter = updateRouter;
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        pollingThread = Thread.ofVirtual()
                .name("telegram-long-polling")
                .start(this::poll);
    }

    @Override
    public synchronized void stop() {
        running = false;
        if (pollingThread != null) {
            pollingThread.interrupt();
            pollingThread = null;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    long processUpdate(long currentOffset, TelegramUpdate update) {
        if (update == null || update.updateId() < currentOffset) {
            return currentOffset;
        }
        updateRouter.handle(update);
        return update.updateId() + 1;
    }

    private void poll() {
        long nextOffset = 0;
        boolean webhookDeleted = false;
        log.info("Telegram long polling is starting");

        while (running) {
            try {
                if (!webhookDeleted) {
                    botClient.deleteWebhook(properties.pollingDropPendingUpdates());
                    webhookDeleted = true;
                    log.info("Telegram webhook is disabled; long polling is active");
                }

                List<TelegramUpdate> updates = botClient.getUpdates(
                        nextOffset,
                        properties.pollingTimeoutSeconds()
                );
                for (TelegramUpdate update : updates) {
                    nextOffset = processUpdate(nextOffset, update);
                }
            } catch (RuntimeException exception) {
                if (!running) {
                    break;
                }
                log.error("Telegram long polling iteration failed; it will be retried", exception);
                waitBeforeRetry(properties.pollingRetryDelay());
            }
        }

        log.info("Telegram long polling stopped");
    }

    private void waitBeforeRetry(Duration delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
