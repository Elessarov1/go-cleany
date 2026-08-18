package com.cleany.order;

public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(long orderId, CleaningOrderStatus current, String action) {
        super("Order %d cannot %s while its status is %s".formatted(orderId, action, current));
    }
}

