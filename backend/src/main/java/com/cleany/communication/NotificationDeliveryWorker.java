package com.cleany.communication;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationDeliveryWorker {
    private static final Logger log = LoggerFactory.getLogger(NotificationDeliveryWorker.class);
    private final NotificationDeliveryQueue queue;
    private final Map<CommunicationEndpointType, NotificationDeliveryChannel> channels;

    public NotificationDeliveryWorker(NotificationDeliveryQueue queue,
                                      List<NotificationDeliveryChannel> channels) {
        this.queue = queue;
        var byType = new EnumMap<CommunicationEndpointType, NotificationDeliveryChannel>(
                CommunicationEndpointType.class);
        channels.forEach(channel -> {
            if (byType.putIfAbsent(channel.endpointType(), channel) != null) {
                throw new IllegalStateException("Multiple delivery channels for " + channel.endpointType());
            }
        });
        this.channels = Map.copyOf(byType);
    }

    @Scheduled(fixedDelayString = "${communications.worker-delay:2s}")
    public void deliverBatch() {
        for (Long id : queue.claim(20)) deliver(id);
    }

    private void deliver(long id) {
        try {
            var work = queue.load(id);
            if (work.endpoint() == null || work.notification() == null) {
                queue.cancelled(id, "ENDPOINT_OR_NOTIFICATION_UNAVAILABLE");
                return;
            }
            NotificationDeliveryChannel channel = channels.get(work.endpoint().getType());
            if (channel == null) {
                queue.cancelled(id, "CHANNEL_DISABLED");
                return;
            }
            channel.deliver(work.notificationId(), work.endpoint(), work.notification());
            queue.delivered(id);
        } catch (DeliveryFailureException failure) {
            queue.failed(id, failure);
        } catch (RuntimeException exception) {
            log.warn("Unexpected notification delivery failure for job {}", id, exception);
            queue.failed(id, new DeliveryFailureException("unexpected_delivery_error",
                    true, false, null, exception));
        }
    }
}
