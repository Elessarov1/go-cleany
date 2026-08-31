package com.cleany.support;

import com.cleany.catalog.PlatformService;

public record SupportSource(
        PlatformService service,
        long sourceEntityId,
        long customerId,
        boolean completed,
        String customerName,
        String customerPhone,
        String customerPath,
        String adminPath
) {
}
