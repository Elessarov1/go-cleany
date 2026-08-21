package com.cleany.telegram.bot;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.configuration.CleanerProperties;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentity;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderReport;
import com.cleany.order.CleaningOrderReportProgress;
import com.cleany.order.CleaningOrderService;
import com.cleany.order.OrderClaimConflictException;
import com.cleany.order.OnsiteIssueDelivery;
import com.cleany.order.OnsiteIssueProgress;
import com.cleany.order.OnsiteIssueReason;
import com.cleany.order.OnsiteIssueService;
import com.cleany.order.PhotoReportEmptyException;
import com.cleany.telegram.bot.TelegramBotClient.InlineButton;
import com.cleany.telegram.bot.TelegramBotClient.InlineKeyboard;
import com.cleany.telegram.bot.TelegramUpdate.CallbackQuery;
import com.cleany.telegram.bot.TelegramUpdate.Chat;
import com.cleany.telegram.bot.TelegramUpdate.Contact;
import com.cleany.telegram.bot.TelegramUpdate.Message;
import com.cleany.telegram.bot.TelegramUpdate.PhotoSize;
import com.cleany.telegram.bot.TelegramUpdate.TelegramUser;

class TelegramCleanerBotServiceTest {

    private static final long CLEANER_ID = 101L;
    private static final long OTHER_CLEANER_ID = 102L;
    private static final long CUSTOMER_ID = 900001L;
    private static final long CUSTOMER_ACCOUNT_ID = 77L;
    private static final long COMMUNICATION_ID = 501L;

    private CleaningOrderService orderService;
    private CleaningOrderBotMessageFactory messageFactory;
    private TelegramBotClient botClient;
    private TelegramAdminBotService adminBotService;
    private CustomerAccountService customerAccountService;
    private CustomerExternalIdentityRepository customerIdentityRepository;
    private OnsiteIssueService onsiteIssueService;
    private TelegramCleanerBotService cleanerBotService;

    @BeforeEach
    void setUp() {
        orderService = Mockito.mock(CleaningOrderService.class);
        messageFactory = Mockito.mock(CleaningOrderBotMessageFactory.class);
        botClient = Mockito.mock(TelegramBotClient.class);
        adminBotService = Mockito.mock(TelegramAdminBotService.class);
        customerAccountService = Mockito.mock(CustomerAccountService.class);
        customerIdentityRepository = Mockito.mock(CustomerExternalIdentityRepository.class);
        onsiteIssueService = Mockito.mock(OnsiteIssueService.class);
        CustomerExternalIdentity customerIdentity = Mockito.mock(CustomerExternalIdentity.class);
        Mockito.when(customerIdentity.getProvider()).thenReturn(ExternalIdentityProvider.TELEGRAM);
        Mockito.when(customerIdentity.getExternalSubject()).thenReturn(Long.toString(CUSTOMER_ID));
        Mockito.when(customerIdentityRepository.findByIdAndCustomerId(
                COMMUNICATION_ID,
                CUSTOMER_ACCOUNT_ID
        ))
                .thenReturn(Optional.of(customerIdentity));
        cleanerBotService = new TelegramCleanerBotService(
                new CleanerProperties(List.of(CLEANER_ID, OTHER_CLEANER_ID)),
                orderService,
                messageFactory,
                botClient,
                adminBotService,
                customerAccountService,
                customerIdentityRepository,
                onsiteIssueService
        );
    }

    @Test
    void callbackFromUserOutsideWhitelist_actionRejected() {
        cleanerBotService.handle(update(777L, "order:accept:43"));

        Mockito.verify(botClient).answerCallbackQuery(
                "callback-1",
                "Вы не авторизованы как клинер.",
                true
        );
        Mockito.verifyNoInteractions(orderService);
    }

    @Test
    void skipCallback_globalOrderStateNotChanged() {
        cleanerBotService.handle(update(CLEANER_ID, "order:skip:43"));

        Mockito.verify(botClient).answerCallbackQuery("callback-1", "Заказ пропущен.", false);
        Mockito.verifyNoInteractions(orderService);
    }

