package com.cleany.reminder;

import java.time.Instant;
import java.time.LocalDate;

public record CleaningRepeatReminderResponse(
        CleaningRepeatReminderSelection selection,
        CustomerReminderStatus status,
        LocalDate scheduledDate,
        Instant notifiedAt,
        boolean editable
) {

    static CleaningRepeatReminderResponse notConfigured() {
        return new CleaningRepeatReminderResponse(null, null, null, null, true);
    }

    static CleaningRepeatReminderResponse from(CustomerReminder reminder) {
        return new CleaningRepeatReminderResponse(
                selection(reminder),
                reminder.getStatus(),
                reminder.getScheduledDate(),
                reminder.getNotifiedAt(),
                !reminder.isFinal()
        );
    }

    private static CleaningRepeatReminderSelection selection(CustomerReminder reminder) {
        if (reminder.getStatus() == CustomerReminderStatus.DISABLED) {
            return CleaningRepeatReminderSelection.DO_NOT_REMIND;
        }
        return reminder.getCleaningIntervalDays() == null
                ? null
                : reminder.getCleaningIntervalDays() == 14
                        ? CleaningRepeatReminderSelection.IN_14_DAYS
                        : CleaningRepeatReminderSelection.IN_30_DAYS;
    }
}
