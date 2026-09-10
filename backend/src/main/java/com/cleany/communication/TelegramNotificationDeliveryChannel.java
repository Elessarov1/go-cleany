package com.cleany.communication;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.notification.CommunicationTarget;
import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationSender;
import com.cleany.telegram.bot.TelegramBotApiException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
class TelegramNotificationDeliveryChannel implements NotificationDeliveryChannel {
    private final CustomerNotificationSender sender;

    @Override
    public CommunicationEndpointType endpointType() {
        return CommunicationEndpointType.TELEGRAM;
    }

    @Override
    public void deliver(long notificationId, CommunicationEndpoint endpoint,
                        CustomerNotification notification) {
        try {
            sender.send(new CommunicationTarget(endpoint.getCustomerId(), 1,
                    ExternalIdentityProvider.TELEGRAM, endpoint.getAddressCiphertext(),
                    endpoint.getLocale()), notification);
        } catch (TelegramBotApiException exception) {
            throw new DeliveryFailureException("telegram_provider_error", exception.retryable(),
                    exception.invalidRecipient(), exception.retryAfter(), exception);
        } catch (IllegalArgumentException exception) {
            throw new DeliveryFailureException("telegram_endpoint_invalid", false, true, null, exception);
        }
    }
}
