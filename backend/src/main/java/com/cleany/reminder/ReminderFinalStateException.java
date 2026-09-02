package com.cleany.reminder;

public class ReminderFinalStateException extends RuntimeException {

    public ReminderFinalStateException(long sourceEntityId) {
        super("Reminder for source operation " + sourceEntityId + " can no longer be changed");
    }
}