    @Test
    void acceptCallback_firstCleanerWins_customerAndCleanerNotified() {
        CleaningOrder order = order(43L, CLEANER_ID);
        InlineKeyboard keyboard = InlineKeyboard.ofRows(List.of(
                InlineButton.callback("Finish", "order:finish:43")
        ));
        Mockito.when(orderService.acceptOrder(43L, CLEANER_ID)).thenReturn(order);
        Mockito.when(messageFactory.acceptedOrder(order)).thenReturn("accepted-message");
        Mockito.when(messageFactory.acceptedOrderKeyboard(order, CUSTOMER_ID)).thenReturn(keyboard);

        cleanerBotService.handle(update(CLEANER_ID, "order:accept:43"));

        Mockito.verify(botClient).answerCallbackQuery(
                "callback-1",
                "Заказ №43 принят вами.",
                false
        );
        Mockito.verify(botClient).sendMessage(CLEANER_ID, "accepted-message", keyboard);
        Mockito.verify(botClient).sendMessage(
                CUSTOMER_ID,
                "Ваш заказ на уборку подтверждён ✅",
                InlineKeyboard.empty()
        );
    }

    @Test
    void acceptCallback_secondCleanerLosesRace_conflictReported() {
        CleaningOrder acceptedOrder = order(43L, OTHER_CLEANER_ID);
        Mockito.when(orderService.acceptOrder(43L, CLEANER_ID))
                .thenThrow(new OrderClaimConflictException(43L));
        Mockito.when(orderService.getOrderForConfiguredCleaner(43L, CLEANER_ID))
                .thenReturn(acceptedOrder);

        cleanerBotService.handle(update(CLEANER_ID, "order:accept:43"));

        Mockito.verify(botClient).answerCallbackQuery(
                "callback-1",
                "Заказ №43 уже принят другим клинером.",
                true
        );
        Mockito.verify(botClient, Mockito.never()).sendMessage(
                Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.any()
        );
    }

    @Test
    void finishCallback_assignedOrderMovedToAwaitingReport() {
        CleaningOrder order = order(43L, CLEANER_ID);
        Mockito.when(orderService.markAwaitingReport(43L, CLEANER_ID)).thenReturn(order);
        Mockito.when(messageFactory.awaitingPhotoReport(order)).thenReturn("send-photos");

        cleanerBotService.handle(update(CLEANER_ID, "order:finish:43"));

        Mockito.verify(orderService).markAwaitingReport(43L, CLEANER_ID);
        Mockito.verify(botClient).sendMessage(CLEANER_ID, "send-photos", InlineKeyboard.empty());
    }

    @Test
    void cancelCallback_assignedOrderCancelled_customerNotified() {
        CleaningOrder order = order(43L, CLEANER_ID);
        Mockito.when(orderService.cancelOrderByCleaner(43L, CLEANER_ID)).thenReturn(order);

        cleanerBotService.handle(update(CLEANER_ID, "order:cancel:43"));

        Mockito.verify(orderService).cancelOrderByCleaner(43L, CLEANER_ID);
        Mockito.verify(botClient).sendMessage(
                CUSTOMER_ID,
                "Заказ отменён.",
                InlineKeyboard.empty()
        );
    }

    @Test
    void malformedCallback_actionRejectedWithoutOrderLookup() {
        cleanerBotService.handle(update(CLEANER_ID, "order:accept:not-a-number"));

        Mockito.verify(botClient).answerCallbackQuery(
                "callback-1",
                "Это действие не поддерживается.",
                true
        );
        Mockito.verifyNoInteractions(orderService);
    }

