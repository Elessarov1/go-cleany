package com.cleany.telegram.bot;

import java.util.Comparator;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.cleany.configuration.CleanerProperties;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.order.CleanerNotAuthorizedException;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderReport;
import com.cleany.order.CleaningOrderReportProgress;
import com.cleany.order.CleaningOrderService;
import com.cleany.order.InvalidPhotoReportInputException;
import com.cleany.order.InvalidOrderStateException;
import com.cleany.order.InvalidOnsiteIssueException;
import com.cleany.order.InvalidPhoneNumberException;
import com.cleany.order.OnsiteIssueDelivery;
import com.cleany.order.OnsiteIssueProgress;
import com.cleany.order.OnsiteIssueReason;
import com.cleany.order.OnsiteIssueService;
import com.cleany.order.OrderClaimConflictException;
import com.cleany.order.OrderNotFoundException;
import com.cleany.order.PhotoReportEmptyException;
import com.cleany.order.ReportCollectionNotActiveException;
import com.cleany.telegram.bot.TelegramUpdate.CallbackQuery;
import com.cleany.telegram.bot.TelegramUpdate.Contact;
import com.cleany.telegram.bot.TelegramUpdate.Message;
import com.cleany.telegram.bot.TelegramUpdate.PhotoSize;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Service
public class TelegramCleanerBotService {

    private static final Logger log = LoggerFactory.getLogger(TelegramCleanerBotService.class);
    private static final Pattern CALLBACK_PATTERN = Pattern.compile(
            "order:(accept|skip|finish|cancel|report|issue|issue_submit):([1-9][0-9]*)"
    );
    private static final Pattern ISSUE_REASON_CALLBACK_PATTERN = Pattern.compile(
            "order:issue_reason:([A-Z_]+):([1-9][0-9]*)"
    );

    private final CleanerProperties cleanerProperties;
    private final CleaningOrderService orderService;
    private final CleaningOrderBotMessageFactory messageFactory;
    private final TelegramBotClient botClient;
    private final TelegramAdminBotService adminBotService;
    private final CustomerAccountService customerAccountService;
    private final CustomerExternalIdentityRepository customerIdentityRepository;
    private final OnsiteIssueService onsiteIssueService;

