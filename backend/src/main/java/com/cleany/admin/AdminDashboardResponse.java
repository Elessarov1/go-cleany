package com.cleany.admin;

import java.util.List;

public record AdminDashboardResponse(
        AdminStatsResponse stats,
        List<AdminOrderSummaryResponse> recentOrders
) {
}
