package com.cleany.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class NotificationDeliveryTest {

    @Test
    void retriesUseBoundedPolicyAndHonorLongerProviderRetryAfter() {
        Instant now = Instant.parse("2026-09-10T10:00:00Z");
        NotificationDelivery delivery = new NotificationDelivery(17L, UUID.randomUUID(), now,
                now.plus(Duration.ofDays(1)));

        delivery.claim(now, Duration.ofMinutes(2));
        delivery.failed(now, "rate_limited", Duration.ofMinutes(3), true);

        assertEquals(NotificationDeliveryStatus.RETRY, delivery.getStatus());
        assertEquals(now.plus(Duration.ofMinutes(3)), delivery.getAvailableAt());
        assertEquals(1, delivery.getAttemptCount());
    }

    @Test
    void permanentFailureAndExpiredEventAreDead() {
        Instant now = Instant.parse("2026-09-10T10:00:00Z");
        NotificationDelivery permanent = new NotificationDelivery(17L, UUID.randomUUID(), now,
                now.plus(Duration.ofDays(1)));
        permanent.claim(now, Duration.ofMinutes(2));
        permanent.failed(now, "invalid_request", null, false);
        assertEquals(NotificationDeliveryStatus.DEAD, permanent.getStatus());

        NotificationDelivery expired = new NotificationDelivery(18L, UUID.randomUUID(), now.minusSeconds(10), now);
        expired.claim(now, Duration.ofMinutes(2));
        expired.failed(now, "provider_down", null, true);
        assertEquals(NotificationDeliveryStatus.DEAD, expired.getStatus());
    }
}
