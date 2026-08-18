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
import com.cleany.telegram.bot.TelegramBotClient.InlineKeyboard;

class TelegramAdminBotServiceTest {

    private static final long ADMIN_ID = 900001L;

    private AdminAccessService accessService;
    private AdminQueryService queryService;
    private AdminBotMessageFactory messageFactory;
    private TelegramBotClient botClient;
    private TelegramAdminBotService service;

    @BeforeEach
    void setUp() {
        accessService = Mockito.mock(AdminAccessService.class);
        queryService = Mockito.mock(AdminQueryService.class);
        messageFactory = Mockito.mock(AdminBotMessageFactory.class);
        botClient = Mockito.mock(TelegramBotClient.class);
        service = new TelegramAdminBotService(accessService, queryService, messageFactory, botClient);
    }

    @Test
    void unsupportedMessage_notHandled() {
        Assertions.assertFalse(service.handleIfSupported(ADMIN_ID, "обычное сообщение"));
        Mockito.verifyNoInteractions(accessService, queryService, messageFactory, botClient);
    }

    @Test
    void adminCommand_userOutsideWhitelist_receivesDenial() {
        Mockito.when(accessService.isAdmin(777L)).thenReturn(false);

        Assertions.assertTrue(service.handleIfSupported(777L, "/stats"));

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
        Mockito.when(accessService.isAdmin(ADMIN_ID)).thenReturn(true);
        Mockito.when(queryService.getDashboard(ADMIN_ID, 1)).thenReturn(dashboard);
        Mockito.when(messageFactory.stats(stats)).thenReturn("stats-message");

        Assertions.assertTrue(service.handleIfSupported(ADMIN_ID, "/stats@go_cleany_bot"));

        Mockito.verify(botClient).sendMessage(ADMIN_ID, "stats-message", InlineKeyboard.empty());
    }

    @Test
    void orderCommand_withoutNumber_explainsExpectedFormat() {
        Mockito.when(accessService.isAdmin(ADMIN_ID)).thenReturn(true);

        Assertions.assertTrue(service.handleIfSupported(ADMIN_ID, "/order"));

        Mockito.verify(botClient).sendMessage(
                ADMIN_ID,
                "Используйте команду в формате /order <номер заказа>.",
                InlineKeyboard.empty()
        );
        Mockito.verifyNoInteractions(queryService);
    }
}
