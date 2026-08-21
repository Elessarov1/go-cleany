package com.cleany.order;

import java.util.List;

public record CleaningOrderReport(CleaningOrder order, List<Long> mediaIds) {

    public CleaningOrderReport {
        mediaIds = List.copyOf(mediaIds);
    }
}
