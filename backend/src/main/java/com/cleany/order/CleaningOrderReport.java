package com.cleany.order;

import java.util.List;

public record CleaningOrderReport(CleaningOrder order, List<String> telegramFileIds) {

    public CleaningOrderReport {
        telegramFileIds = List.copyOf(telegramFileIds);
    }
}
