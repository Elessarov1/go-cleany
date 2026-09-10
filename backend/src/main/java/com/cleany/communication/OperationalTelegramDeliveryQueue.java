package com.cleany.communication;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.telegram.bot.TelegramBotClient;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OperationalTelegramDeliveryQueue {
    private static final Duration LEASE = Duration.ofMinutes(2);
    private static final Duration RETENTION = Duration.ofDays(7);

    private final OperationalTelegramDeliveryRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Transactional
    public boolean enqueue(String deduplicationKey, long chatId, String message,
                           TelegramBotClient.InlineKeyboard keyboard) {
        var now = clock.instant();
        return jdbcTemplate.update("""
                insert into operational_telegram_delivery
                    (deduplication_key, chat_id, message_text, keyboard_json, status,
                     attempt_count, available_at, expires_at, created_at, updated_at)
                values (?, ?, ?, ?, 'PENDING', 0, ?, ?, ?, ?)
                on conflict (deduplication_key) do nothing
                """, deduplicationKey, chatId, message, write(keyboard), now,
                now.plus(RETENTION), now, now) == 1;
    }

    @Transactional
    List<Long> claim(int batchSize) {
        var now = clock.instant();
        List<OperationalTelegramDelivery> claimed = repository.claimable(now, batchSize);
        claimed.forEach(delivery -> delivery.claim(now, LEASE));
        return claimed.stream().map(OperationalTelegramDelivery::getId).toList();
    }

    @Transactional(readOnly = true)
    WorkItem load(long id) {
        var delivery = repository.findById(id).orElseThrow();
        return new WorkItem(delivery.getChatId(), delivery.getMessageText(),
                read(delivery.getKeyboardJson()));
    }

    @Transactional
    void delivered(long id) {
        repository.findById(id).ifPresent(delivery -> delivery.delivered(clock.instant()));
    }

    @Transactional
    void failed(long id, String code, boolean retryable, Duration retryAfter) {
        repository.findById(id).ifPresent(delivery ->
                delivery.failed(clock.instant(), code, retryAfter, retryable));
    }

    private String write(TelegramBotClient.InlineKeyboard keyboard) {
        try {
            return objectMapper.writeValueAsString(keyboard);
        } catch (JacksonException exception) {
            throw new IllegalArgumentException("Telegram keyboard is not serializable", exception);
        }
    }

    private TelegramBotClient.InlineKeyboard read(String value) {
        try {
            return objectMapper.readValue(value, TelegramBotClient.InlineKeyboard.class);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Stored Telegram keyboard is invalid", exception);
        }
    }

    record WorkItem(long chatId, String message, TelegramBotClient.InlineKeyboard keyboard) {
    }
}
