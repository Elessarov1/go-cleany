package com.cleany.order;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cleaning_order_event")
public class CleaningOrderEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private CleaningOrder order;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private OrderEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 32)
    private CleaningOrderStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 32)
    private CleaningOrderStatus toStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false, length = 20)
    private OrderActorType actorType;

    @Column(name = "actor_telegram_user_id")
    private Long actorTelegramUserId;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected CleaningOrderEvent() {
    }

    public CleaningOrderEvent(
            CleaningOrder order,
            OrderEventType eventType,
            CleaningOrderStatus fromStatus,
            CleaningOrderStatus toStatus,
            OrderActorType actorType,
            Long actorTelegramUserId,
            String details,
            Instant occurredAt
    ) {
        this.order = Objects.requireNonNull(order);
        this.eventType = Objects.requireNonNull(eventType);
        this.fromStatus = fromStatus;
        this.toStatus = Objects.requireNonNull(toStatus);
        this.actorType = Objects.requireNonNull(actorType);
        this.actorTelegramUserId = actorTelegramUserId;
        this.details = details;
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    public Long getId() {
        return id;
    }

    public CleaningOrder getOrder() {
        return order;
    }

    public OrderEventType getEventType() {
        return eventType;
    }

    public CleaningOrderStatus getFromStatus() {
        return fromStatus;
    }

    public CleaningOrderStatus getToStatus() {
        return toStatus;
    }

    public OrderActorType getActorType() {
        return actorType;
    }

    public Long getActorTelegramUserId() {
        return actorTelegramUserId;
    }

    public String getDetails() {
        return details;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
