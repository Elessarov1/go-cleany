package com.cleany.communication;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.notification.CustomerNotificationPayloadCodec;
import com.cleany.notification.CustomerNotificationType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationDeliveryQueue {
    private static final Duration LEASE = Duration.ofMinutes(2);
    private static final Duration DELIVERY_RETENTION = Duration.ofDays(7);

    private final NotificationDeliveryRepository repository;
    private final CommunicationEndpointRepository endpointRepository;
    private final CustomerNotificationPayloadCodec payloadCodec;
    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    @Transactional
    public int enqueue(long notificationId, List<CommunicationEndpoint> endpoints) {
        var now = clock.instant();
        for (CommunicationEndpoint endpoint : endpoints) {
            repository.save(new NotificationDelivery(notificationId, endpoint.getId(), now,
                    now.plus(DELIVERY_RETENTION)));
        }
        return endpoints.size();
    }

    @Transactional
    public List<Long> claim(int batchSize) {
        var now = clock.instant();
        List<NotificationDelivery> claimed = repository.claimable(now, batchSize);
        claimed.forEach(delivery -> delivery.claim(now, LEASE));
        return claimed.stream().map(NotificationDelivery::getId).toList();
    }

    @Transactional(readOnly = true)
    public WorkItem load(long id) {
        NotificationDelivery delivery = repository.findById(id).orElseThrow();
        CommunicationEndpoint endpoint = endpointRepository.findById(delivery.getEndpointId()).orElse(null);
        if (endpoint == null || !endpoint.active()) {
            return new WorkItem(id, delivery.getNotificationId(), endpoint, null);
        }
        return jdbcTemplate.queryForObject("""
                select type, payload_json from customer_notification where id = ?
                """, (rs, row) -> new WorkItem(id, delivery.getNotificationId(), endpoint, payloadCodec.decode(
                        CustomerNotificationType.valueOf(rs.getString("type")),
                        rs.getString("payload_json"))), delivery.getNotificationId());
    }

    @Transactional
    public void delivered(long id) {
        repository.findById(id).ifPresent(delivery -> delivery.delivered(clock.instant()));
    }

    @Transactional
    public void failed(long id, DeliveryFailureException failure) {
        repository.findById(id).ifPresent(delivery -> {
            if (failure.invalidEndpoint()) {
                endpointRepository.findById(delivery.getEndpointId())
                        .ifPresent(endpoint -> endpoint.disable(clock.instant()));
            }
            delivery.failed(clock.instant(), failure.code(), failure.retryAfter(), failure.retryable());
        });
    }

    @Transactional
    public void cancelled(long id, String reason) {
        repository.findById(id).ifPresent(delivery -> delivery.cancel(clock.instant(), reason));
    }

    public record WorkItem(long deliveryId, long notificationId, CommunicationEndpoint endpoint,
                           com.cleany.notification.CustomerNotification notification) {
    }
}
