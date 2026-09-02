package com.cleany.reminder;

public enum CleaningRepeatReminderSelection {
    IN_14_DAYS(14),
    IN_30_DAYS(30),
    DO_NOT_REMIND(null);

    private final Integer intervalDays;

    CleaningRepeatReminderSelection(Integer intervalDays) {
        this.intervalDays = intervalDays;
    }

    public Integer intervalDays() {
        return intervalDays;
    }
}
