package com.cleany.transfer;

import static com.cleany.common.text.TextValues.normalizeOptional;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transfer_driver")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 40)
    private String phone;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "configured_telegram_user_id", unique = true)
    private Long configuredTelegramUserId;

    @Column(name = "verified_telegram_user_id", unique = true)
    private Long verifiedTelegramUserId;

    @Column(name = "telegram_chat_id")
    private Long telegramChatId;

    @Column(name = "telegram_notifications_enabled", nullable = false)
    private boolean telegramNotificationsEnabled;

    @Column(name = "telegram_bot_authorized_at")
    private Instant telegramBotAuthorizedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    public TransferDriver(
            String name,
            String phone,
            boolean enabled,
            Long configuredTelegramUserId,
            Instant createdAt
    ) {
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        update(name, phone, enabled, configuredTelegramUserId, createdAt);
    }

    public void update(
            String name,
            String phone,
            boolean enabled,
            Long configuredTelegramUserId,
            Instant updatedAt
    ) {
        requirePositiveTelegramId(configuredTelegramUserId);
        if (!Objects.equals(this.configuredTelegramUserId, configuredTelegramUserId)) {
            clearTelegramAuthorization();
        }
        this.name = requireText(name, "name");
        this.phone = requireText(phone, "phone");
        this.enabled = enabled;
        this.configuredTelegramUserId = configuredTelegramUserId;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public void authorizeTelegram(long telegramUserId, long chatId, Instant authorizedAt) {
        if (!enabled || configuredTelegramUserId == null || configuredTelegramUserId != telegramUserId) {
            throw new InvalidTransferConfigurationException("Telegram identity does not match the driver");
        }
        if (chatId <= 0) {
            throw new InvalidTransferConfigurationException("Telegram chat id must be positive");
        }
        verifiedTelegramUserId = telegramUserId;
        telegramChatId = chatId;
        telegramNotificationsEnabled = true;
        telegramBotAuthorizedAt = Objects.requireNonNull(authorizedAt, "authorizedAt");
        updatedAt = authorizedAt;
    }

    public void setTelegramNotificationsEnabled(boolean enabled, Instant updatedAt) {
        if (enabled && telegramStatus() != DriverTelegramStatus.CONNECTED) {
            throw new InvalidTransferConfigurationException("Driver Telegram is not connected");
        }
        telegramNotificationsEnabled = enabled;
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public DriverTelegramStatus telegramStatus() {
        if (configuredTelegramUserId == null) {
            return DriverTelegramStatus.NOT_CONFIGURED;
        }
        if (verifiedTelegramUserId == null || telegramChatId == null || telegramBotAuthorizedAt == null) {
            return DriverTelegramStatus.AWAITING_AUTHORIZATION;
        }
        return DriverTelegramStatus.CONNECTED;
    }

    public boolean canReceiveTelegramBookings() {
        return enabled
                && telegramNotificationsEnabled
                && telegramStatus() == DriverTelegramStatus.CONNECTED;
    }

    private void clearTelegramAuthorization() {
        verifiedTelegramUserId = null;
        telegramChatId = null;
        telegramNotificationsEnabled = false;
        telegramBotAuthorizedAt = null;
    }

    private static void requirePositiveTelegramId(Long telegramUserId) {
        if (telegramUserId != null && telegramUserId <= 0) {
            throw new InvalidTransferConfigurationException("Telegram user id must be positive");
        }
    }

    private static String requireText(String value, String field) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            throw new InvalidTransferConfigurationException(field + " must not be blank");
        }
        return normalized;
    }
}
