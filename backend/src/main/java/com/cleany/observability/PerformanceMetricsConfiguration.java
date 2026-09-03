package com.cleany.observability;

import jakarta.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.micrometer.core.instrument.binder.MeterBinder;

@Configuration(proxyBeanMethods = false)
@Profile("performance")
public class PerformanceMetricsConfiguration {

    @Bean
    MeterBinder hibernatePerformanceMetrics(EntityManagerFactory entityManagerFactory) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        return new HibernateStatisticsMetrics(sessionFactory.getStatistics());
    }
}
