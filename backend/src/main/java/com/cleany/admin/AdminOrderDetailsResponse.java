package com.cleany.admin;

import java.util.List;

import com.cleany.order.CleaningOrderResponse;

public record AdminOrderDetailsResponse(
        CleaningOrderResponse order,
        long photoCount,
        List<AdminOrderEventResponse> events
) {
}
