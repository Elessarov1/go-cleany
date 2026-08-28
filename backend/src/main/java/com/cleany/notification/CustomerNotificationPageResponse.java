package com.cleany.notification;

import java.util.List;

public record CustomerNotificationPageResponse(
        List<CustomerNotificationResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