    @Test
    void photoMessage_largestTelegramSizeStoredForActiveReport() {
        var progress = new CleaningOrderReportProgress(43L, 1L, true);
        InlineKeyboard keyboard = InlineKeyboard.ofRows(List.of(
                InlineButton.callback("Send", "order:report:43")
        ));
        Mockito.when(orderService.addPhotoToActiveReport(
                CLEANER_ID,
                "large-file",
                "large-unique",
                "Everything is ready"
        )).thenReturn(progress);
        Mockito.when(messageFactory.photoSaved(progress)).thenReturn("photo-saved");
        Mockito.when(messageFactory.reportReadyKeyboard(43L)).thenReturn(keyboard);

        cleanerBotService.handle(photoUpdate(CLEANER_ID));

        Mockito.verify(orderService).addPhotoToActiveReport(
                CLEANER_ID,
                "large-file",
                "large-unique",
                "Everything is ready"
        );
        Mockito.verify(botClient).sendMessage(CLEANER_ID, "photo-saved", keyboard);
    }

    @Test
    void textMessage_activeReportCommentUpdated() {
        var progress = new CleaningOrderReportProgress(43L, 1L, true);
        InlineKeyboard keyboard = InlineKeyboard.ofRows(List.of(
                InlineButton.callback("Send", "order:report:43")
        ));
        Mockito.when(orderService.updateActiveReportComment(CLEANER_ID, "Looks good"))
                .thenReturn(progress);
        Mockito.when(messageFactory.commentSaved(progress)).thenReturn("comment-saved");
        Mockito.when(messageFactory.reportReadyKeyboard(43L)).thenReturn(keyboard);

        cleanerBotService.handle(textUpdate(CLEANER_ID, "Looks good"));

        Mockito.verify(orderService).updateActiveReportComment(CLEANER_ID, "Looks good");
        Mockito.verify(botClient).sendMessage(CLEANER_ID, "comment-saved", keyboard);
    }

    @Test
    void photoMessage_userOutsideWhitelist_ignored() {
        cleanerBotService.handle(photoUpdate(777L));

        Mockito.verifyNoInteractions(orderService, botClient);
    }

    @Test
    void whoAmICommand_userOutsideWhitelist_receivesOwnTelegramId() {
        cleanerBotService.handle(textUpdate(777L, "/whoami"));

        Mockito.verify(botClient).sendMessage(
                777L,
                "Ваш Telegram ID: 777",
                InlineKeyboard.empty()
        );
        Mockito.verifyNoInteractions(orderService);
    }

    @Test
    void adminCommand_delegatedBeforeCleanerWhitelistCheck() {
        Mockito.when(adminBotService.handleIfSupported(777L, "/stats")).thenReturn(true);

        cleanerBotService.handle(textUpdate(777L, "/stats"));

        Mockito.verify(adminBotService).handleIfSupported(777L, "/stats");
        Mockito.verifyNoInteractions(orderService, botClient);
    }

    @Test
    void ownTelegramContact_phoneSavedForCustomerProfile() {
        cleanerBotService.handle(contactUpdate(777L, "customer", "Alex", "Customer", "905551234567"));

        Mockito.verify(customerAccountService).savePhoneForExternalIdentity(
                ExternalIdentityProvider.TELEGRAM,
                "777",
                "customer",
                "Alex Customer",
                "ru",
                "+905551234567"
        );
        Mockito.verify(botClient).sendMessage(
                777L,
                "Номер телефона сохранён и будет подставлен в форму заказа.",
                InlineKeyboard.empty()
        );
        Mockito.verifyNoInteractions(orderService);
    }

