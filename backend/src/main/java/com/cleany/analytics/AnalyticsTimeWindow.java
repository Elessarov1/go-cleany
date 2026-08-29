package com.cleany.analytics;

import java.time.Instant;
import java.time.LocalDate;

record AnalyticsTimeWindow(
        LocalDate requestedFrom,
        LocalDate requestedTo,
        Instant fromInclusive,
        Instant toExclusive,
        Instant lifetimeFromInclusive,
        AnalyticsServiceDimension service
) {
}
