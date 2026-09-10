package com.cleany.idempotency;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.util.HexFormat;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.customer.CustomerAccountService;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private static final Duration RETENTION = Duration.ofDays(7);
    private static final int MAXIMUM_KEY_LENGTH = 200;
    private static final int MINIMUM_KEY_LENGTH = 8;

    private final IdempotencyRecordRepository repository;
    private final CustomerAccountService accountService;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Transactional
    public <T> T execute(String rawKey, String operation, Object payload, String resourceType,
                         Supplier<T> command, ToLongFunction<T> idExtractor,
                         LongFunction<T> replay) {
        String key = requireKey(rawKey);
        long customerId = accountService.currentCustomer().customerId();
        String keyHash = hash(key);
        String payloadHash = hash(serialize(payload));
        acquireLock(customerId, operation, keyHash);
        IdempotencyRecord existing = repository
                .findByCustomerIdAndOperationAndKeyHash(customerId, operation, keyHash)
                .orElse(null);
        var now = clock.instant();
        if (existing != null && !now.isBefore(existing.getExpiresAt())) {
            repository.delete(existing);
            repository.flush();
            existing = null;
        }
        if (existing != null) {
            if (!existing.getPayloadHash().equals(payloadHash)) {
                throw error("idempotency_key_payload_mismatch",
                        "Idempotency-Key was already used with a different request payload");
            }
            if (existing.getStatus() != IdempotencyStatus.COMPLETED
                    || existing.getResourceId() == null) {
                throw error("idempotency_request_in_progress", "Idempotent request is still in progress");
            }
            return replay.apply(existing.getResourceId());
        }

        IdempotencyRecord record = repository.save(new IdempotencyRecord(customerId, operation,
                keyHash, payloadHash, now, now.plus(RETENTION)));
        T result = command.get();
        record.complete(resourceType, idExtractor.applyAsLong(result), 201);
        return result;
    }

    private void acquireLock(long customerId, String operation, String keyHash) {
        byte[] digest = digest(customerId + ":" + operation + ":" + keyHash);
        long lockId = ByteBuffer.wrap(digest, 0, Long.BYTES).getLong();
        jdbcTemplate.execute("select pg_advisory_xact_lock(?)", (PreparedStatementCallback<Void>) statement -> {
            statement.setLong(1, lockId);
            statement.execute();
            return null;
        });
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JacksonException exception) {
            throw new IllegalArgumentException("Request payload cannot be serialized", exception);
        }
    }

    private static String requireKey(String key) {
        if (key == null || key.isBlank()) {
            throw error("idempotency_key_required", "Idempotency-Key is required");
        }
        String normalized = key.trim();
        if (normalized.length() < MINIMUM_KEY_LENGTH || normalized.length() > MAXIMUM_KEY_LENGTH) {
            throw error("idempotency_key_invalid", "Idempotency-Key length is invalid");
        }
        return normalized;
    }

    private static String hash(String value) {
        return HexFormat.of().formatHex(digest(value));
    }

    private static byte[] digest(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private static IdempotencyException error(String code, String message) {
        return new IdempotencyException(code, message);
    }
}
