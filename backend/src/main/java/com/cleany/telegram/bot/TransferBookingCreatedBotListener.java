package com.cleany.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.transfer.TransferAssignmentMode;
import com.cleany.transfer.TransferBookingCreatedEvent;
import com.cleany.transfer.TransferDriverRepository;
import com.cleany.transfer.TransferProperties;

import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Component
@RequiredArgsConstructor
public class TransferBookingCreatedBotListener {

    private static final Logger log = LoggerFactory.getLogger(TransferBookingCreatedBotListener.class);

    private final TransferProperties properties;
    private final TransferDriverRepository driverRepository;
    private final TransferBookingBotMessageFactory messageFactory;
    private final TelegramBotClient botClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void broadcast(TransferBookingCreatedEvent event) {
        if (properties.assignmentMode() != TransferAssignmentMode.DRIVER_SELF_ACCEPT) {
            return;
        }
        var booking = event.booking();
        String text = messageFactory.newBooking(booking);
        var keyboard = messageFactory.acceptKeyboard(booking.id());
        driverRepository
                .findAllByEnabledTrueAndVerifiedTelegramUserIdIsNotNullAndTelegramNotificationsEnabledTrueOrderByIdAsc()
                .stream()
                .filter(driver -> driver.canReceiveTelegramBookings() && driver.getTelegramChatId() != null)
                .forEach(driver -> {
                    try {
                        botClient.sendMessage(driver.getTelegramChatId(), text, keyboard);
                    } catch (RuntimeException exception) {
                        log.error(
                                "Transfer booking notification failed for booking {} and driver {}",
                                booking.id(), driver.getId(), exception
                        );
                    }
                });
    }
}
