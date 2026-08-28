package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.admin.AdminAccessService;
import com.cleany.admin.AdminDashboardResponse;
import com.cleany.admin.AdminQueryService;
import com.cleany.admin.AdminStatsResponse;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.order.OnsiteIssueReason;
import com.cleany.telegram.bot.TelegramBotClient.InlineKeyboard;

class TelegramAdminBotServiceTest {

    private static final long ADMIN_ID = 900001L;
    private static final long ADMIN_CUSTOMER_ID = 77L;

    private AdminAccessService accessService;
    private AdminQueryService queryService;
    private CustomerAccountService customerAccountService;
    private AdminBotMessageFactory messageFactory;
    private TelegramBotClient botClient;
    private AdminTelegramRecipientService recipientService;
    private TelegramAdminBotService service;

    @BeforeEach
    void setUp() {
        accessService = Mockito.mock(AdminAccessService.class);
        queryService = Mockito.mock(AdminQueryService.class);
        customerAccountService = Mockito.mock(CustomerAccountService.class);
        messageFactory = Mockito.mock(AdminBotMessageFactory.class);
        botClient = Mockito.mock(TelegramBotClient.class);
        recipientService = Mockito.mock(AdminTelegramRecipientService.class);
        service = new TelegramAdminBotService(
                accessService,
                recipientService,
                customerAccountService,
                queryService,
                messageFactory,
                botClient
        );
    }

    @Test
    void unsupportedMessage_notHandled() {
        Assertions.assertFalse(service.handleIfSupported(user(ADMIN_ID), "обычное сообщение"));
        Mockito.verifyNoInteractions(accessService, queryService, messageFactory, botClient);
    }

    @Test
    void adminCommand_userOutsideWhitelist_receivesDenial() {
        stubCustomer(777L, 78L);
        Mockito.when(accessService.isAdmin(78L)).thenReturn(false);

        Assertions.assertTrue(service.handleIfSupported(user(777L), "/stats"));

        Mockito.verify(botClient).sendMessage(
                777L,
                "Эта команда доступна только администратору.",
                InlineKeyboard.empty()
        );
        Mockito.verifyNoInteractions(queryService);
    }

    @Test
    void statsCommand_authorizedAdmin_receivesFormattedStats() {
        var stats = new AdminStatsResponse(5, 2, 1, 2, 1, 1, BigDecimal.valueOf(1100), "TRY");
        var dashboard = new AdminDashboardResponse(stats, List.of());
        stubCustomer(ADMIN_ID, ADMIN_CUSTOMER_ID);
        Mockito.when(accessService.isAdmin(ADMIN_CUSTOMER_ID)).thenReturn(true);
        Mockito.when(queryService.getDashboard(ADMIN_CUSTOMER_ID, 1)).thenReturn(dashboard);
        Mockito.when(messageFactory.stats(stats)).thenReturn("stats-message");

        Assertions.assertTrue(service.handleIfSupported(user(ADMIN_ID), "/stats@go_cleany_bot"));

        Mockito.verify(botClient).sendMessage(ADMIN_ID, "stats-message", InlineKeyboard.empty());
    }

    @Test
    void orderCommand_withoutNumber_explainsExpectedFormat() {
        stubCustomer(ADMIN_ID, ADMIN_CUSTOMER_ID);
        Mockito.when(accessService.isAdmin(ADMIN_CUSTOMER_ID)).thenReturn(true);

        Assertions.assertTrue(service.handleIfSupported(user(ADMIN_ID), "/order"));

        Mockito.verify(botClient).sendMessage(
                ADMIN_ID,
                "Используйте команду в формате /order <номер заказа>.",
                InlineKeyboard.empty()
        );
        Mockito.verifyNoInteractions(queryService);
    }

    @Test
    void onsiteIssue_allEligiblePersistedAdminsReceiveAlert() {
        Mockito.when(messageFactory.onsiteIssueAlert(43L, OnsiteIssueReason.ACCESS_PROBLEM))
                .thenReturn("onsite-issue-alert");
        Mockito.when(recipientService.recipients()).thenReturn(List.of(ADMIN_ID, 900002L));

        service.notifyOnsiteIssue(43L, OnsiteIssueReason.ACCESS_PROBLEM);

        Mockito.verify(botClient).sendMessage(ADMIN_ID, "onsite-issue-alert", InlineKeyboard.empty());
        Mockito.verify(botClient).sendMessage(900002L, "onsite-issue-alert", InlineKeyboard.empty());
    }

    private void stubCustomer(long telegramId, long customerId) {
        Mockito.when(customerAccountService.resolveCustomer(Mockito.any())).thenReturn(new CurrentCustomer(
                customerId,
                customerId + 100,
                ExternalIdentityProvider.TELEGRAM,
                Long.toString(telegramId),
                "alex",
                "Alex",
                "ru"
        ));
    }

    private static TelegramUpdate.TelegramUser user(long telegramId) {
        return new TelegramUpdate.TelegramUser(
                telegramId,
                "alex",
                "Alex",
                null,
                "ru"
        );
    }
}
