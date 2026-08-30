package com.cleany.customer.activity;

import java.util.List;

public record CustomerActivityResponse(
        List<CustomerActivityItem> activeAndUpcoming,
        List<CustomerActivityItem> history
) {
}
