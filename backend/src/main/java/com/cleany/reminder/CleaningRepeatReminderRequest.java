package com.cleany.reminder;

import jakarta.validation.constraints.NotNull;

public record CleaningRepeatReminderRequest(
        @NotNull CleaningRepeatReminderSelection selection
) {
}
