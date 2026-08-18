package com.cleany.order;

public class ReportCollectionNotActiveException extends RuntimeException {

    public ReportCollectionNotActiveException(long cleanerTelegramUserId) {
        super("No active photo report for cleaner " + cleanerTelegramUserId);
    }
}
