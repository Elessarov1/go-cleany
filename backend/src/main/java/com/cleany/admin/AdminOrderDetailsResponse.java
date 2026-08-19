package com.cleany.admin;

import java.util.List;

import com.cleany.order.CleaningOrderResponse;

public record AdminOrderDetailsResponse(
        CleaningOrderResponse order,
        AdminOrderFinancialResponse financial,
        long photoCount,
        List<AdminOrderEventResponse> events
) {
}
