package com.cleany.reminder;

public class ReminderSourceNotEligibleException extends RuntimeException {

    public ReminderSourceNotEligibleException(long sourceEntityId) {
        super("Completed cleaning order is required to configure reminder: " + sourceEntityId);
    }
}
