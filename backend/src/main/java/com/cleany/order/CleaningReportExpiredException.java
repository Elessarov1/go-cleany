package com.cleany.order;

public class CleaningReportExpiredException extends RuntimeException {

    public CleaningReportExpiredException(long orderId) {
        super("Cleaning report has expired for order: " + orderId);
    }
}
