package com.cleany.telegram.bot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.transfer.TransferAssignmentMode;
import com.cleany.communication.OperationalTelegramDeliveryQueue;
import com.cleany.transfer.TransferBookingCreatedEvent;
import com.cleany.transfer.TransferDriverRepository;
import com.cleany.transfer.TransferProperties;

import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Component
@RequiredArgsConstructor
public class TransferBookingCreatedBotListener {

    private final TransferProperties properties;
    private final TransferDriverRepository driverRepository;
    private final TransferBookingBotMessageFactory messageFactory;
    private final OperationalTelegramDeliveryQueue deliveryQueue;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
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
                .forEach(driver -> deliveryQueue.enqueue(
                        "transfer:new:" + booking.id() + ":" + driver.getId(),
                        driver.getTelegramChatId(), text, keyboard));
    }
}
