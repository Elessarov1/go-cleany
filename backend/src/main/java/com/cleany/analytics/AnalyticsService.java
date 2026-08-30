package com.cleany.analytics;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsQueryRepository queryRepository;
    private final AnalyticsProperties properties;

    @Transactional(readOnly = true)
    public AnalyticsOverviewResponse overview(
            LocalDate from,
            LocalDate to,
            AnalyticsServiceDimension service
    ) {
        AnalyticsTimeWindow window = window(from, to, service);
        return new AnalyticsOverviewResponse(
                new AnalyticsPeriodResponse(from, to, service),
                queryRepository.customerMetrics(window),
                queryRepository.businessHealth(window),
                queryRepository.retention(window),
                queryRepository.transitions(window),
                queryRepository.repeatActions(window),
                queryRepository.averageChecks(window),
                queryRepository.acquisitionMetrics(window)
        );
    }

    private AnalyticsTimeWindow window(
            LocalDate from,
            LocalDate to,
            AnalyticsServiceDimension service
    ) {
        if (from == null || to == null || service == null) {
            throw new InvalidAnalyticsPeriodException("Analytics period and service are required");
        }
        if (from.isAfter(to)) {
            throw new InvalidAnalyticsPeriodException("Analytics period start must not be after end");
        }
        Instant requestedFrom = from.atStartOfDay(properties.zoneId()).toInstant();
        Instant toExclusive = to.plusDays(1).atStartOfDay(properties.zoneId()).toInstant();
        Instant effectiveFrom = properties.commercialLaunchAt() == null
                || requestedFrom.isAfter(properties.commercialLaunchAt())
                ? requestedFrom
                : properties.commercialLaunchAt();
        if (effectiveFrom.isAfter(toExclusive)) {
            effectiveFrom = toExclusive;
        }
        Instant lifetimeFrom = properties.commercialLaunchAt() == null
                ? Instant.EPOCH
                : properties.commercialLaunchAt();
        return new AnalyticsTimeWindow(
                from,
                to,
                effectiveFrom,
                toExclusive,
                lifetimeFrom,
                service
        );
    }
}
