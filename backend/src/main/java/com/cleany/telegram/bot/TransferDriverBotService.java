package com.cleany.telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.cleany.transfer.InvalidTransferConfigurationException;
import com.cleany.transfer.TransferAssignmentConflictException;
import com.cleany.transfer.TransferBookingNotFoundException;
import com.cleany.transfer.TransferDriverAssignmentService;
import com.cleany.transfer.TransferDriverLinkException;
import com.cleany.transfer.TransferDriverLinkService;

import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Service
@RequiredArgsConstructor
public class TransferDriverBotService {

    private static final Logger log = LoggerFactory.getLogger(TransferDriverBotService.class);
    private static final String ACCEPT_PREFIX = "transfer:accept:";

    private final TransferDriverLinkService driverLinkService;
    private final TransferDriverAssignmentService assignmentService;
    private final TransferBookingBotMessageFactory messageFactory;
    private final TelegramBotClient botClient;

    public boolean handleIfSupported(TelegramUpdate update) {
        if (update.callbackQuery() != null && isTransferCallback(update.callbackQuery().data())) {
            handleCallback(update.callbackQuery());
            return true;
        }
        TelegramUpdate.Message message = update.message();
        String startParameter = startParameter(message == null ? null : message.text());
        if (startParameter != null && startParameter.startsWith("driver_")) {
            handleDriverStart(message, startParameter);
            return true;
        }
        return false;
    }

    private void handleDriverStart(TelegramUpdate.Message message, String startParameter) {
        if (!isAuthenticatedPrivateMessage(message)) {
            return;
        }
        String token = TransferDriverLinkService.extractToken(startParameter);
        if (token == null) {
            safeSend(message.chat().id(), "Ссылка подключения водителя недействительна.");
            return;
        }
        try {
            driverLinkService.authorize(token, message.from().id(), message.chat().id());
            safeSend(
                    message.chat().id(),
                    "✅ Telegram подключён. Теперь вы можете получать и принимать заявки Loco Transfer."
            );
        } catch (TransferDriverLinkException | InvalidTransferConfigurationException exception) {
            safeSend(
                    message.chat().id(),
                    "Не удалось подключить Telegram водителя. Проверьте ссылку и Telegram ID у администратора."
            );
        }
    }

    private void handleCallback(TelegramUpdate.CallbackQuery callback) {
        if (callback.from() == null) {
            safeAnswer(callback.id(), "Не удалось определить пользователя Telegram.", true);
            return;
        }
        Long bookingId = bookingId(callback.data());
        if (bookingId == null) {
            safeAnswer(callback.id(), "Действие недействительно.", true);
            return;
        }
        try {
            var booking = assignmentService.selfAccept(bookingId, callback.from().id());
            safeAnswer(callback.id(), "Трансфер №" + bookingId + " принят вами.", false);
            safeSend(callback.from().id(), messageFactory.accepted(booking));
        } catch (TransferAssignmentConflictException exception) {
            safeAnswer(callback.id(), "Трансфер уже назначен или больше недоступен.", true);
        } catch (TransferDriverLinkException exception) {
            safeAnswer(callback.id(), "Telegram не подключён к активному водителю.", true);
        } catch (TransferBookingNotFoundException exception) {
            safeAnswer(callback.id(), "Трансфер больше не существует.", true);
        }
    }

    private void safeSend(long chatId, String text) {
        try {
            botClient.sendMessage(chatId, text);
        } catch (RuntimeException exception) {
            log.error("Transfer Telegram message delivery failed for chat {}", chatId, exception);
        }
    }

    private void safeAnswer(String callbackId, String text, boolean showAlert) {
        try {
            botClient.answerCallbackQuery(callbackId, text, showAlert);
        } catch (RuntimeException exception) {
            log.error("Transfer Telegram callback answer failed for callback {}", callbackId, exception);
        }
    }

    private static boolean isAuthenticatedPrivateMessage(TelegramUpdate.Message message) {
        return message != null
                && message.from() != null
                && message.chat() != null
                && "private".equals(message.chat().type())
                && message.chat().id() == message.from().id();
    }

    private static boolean isTransferCallback(String data) {
        return data != null && data.startsWith(ACCEPT_PREFIX);
    }

    private static Long bookingId(String data) {
        if (!isTransferCallback(data)) {
            return null;
        }
        try {
            long id = Long.parseLong(data.substring(ACCEPT_PREFIX.length()));
            return id > 0 ? id : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static String startParameter(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String[] parts = text.trim().split("\\s+", 2);
        String command = parts[0];
        if (!(command.equals("/start") || command.startsWith("/start@")) || parts.length < 2) {
            return null;
        }
        return parts[1].trim();
    }
}
