package com.cleany.customer.home;

public enum CustomerHomePrimaryActionType {
    RENTAL_TRANSFER_ARRIVAL(0),
    RENTAL_TRANSFER_CHECKOUT(1),
    RENTAL_CLEANING(2);

    private final int priority;

    CustomerHomePrimaryActionType(int priority) {
        this.priority = priority;
    }

    int priority() {
        return priority;
    }
}
