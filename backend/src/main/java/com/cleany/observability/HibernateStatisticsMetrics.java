package com.cleany.observability;

import org.hibernate.stat.Statistics;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class HibernateStatisticsMetrics implements MeterBinder {

    private static final String FACTORY_TAG = "default";

    private final Statistics statistics;

    @Override
    public void bindTo(MeterRegistry registry) {
        counter(registry, "hibernate.sessions.open", Statistics::getSessionOpenCount);
        counter(registry, "hibernate.sessions.close", Statistics::getSessionCloseCount);
        counter(registry, "hibernate.transactions", Statistics::getTransactionCount);
        counter(registry, "hibernate.transactions.success", Statistics::getSuccessfulTransactionCount);
        counter(registry, "hibernate.connections.obtained", Statistics::getConnectCount);
        counter(registry, "hibernate.flushes", Statistics::getFlushCount);
        counter(registry, "hibernate.entities.loads", Statistics::getEntityLoadCount);
        counter(registry, "hibernate.entities.fetches", Statistics::getEntityFetchCount);
        counter(registry, "hibernate.collections.loads", Statistics::getCollectionLoadCount);
        counter(registry, "hibernate.collections.fetches", Statistics::getCollectionFetchCount);
        counter(registry, "hibernate.statements.prepared", Statistics::getPrepareStatementCount);
        counter(registry, "hibernate.queries.executions", Statistics::getQueryExecutionCount);
        Gauge.builder("hibernate.queries.max", statistics, Statistics::getQueryExecutionMaxTime)
                .description("Maximum observed Hibernate query execution time in milliseconds")
                .tag("entityManagerFactory", FACTORY_TAG)
                .register(registry);
    }

    private void counter(
            MeterRegistry registry,
            String name,
            java.util.function.ToDoubleFunction<Statistics> value
    ) {
        FunctionCounter.builder(name, statistics, value)
                .tag("entityManagerFactory", FACTORY_TAG)
                .register(registry);
    }
}
