package com.cleany.order;

public class OrderClaimConflictException extends RuntimeException {

    public OrderClaimConflictException(long orderId) {
        super("Order %d has already been claimed or is no longer available".formatted(orderId));
    }
}

