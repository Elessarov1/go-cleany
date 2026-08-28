package com.cleany.telegram.bot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.cleany.rental.RentalAdminNotificationSender;
import com.cleany.rental.RentalBookingAdminEvent;
import com.cleany.rental.RentalBookingAdminNotification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramRentalAdminNotificationSender implements RentalAdminNotificationSender {

    private final AdminTelegramRecipientService recipientService;
    private final TelegramRentalAdminMessageFactory messageFactory;
    private final TelegramBotClient botClient;

    @Override
    public void send(
            RentalBookingAdminEvent.Type type,
            RentalBookingAdminNotification notification
    ) {
        String message = messageFactory.format(type, notification);
        recipientService.recipients()
                .forEach(adminId -> safeSend(adminId, message, notification.bookingId()));
    }

    private void safeSend(long adminId, String message, long bookingId) {
        try {
            botClient.sendMessage(adminId, message, TelegramBotClient.InlineKeyboard.empty());
        } catch (RuntimeException exception) {
            log.error(
                    "Telegram rental admin notification failed for booking {} and admin {}",
                    bookingId,
                    adminId,
                    exception
            );
        }
    }
}
