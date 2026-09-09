package com.cleany.rental;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RentalSearchResponse(
        UUID searchExecutionId,
        RentalSearchCriteriaResponse criteria,
        Instant calculatedAt,
        List<RentalSearchPropertyResponse> properties
) {
}
