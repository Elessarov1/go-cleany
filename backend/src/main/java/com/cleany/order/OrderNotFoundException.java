package com.cleany.order;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(long orderId) {
        super("Order %d was not found".formatted(orderId));
    }
}

