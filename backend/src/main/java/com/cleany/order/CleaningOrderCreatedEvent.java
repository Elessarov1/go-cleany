package com.cleany.order;

import java.util.Objects;

public record CleaningOrderCreatedEvent(CleaningOrder order) {

    public CleaningOrderCreatedEvent {
        Objects.requireNonNull(order);
    }
}
