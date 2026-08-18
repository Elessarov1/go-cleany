package com.cleany.order;

public class PhotoReportEmptyException extends RuntimeException {

    public PhotoReportEmptyException(long orderId) {
        super("Photo report for order " + orderId + " has no photos");
    }
}