    @Test
    void reportCallback_photosDeliveredBeforeOrderCompleted() {
        CleaningOrder order = order(43L, CLEANER_ID);
        Mockito.when(order.getCleanerComment()).thenReturn("Everything is ready");
        Mockito.when(orderService.getReportForDelivery(43L, CLEANER_ID))
                .thenReturn(new CleaningOrderReport(order, List.of("file-1", "file-2")));
        Mockito.when(messageFactory.customerReportHeader(order)).thenReturn("report-header");
        Mockito.when(messageFactory.customerReportComment(order)).thenReturn("report-comment");

        cleanerBotService.handle(update(CLEANER_ID, "order:report:43"));

        var deliveryOrder = Mockito.inOrder(botClient, orderService);
        deliveryOrder.verify(botClient).sendMessage(
                CUSTOMER_ID,
                "report-header",
                InlineKeyboard.empty()
        );
        deliveryOrder.verify(botClient).sendPhoto(CUSTOMER_ID, "file-1");
        deliveryOrder.verify(botClient).sendPhoto(CUSTOMER_ID, "file-2");
        deliveryOrder.verify(botClient).sendMessage(
                CUSTOMER_ID,
                "report-comment",
                InlineKeyboard.empty()
        );
        deliveryOrder.verify(orderService).completeOrder(43L, CLEANER_ID, "Everything is ready");
    }

    @Test
    void reportCallback_photoDeliveryFails_orderNotCompleted() {
        CleaningOrder order = order(43L, CLEANER_ID);
        Mockito.when(orderService.getReportForDelivery(43L, CLEANER_ID))
                .thenReturn(new CleaningOrderReport(order, List.of("file-1")));
        Mockito.when(messageFactory.customerReportHeader(order)).thenReturn("report-header");
        Mockito.doThrow(new TelegramBotApiException("delivery failed"))
                .when(botClient).sendPhoto(CUSTOMER_ID, "file-1");

        Assertions.assertThrows(
                TelegramBotApiException.class,
                () -> cleanerBotService.handle(update(CLEANER_ID, "order:report:43"))
        );

        Mockito.verify(orderService, Mockito.never()).completeOrder(
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.any()
        );
    }

    @Test
    void reportCallback_withoutPhotos_actionRejected() {
        Mockito.when(orderService.getReportForDelivery(43L, CLEANER_ID))
                .thenThrow(new PhotoReportEmptyException(43L));

        cleanerBotService.handle(update(CLEANER_ID, "order:report:43"));

        Mockito.verify(botClient).answerCallbackQuery(
                "callback-1",
                "Перед отправкой отчёта добавьте хотя бы одну фотографию.",
                true
        );
        Mockito.verify(orderService, Mockito.never()).completeOrder(
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.any()
        );
    }

    @Test
    void activeOnsiteIssue_photoDownloadedAndStoredAsEvidence() {
        byte[] content = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xD9};
        OnsiteIssueProgress progress = new OnsiteIssueProgress(
                43L,
                OnsiteIssueReason.HEAVY_CONTAMINATION,
                1L,
                true,
                false
        );
        InlineKeyboard keyboard = InlineKeyboard.empty();
        Mockito.when(onsiteIssueService.hasActiveDraft(CLEANER_ID)).thenReturn(true);
        Mockito.when(botClient.downloadFile("large-file")).thenReturn(content);
        Mockito.when(onsiteIssueService.addPhoto(
                CLEANER_ID,
                "large-file",
                "large-unique",
                content,
                "Everything is ready"
        )).thenReturn(progress);
        Mockito.when(messageFactory.onsiteIssueProgress(progress)).thenReturn("issue-progress");
        Mockito.when(messageFactory.onsiteIssueSubmitKeyboard(progress)).thenReturn(keyboard);

        cleanerBotService.handle(photoUpdate(CLEANER_ID));

