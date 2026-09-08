package com.cleany.telegram.bot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TelegramBotUpdateRouterTest {

    private TransferDriverBotService transferDriverBotService;
    private TelegramCustomerBotService customerBotService;
    private TelegramCleanerBotService cleanerBotService;
    private TelegramBotUpdateRouter router;
    private TelegramUpdate update;

    @BeforeEach
    void setUp() {
        transferDriverBotService = Mockito.mock(TransferDriverBotService.class);
        customerBotService = Mockito.mock(TelegramCustomerBotService.class);
        cleanerBotService = Mockito.mock(TelegramCleanerBotService.class);
        router = new TelegramBotUpdateRouter(
                transferDriverBotService,
                customerBotService,
                cleanerBotService
        );
        update = new TelegramUpdate(1L, null, null);
    }

    @Test
    void driverStart_hasPriorityOverCustomerWelcome() {
        Mockito.when(transferDriverBotService.handleIfSupported(update)).thenReturn(true);

        router.handle(update);

        Mockito.verifyNoInteractions(customerBotService, cleanerBotService);
    }

    @Test
    void customerCommand_consumedBeforeCleanerFlow() {
        Mockito.when(customerBotService.handleIfSupported(update)).thenReturn(true);

        router.handle(update);

        Mockito.verify(transferDriverBotService).handleIfSupported(update);
        Mockito.verify(customerBotService).handleIfSupported(update);
        Mockito.verifyNoInteractions(cleanerBotService);
    }

    @Test
    void operationalUpdate_fallsThroughToCleanerFlow() {
        router.handle(update);

        Mockito.verify(transferDriverBotService).handleIfSupported(update);
        Mockito.verify(customerBotService).handleIfSupported(update);
        Mockito.verify(cleanerBotService).handle(update);
    }
}
