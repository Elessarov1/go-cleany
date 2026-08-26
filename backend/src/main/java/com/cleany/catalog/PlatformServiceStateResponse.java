package com.cleany.catalog;

import java.time.Instant;

public record PlatformServiceStateResponse(
        PlatformService service,
        PlatformServiceStatus status,
        Instant updatedAt,
        Long updatedByCustomerId,
        long version
) {

    static PlatformServiceStateResponse from(PlatformServiceState state) {
        return new PlatformServiceStateResponse(
                state.getService(),
                state.getStatus(),
                state.getUpdatedAt(),
                state.getUpdatedByCustomerId(),
                state.getVersion()
        );
    }
}