        var processingOrder = Mockito.inOrder(botClient, onsiteIssueService);
        processingOrder.verify(botClient).downloadFile("large-file");
        processingOrder.verify(onsiteIssueService).addPhoto(
                CLEANER_ID,
                "large-file",
                "large-unique",
                content,
                "Everything is ready"
        );
        processingOrder.verify(botClient).sendMessage(CLEANER_ID, "issue-progress", keyboard);
        Mockito.verifyNoInteractions(orderService);
    }

    @Test
    void onsiteIssueSubmit_customerReceivesReasonCommentAndEvidenceBeforeNotificationAudit() {
        CleaningOrder order = order(43L, CLEANER_ID);
        OnsiteIssueDelivery delivery = new OnsiteIssueDelivery(
                order,
                OnsiteIssueReason.ADDRESS_MISMATCH,
                "Wrong address",
                List.of("evidence-1", "evidence-2", "evidence-3")
        );
        Mockito.when(onsiteIssueService.submit(43L, CLEANER_ID)).thenReturn(delivery);
        Mockito.when(messageFactory.customerOnsiteIssueReport(
                OnsiteIssueReason.ADDRESS_MISMATCH,
                "Wrong address"
        )).thenReturn("issue-report");
        Mockito.when(messageFactory.customerOnsiteIssuePaused()).thenReturn("order-paused");

        cleanerBotService.handle(update(CLEANER_ID, "order:issue_submit:43"));

        var deliveryOrder = Mockito.inOrder(onsiteIssueService, botClient);
        deliveryOrder.verify(onsiteIssueService).submit(43L, CLEANER_ID);
        deliveryOrder.verify(botClient).sendMessage(CUSTOMER_ID, "issue-report", InlineKeyboard.empty());
        deliveryOrder.verify(botClient).sendPhoto(CUSTOMER_ID, "evidence-1");
        deliveryOrder.verify(botClient).sendPhoto(CUSTOMER_ID, "evidence-2");
        deliveryOrder.verify(botClient).sendPhoto(CUSTOMER_ID, "evidence-3");
        deliveryOrder.verify(botClient).sendMessage(CUSTOMER_ID, "order-paused", InlineKeyboard.empty());
        deliveryOrder.verify(onsiteIssueService).recordCustomerNotified(43L, CLEANER_ID);
        Mockito.verify(adminBotService).notifyOnsiteIssue(43L, OnsiteIssueReason.ADDRESS_MISMATCH);
    }

    private static TelegramUpdate update(long cleanerId, String data) {
        return new TelegramUpdate(
                1L,
                new CallbackQuery(
                        "callback-1",
                        new TelegramUser(cleanerId, null, "Cleaner", null, "ru"),
                        data
                ),
                null
        );
    }

    private static TelegramUpdate photoUpdate(long cleanerId) {
        return new TelegramUpdate(
                2L,
                null,
                new Message(
                        55L,
                        new TelegramUser(cleanerId, null, "Cleaner", null, "ru"),
                        new Chat(cleanerId, "private"),
                        null,
                        "Everything is ready",
                        List.of(
                                new PhotoSize("small-file", "small-unique", 90, 90, 1200L),
                                new PhotoSize("large-file", "large-unique", 1280, 960, 250000L)
                        ),
                        null
                )
        );
    }

    private static TelegramUpdate textUpdate(long cleanerId, String text) {
        return new TelegramUpdate(
                3L,
                null,
                new Message(
                        56L,
                        new TelegramUser(cleanerId, null, "Cleaner", null, "ru"),
                        new Chat(cleanerId, "private"),
                        text,
                        null,
                        List.of(),
                        null
                )
        );
    }

    private static TelegramUpdate contactUpdate(
            long userId,
            String username,
            String firstName,
            String lastName,
            String phone
    ) {
        return new TelegramUpdate(
                4L,
                null,
                new Message(
                        57L,
                        new TelegramUser(userId, username, firstName, lastName, "ru"),
                        new Chat(userId, "private"),
                        null,
                        null,
                        List.of(),
                        new Contact(phone, userId)
                )
        );
    }

    private static CleaningOrder order(long orderId, long cleanerId) {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(orderId);
        Mockito.when(order.getCustomerId()).thenReturn(CUSTOMER_ACCOUNT_ID);
        Mockito.when(order.getCommunicationIdentityId()).thenReturn(COMMUNICATION_ID);
        Mockito.when(order.getCleanerTelegramUserId()).thenReturn(cleanerId);
        return order;
    }
}
