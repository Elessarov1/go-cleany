package com.cleany.telegram.bot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.configuration.AdminProperties;
import com.cleany.rental.RentalAdminNotificationPreferenceService;
import com.cleany.rental.RentalBookingAdminEvent;
import com.cleany.rental.RentalBookingAdminNotification;
import com.cleany.rental.RentalTermType;

class TelegramRentalAdminNotificationSenderTest {

    private final RentalAdminNotificationPreferenceService preferenceService =
            Mockito.mock(RentalAdminNotificationPreferenceService.class);
    private final TelegramBotClient botClient = Mockito.mock(TelegramBotClient.class);
    private final TelegramRentalAdminNotificationSender sender =
            new TelegramRentalAdminNotificationSender(
                    new AdminProperties(List.of(1001L, 1002L, 1003L), List.of()),
                    preferenceService,
                    new TelegramRentalAdminMessageFactory(),
                    botClient
            );

    @Test
    void disabledAdmin_isSkippedWhileEveryEnabledAdminReceivesMessage() {
        Mockito.when(preferenceService.enabledAdminIds(List.of(1001L, 1002L, 1003L)))
                .thenReturn(List.of(1001L, 1003L));

        sender.send(RentalBookingAdminEvent.Type.CREATED, notification());

        Mockito.verify(botClient).sendMessage(
                Mockito.eq(1001L),
                Mockito.contains("Новое бронирование go-renty"),
                Mockito.eq(TelegramBotClient.InlineKeyboard.empty())
        );
        Mockito.verify(botClient, Mockito.never()).sendMessage(
                Mockito.eq(1002L),
                Mockito.anyString(),
                Mockito.any()
        );
        Mockito.verify(botClient).sendMessage(
                Mockito.eq(1003L),
                Mockito.contains("Новое бронирование go-renty"),
                Mockito.eq(TelegramBotClient.InlineKeyboard.empty())
        );
    }

    @Test
    void oneTelegramFailure_doesNotPreventDeliveryToOtherAdmins() {
        Mockito.when(preferenceService.enabledAdminIds(List.of(1001L, 1002L, 1003L)))
                .thenReturn(List.of(1001L, 1003L));
        Mockito.doThrow(new TelegramBotApiException("unavailable"))
                .when(botClient)
                .sendMessage(
                        Mockito.eq(1001L),
                        Mockito.anyString(),
                        Mockito.any()
                );

        Assertions.assertDoesNotThrow(() -> sender.send(
                RentalBookingAdminEvent.Type.CANCELLED_BY_CUSTOMER,
                notification()
        ));
        Mockito.verify(botClient).sendMessage(
                Mockito.eq(1003L),
                Mockito.anyString(),
                Mockito.eq(TelegramBotClient.InlineKeyboard.empty())
        );
    }

    private static RentalBookingAdminNotification notification() {
        return new RentalBookingAdminNotification(
                42L,
                "Sea View 1+1",
                "Alexandr",
                "+90 555 123 45 67",
                RentalTermType.DATE_RANGE,
                LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 9, 29),
                null,
                14,
                new BigDecimal("2000.00"),
                null,
                new BigDecimal("28000.00"),
                "TRY"
        );
    }
}
