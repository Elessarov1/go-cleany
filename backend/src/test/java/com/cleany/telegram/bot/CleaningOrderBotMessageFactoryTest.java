package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.order.ApartmentType;
import com.cleany.order.CleaningOrder;
import com.cleany.order.CleaningOrderCustomerNotification;
import com.cleany.order.CleaningOrderReportProgress;
import com.cleany.order.CleaningType;
import com.cleany.order.ServiceArea;

class CleaningOrderBotMessageFactoryTest {

    private final CleaningOrderBotMessageFactory messageFactory = new CleaningOrderBotMessageFactory();

    @Test
    void newOrder_customerDetailsAndMinimalCallbacksRendered() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(43L);
        Mockito.when(order.getRequestedDate()).thenReturn(LocalDate.of(2026, 8, 18));
        Mockito.when(order.getArea()).thenReturn(ServiceArea.MAHMUTLAR);
        Mockito.when(order.getApartmentType()).thenReturn(ApartmentType.TWO_PLUS_ONE);
        Mockito.when(order.isDuplex()).thenReturn(false);
        Mockito.when(order.getCleaningType()).thenReturn(CleaningType.REGULAR);
        Mockito.when(order.getFinalCustomerPrice()).thenReturn(BigDecimal.valueOf(1100));
        Mockito.when(order.getBasePrice()).thenReturn(BigDecimal.valueOf(1100));
        Mockito.when(order.getBaseCommission()).thenReturn(BigDecimal.valueOf(165));
        Mockito.when(order.getCurrency()).thenReturn("TRY");
        Mockito.when(order.getAddress()).thenReturn("Barbaros Cd. 24");
        Mockito.when(order.getPhone()).thenReturn("+90 555 123 45 67");
        Mockito.when(order.getCustomerComment()).thenReturn(null);

        String text = messageFactory.newOrder(order);
        var keyboard = messageFactory.newOrderKeyboard(43L);

        Assertions.assertAll(
                () -> Assertions.assertTrue(text.contains("Заказ №43")),
                () -> Assertions.assertTrue(text.contains("18 августа")),
                () -> Assertions.assertTrue(text.contains("Махмутлар")),
                () -> Assertions.assertTrue(text.contains("2+1")),
                () -> Assertions.assertTrue(text.contains("1100 ₺")),
                () -> Assertions.assertEquals(
                        "order:accept:43",
                        keyboard.rows().getFirst().getFirst().callbackData()
                ),
                () -> Assertions.assertEquals(
                        "order:skip:43",
                        keyboard.rows().getFirst().get(1).callbackData()
                )
        );
    }

    @Test
    void awaitingPhotoReport_nextStepsRendered() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(43L);

        String text = messageFactory.awaitingPhotoReport(order);

        Assertions.assertAll(
                () -> Assertions.assertTrue(text.contains("Отправьте фотографии выполненной уборки.")),
                () -> Assertions.assertTrue(text.contains("Можно отправить несколько фотографий.")),
                () -> Assertions.assertTrue(text.contains("После этого можно добавить комментарий."))
        );
    }

    @Test
    void acceptedOrderKeyboard_resolvedCommunicationIdentityUsedForTelegramContact() {
        CleaningOrder order = Mockito.mock(CleaningOrder.class);
        Mockito.when(order.getId()).thenReturn(43L);

        var keyboard = messageFactory.acceptedOrderKeyboard(order, 900001L);

        Assertions.assertEquals(
                "tg://user?id=900001",
                keyboard.rows().getFirst().getFirst().url()
        );
    }

    @Test
    void reportReady_customerMessageAndCallbackRendered() {
        var notification = new CleaningOrderCustomerNotification.Completed(
                43L,
                ApartmentType.TWO_PLUS_ONE,
                false,
                ServiceArea.MAHMUTLAR,
                LocalDate.of(2026, 8, 18),
                "Everything is ready",
                List.of()
        );
        var progress = new CleaningOrderReportProgress(43L, 2L, true);

        String saved = messageFactory.photoSaved(progress);
        String header = messageFactory.customerReportHeader(notification);
        String comment = messageFactory.customerReportComment(notification);
        var keyboard = messageFactory.reportReadyKeyboard(43L);

        Assertions.assertAll(
                () -> Assertions.assertTrue(saved.contains("Всего фотографий: 2")),
                () -> Assertions.assertTrue(header.contains("Уборка завершена")),
                () -> Assertions.assertTrue(header.contains("2+1")),
                () -> Assertions.assertTrue(header.contains("Махмутлар")),
                () -> Assertions.assertEquals("Комментарий клинера:\nEverything is ready", comment),
                () -> Assertions.assertEquals(
                        "order:report:43",
                        keyboard.rows().getFirst().getFirst().callbackData()
                )
        );
    }
}
