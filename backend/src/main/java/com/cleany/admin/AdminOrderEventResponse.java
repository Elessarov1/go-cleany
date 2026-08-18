package com.cleany.admin;

import java.time.Instant;

import com.cleany.order.CleaningOrderEvent;
import com.cleany.order.CleaningOrderStatus;
import com.cleany.order.OrderActorType;
import com.cleany.order.OrderEventType;

public record AdminOrderEventResponse(
        long id,
        OrderEventType eventType,
        CleaningOrderStatus fromStatus,
        CleaningOrderStatus toStatus,
        OrderActorType actorType,
        Long actorTelegramUserId,
        String details,
        Instant occurredAt
) {

    public static AdminOrderEventResponse from(CleaningOrderEvent event) {
        return new AdminOrderEventResponse(
                event.getId(),
                event.getEventType(),
                event.getFromStatus(),
                event.getToStatus(),
                event.getActorType(),
                event.getActorTelegramUserId(),
                event.getDetails(),
                event.getOccurredAt()
        );
    }
}