    public TelegramCleanerBotService(
            CleanerProperties cleanerProperties,
            CleaningOrderService orderService,
            CleaningOrderBotMessageFactory messageFactory,
            TelegramBotClient botClient,
            TelegramAdminBotService adminBotService,
            CustomerAccountService customerAccountService,
            CustomerExternalIdentityRepository customerIdentityRepository,
            OnsiteIssueService onsiteIssueService
    ) {
        if (cleanerProperties.telegramIds().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one cleaner.telegram-ids value is required when the Telegram bot is enabled"
            );
        }
        this.cleanerProperties = cleanerProperties;
        this.orderService = orderService;
        this.messageFactory = messageFactory;
        this.botClient = botClient;
        this.adminBotService = adminBotService;
        this.customerAccountService = customerAccountService;
        this.customerIdentityRepository = customerIdentityRepository;
        this.onsiteIssueService = onsiteIssueService;
    }

    public void handle(TelegramUpdate update) {
        if (update == null) {
            return;
        }

        if (update.callbackQuery() != null) {
            handleCallback(update.callbackQuery());
        } else if (update.message() != null) {
            handleMessage(update.message());
        }
    }

    private void handleCallback(CallbackQuery callback) {
        if (callback.id() == null || callback.id().isBlank()) {
            return;
        }
        if (callback.from() == null || !cleanerProperties.contains(callback.from().id())) {
            safeAnswer(callback.id(), "Вы не авторизованы как клинер.", true);
            return;
        }

        CallbackAction action = parseAction(callback.data());
        if (action == null) {
            safeAnswer(callback.id(), "Это действие не поддерживается.", true);
            return;
        }

        long cleanerId = callback.from().id();
        try {
            switch (action.name()) {
                case "accept" -> accept(callback.id(), action.orderId(), cleanerId);
                case "skip" -> safeAnswer(callback.id(), "Заказ пропущен.", false);
                case "finish" -> finish(callback.id(), action.orderId(), cleanerId);
                case "cancel" -> cancel(callback.id(), action.orderId(), cleanerId);
                case "report" -> deliverReport(callback.id(), action.orderId(), cleanerId);
                case "issue" -> startOnsiteIssue(callback.id(), action.orderId(), cleanerId);
                case "issue_reason" -> selectOnsiteIssueReason(
                        callback.id(),
                        action.orderId(),
                        cleanerId,
                        action.reason()
                );
                case "issue_submit" -> submitOnsiteIssue(callback.id(), action.orderId(), cleanerId);
                default -> safeAnswer(callback.id(), "Это действие не поддерживается.", true);
            }
        } catch (OrderNotFoundException exception) {
            safeAnswer(callback.id(), "Этот заказ больше не существует.", true);
        } catch (CleanerNotAuthorizedException exception) {
            safeAnswer(callback.id(), "У вас нет доступа к этому заказу.", true);
        } catch (InvalidOrderStateException exception) {
            safeAnswer(callback.id(), "Это действие больше недоступно.", true);
        } catch (PhotoReportEmptyException exception) {
            safeAnswer(callback.id(), "Перед отправкой отчёта добавьте хотя бы одну фотографию.", true);
        } catch (InvalidOnsiteIssueException exception) {
            safeAnswer(callback.id(), onsiteIssueError(exception), true);
        }
    }

    private void handleMessage(Message message) {
        if (message.from() == null
                || message.chat() == null
                || !"private".equals(message.chat().type())
                || message.chat().id() != message.from().id()) {
            return;
        }

        long cleanerId = message.from().id();
        if (message.contact() != null) {
            saveCustomerContact(message);
            return;
        }
        if (isCommand(message.text(), "/start")) {
            safeSend(cleanerId, "Бот go-cleany запущен. Отправьте /whoami, чтобы узнать свой Telegram ID.");
            return;
        }
        if (isCommand(message.text(), "/whoami")) {
            safeSend(cleanerId, "Ваш Telegram ID: " + cleanerId);
            return;
        }
        if (adminBotService.handleIfSupported(cleanerId, message.text())) {
            return;
        }
        if (!cleanerProperties.contains(cleanerId)) {
            return;
        }

        try {
            if (onsiteIssueService.hasActiveDraft(cleanerId)) {
                handleOnsiteIssueInput(cleanerId, message);
                return;
            }
            PhotoSize photo = largestPhoto(message);
            if (photo != null) {
                CleaningOrderReportProgress progress = orderService.addPhotoToActiveReport(
                        cleanerId,
                        photo.fileId(),
                        photo.fileUniqueId(),
                        message.caption()
                );
                safeSend(
                        cleanerId,
                        messageFactory.photoSaved(progress),
                        messageFactory.reportReadyKeyboard(progress.orderId()),
                        progress.orderId()
                );
                return;
            }
            if (message.text() != null
                    && !message.text().isBlank()
                    && !message.text().startsWith("/")) {
                CleaningOrderReportProgress progress = orderService.updateActiveReportComment(
                        cleanerId,
                        message.text()
                );
                TelegramBotClient.InlineKeyboard keyboard = progress.photoCount() == 0
                        ? TelegramBotClient.InlineKeyboard.empty()
                        : messageFactory.reportReadyKeyboard(progress.orderId());
                safeSend(
                        cleanerId,
                        messageFactory.commentSaved(progress),
                        keyboard,
                        progress.orderId()
                );
            }
        } catch (ReportCollectionNotActiveException exception) {
            safeSend(cleanerId, "Нет активного фотоотчёта. Сначала нажмите «Завершить уборку» в принятом заказе.");
        } catch (InvalidPhotoReportInputException exception) {
            safeSend(cleanerId, "Комментарий клинера должен содержать от 1 до 1000 символов.");
        } catch (CleanerNotAuthorizedException | InvalidOrderStateException exception) {
            safeSend(cleanerId, "У вас нет доступа к этому фотоотчёту.");
        } catch (InvalidOnsiteIssueException exception) {
            safeSend(cleanerId, onsiteIssueError(exception));
        }
    }

    private void handleOnsiteIssueInput(long cleanerId, Message message) {
        PhotoSize photo = largestPhoto(message);
        OnsiteIssueProgress progress;
        if (photo != null) {
            byte[] content;
            try {
                content = botClient.downloadFile(photo.fileId());
            } catch (TelegramBotApiException exception) {
                log.error("Telegram evidence download failed for cleaner {}", cleanerId, exception);
                safeSend(cleanerId, "Не удалось загрузить фотографию из Telegram. Попробуйте отправить её ещё раз.");
                return;
            }
            progress = onsiteIssueService.addPhoto(
                    cleanerId,
                    photo.fileId(),
                    photo.fileUniqueId(),
                    content,
                    message.caption()
            );
        } else if (message.text() != null
                && !message.text().isBlank()
                && !message.text().startsWith("/")) {
            progress = onsiteIssueService.updateComment(cleanerId, message.text());
        } else {
            return;
        }

        safeSend(
                cleanerId,
                messageFactory.onsiteIssueProgress(progress),
                messageFactory.onsiteIssueSubmitKeyboard(progress),
                progress.orderId()
        );
    }

    private void saveCustomerContact(Message message) {
        Contact contact = message.contact();
        if (contact.userId() == null || contact.userId() != message.from().id()) {
            return;
        }

        try {
            customerAccountService.savePhoneForExternalIdentity(
                    ExternalIdentityProvider.TELEGRAM,
                    Long.toString(message.from().id()),
                    message.from().username(),
                    displayName(message.from()),
                    message.from().languageCode(),
                    asInternationalPhone(contact.phoneNumber())
            );
            safeSend(message.from().id(), "Номер телефона сохранён и будет подставлен в форму заказа.");
        } catch (InvalidPhoneNumberException exception) {
            log.warn("Telegram sent an invalid contact phone for user {}", message.from().id());
            safeSend(message.from().id(), "Не удалось распознать номер телефона. Введите его в форме вручную.");
        }
    }

    private static String displayName(TelegramUpdate.TelegramUser user) {
        String firstName = user.firstName() == null ? "" : user.firstName().trim();
        String lastName = user.lastName() == null ? "" : user.lastName().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isEmpty() ? "Telegram user " + user.id() : fullName;
    }

    private static String asInternationalPhone(String phone) {
        if (phone == null) {
            return null;
        }
        String normalized = phone.trim();
        return normalized.startsWith("+") ? normalized : "+" + normalized;
    }

    private void accept(String callbackId, long orderId, long cleanerId) {
        try {
            CleaningOrder order = orderService.acceptOrder(orderId, cleanerId);
            long customerChatId = requireTelegramCustomerChatId(order);
            safeAnswer(callbackId, "Заказ №" + orderId + " принят вами.", false);
            safeSend(
                    cleanerId,
                    messageFactory.acceptedOrder(order),
                    messageFactory.acceptedOrderKeyboard(order, customerChatId),
                    orderId
            );
            safeSend(customerChatId, "Ваш заказ на уборку подтверждён ✅", orderId);
        } catch (OrderClaimConflictException exception) {
            CleaningOrder order = orderService.getOrderForConfiguredCleaner(orderId, cleanerId);
            String message;
            if (order.getCleanerTelegramUserId() != null
                    && order.getCleanerTelegramUserId() == cleanerId) {
                message = "Заказ №" + orderId + " уже принят вами.";
            } else if (order.getCleanerTelegramUserId() != null) {
                message = "Заказ №" + orderId + " уже принят другим клинером.";
            } else {
                message = "Заказ №" + orderId + " больше недоступен.";
            }
            safeAnswer(callbackId, message, true);
        }
    }

    private void finish(String callbackId, long orderId, long cleanerId) {
        CleaningOrder order = orderService.markAwaitingReport(orderId, cleanerId);
        safeAnswer(callbackId, "Уборка завершена. Отправьте фотоотчёт.", false);
        safeSend(cleanerId, messageFactory.awaitingPhotoReport(order), orderId);
    }

    private void cancel(String callbackId, long orderId, long cleanerId) {
        CleaningOrder order = orderService.cancelOrderByCleaner(orderId, cleanerId);
        long customerChatId = requireTelegramCustomerChatId(order);
        safeAnswer(callbackId, "Заказ №" + orderId + " отменён.", false);
        safeSend(cleanerId, "❌ Заказ №" + orderId + " отменён.", orderId);
        safeSend(customerChatId, "Заказ отменён.", orderId);
    }

    private void deliverReport(String callbackId, long orderId, long cleanerId) {
        CleaningOrderReport report = orderService.getReportForDelivery(orderId, cleanerId);
        CleaningOrder order = report.order();
        long customerChatId = requireTelegramCustomerChatId(order);
        safeAnswer(callbackId, "Отправляем отчёт клиенту.", false);

        botClient.sendMessage(
                customerChatId,
                messageFactory.customerReportHeader(order),
                TelegramBotClient.InlineKeyboard.empty()
        );
        for (String telegramFileId : report.telegramFileIds()) {
            botClient.sendPhoto(customerChatId, telegramFileId);
        }
        botClient.sendMessage(
                customerChatId,
                messageFactory.customerReportComment(order),
                TelegramBotClient.InlineKeyboard.empty()
        );

        orderService.completeOrder(orderId, cleanerId, order.getCleanerComment());
        safeSend(cleanerId, "✅ Отчёт по заказу №" + orderId + " отправлен клиенту.", orderId);
    }

    private void startOnsiteIssue(String callbackId, long orderId, long cleanerId) {
        onsiteIssueService.start(orderId, cleanerId);
        safeAnswer(callbackId, "Выберите причину проблемы.", false);
        safeSend(
                cleanerId,
                "Выберите причину, по которой уборку невозможно начать:",
                messageFactory.onsiteIssueReasonKeyboard(orderId),
                orderId
        );
    }

    private void selectOnsiteIssueReason(
            String callbackId,
            long orderId,
            long cleanerId,
            OnsiteIssueReason reason
    ) {
        OnsiteIssueProgress progress = onsiteIssueService.selectReason(orderId, cleanerId, reason);
        safeAnswer(callbackId, "Причина сохранена.", false);
        safeSend(
                cleanerId,
                messageFactory.onsiteIssueStarted(progress),
                messageFactory.onsiteIssueSubmitKeyboard(progress),
                orderId
        );
    }

    private void submitOnsiteIssue(String callbackId, long orderId, long cleanerId) {
        OnsiteIssueDelivery delivery = onsiteIssueService.submit(orderId, cleanerId);
        CleaningOrder order = delivery.order();
        long customerChatId = requireTelegramCustomerChatId(order);
        safeAnswer(callbackId, "Отчёт сохранён. Уведомляем клиента.", false);
        adminBotService.notifyOnsiteIssue(orderId, delivery.reason());

        botClient.sendMessage(
                customerChatId,
                messageFactory.customerOnsiteIssueReport(delivery.reason(), delivery.comment()),
                TelegramBotClient.InlineKeyboard.empty()
        );
        for (String telegramFileId : delivery.telegramFileIds()) {
            botClient.sendPhoto(customerChatId, telegramFileId);
        }
        botClient.sendMessage(
                customerChatId,
                messageFactory.customerOnsiteIssuePaused(),
                TelegramBotClient.InlineKeyboard.empty()
        );
        onsiteIssueService.recordCustomerNotified(orderId, cleanerId);
        safeSend(cleanerId, "⚠️ Отчёт по заказу №" + orderId + " сохранён и отправлен клиенту.", orderId);
    }

    private long requireTelegramCustomerChatId(CleaningOrder order) {
        var identity = customerIdentityRepository.findByIdAndCustomerId(
                order.getCommunicationIdentityId(),
                order.getCustomerId()
        ).orElseThrow(() -> new IllegalStateException(
                "Communication identity is unavailable for order " + order.getId()
        ));
        if (identity.getProvider() != ExternalIdentityProvider.TELEGRAM) {
            throw new IllegalStateException(
                    "Telegram delivery is unavailable for order " + order.getId()
            );
        }
        try {
            long telegramUserId = Long.parseLong(identity.getExternalSubject());
            if (telegramUserId <= 0) {
                throw new NumberFormatException("Telegram user id must be positive");
            }
            return telegramUserId;
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "Telegram communication identity is invalid for order " + order.getId(),
                    exception
            );
        }
    }

    private void safeSend(long chatId, String text, long orderId) {
        safeSend(chatId, text, TelegramBotClient.InlineKeyboard.empty(), orderId);
    }

    private void safeSend(long chatId, String text) {
        try {
            botClient.sendMessage(chatId, text, TelegramBotClient.InlineKeyboard.empty());
        } catch (TelegramBotApiException exception) {
            log.error("Telegram message delivery failed for chat {}", chatId, exception);
        }
    }

    private void safeSend(
            long chatId,
            String text,
            TelegramBotClient.InlineKeyboard keyboard,
            long orderId
    ) {
        try {
            botClient.sendMessage(chatId, text, keyboard);
        } catch (TelegramBotApiException exception) {
            log.error("Telegram message delivery failed for order {} and chat {}", orderId, chatId, exception);
        }
    }

    private void safeAnswer(String callbackId, String text, boolean showAlert) {
        try {
            botClient.answerCallbackQuery(callbackId, text, showAlert);
        } catch (TelegramBotApiException exception) {
            log.error("Telegram callback answer failed for callback {}", callbackId, exception);
        }
    }

    private static CallbackAction parseAction(String callbackData) {
        if (callbackData == null) {
            return null;
        }
        var reasonMatcher = ISSUE_REASON_CALLBACK_PATTERN.matcher(callbackData);
        if (reasonMatcher.matches()) {
            try {
                return new CallbackAction(
                        "issue_reason",
                        Long.parseLong(reasonMatcher.group(2)),
                        OnsiteIssueReason.valueOf(reasonMatcher.group(1))
                );
            } catch (IllegalArgumentException exception) {
                return null;
            }
        }
        var matcher = CALLBACK_PATTERN.matcher(callbackData);
        if (!matcher.matches()) {
            return null;
        }
        try {
            return new CallbackAction(matcher.group(1), Long.parseLong(matcher.group(2)), null);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static PhotoSize largestPhoto(Message message) {
        return message.photo().stream()
                .filter(photo -> photo.fileId() != null && photo.fileUniqueId() != null)
                .max(Comparator
                        .comparingLong((PhotoSize photo) -> (long) photo.width() * photo.height())
                        .thenComparingLong(photo -> photo.fileSize() == null ? 0L : photo.fileSize()))
                .orElse(null);
    }

    private static boolean isCommand(String text, String expectedCommand) {
        if (text == null) {
            return false;
        }
        return text.equals(expectedCommand) || text.startsWith(expectedCommand + "@");
    }

    private static String onsiteIssueError(InvalidOnsiteIssueException exception) {
        return switch (exception.getProblem()) {
            case REASON_REQUIRED -> "Выберите причину проблемы на объекте.";
            case COMMENT_REQUIRED -> "Добавьте обязательный комментарий к проблеме.";
            case MIN_PHOTOS_REQUIRED -> "Для отчёта нужно минимум 3 фотографии.";
            case MAX_PHOTOS_EXCEEDED -> "Можно приложить не более 8 фотографий.";
            case PHOTO_EMPTY -> "Фотография пуста или не содержит данных.";
            case PHOTO_TOO_LARGE -> "Размер фотографии превышает 5 МБ.";
            case PHOTO_TYPE_UNSUPPORTED -> "Поддерживаются только фотографии JPEG и PNG.";
            case COLLECTION_NOT_ACTIVE -> "Нет активного отчёта о проблеме на объекте.";
        };
    }

    private record CallbackAction(String name, long orderId, OnsiteIssueReason reason) {
    }
}
