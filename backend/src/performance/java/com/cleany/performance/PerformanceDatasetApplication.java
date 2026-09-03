package com.cleany.performance;

import java.util.Arrays;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.cleany.CleaningApplication;
import tools.jackson.databind.ObjectMapper;

public final class PerformanceDatasetApplication {

    private PerformanceDatasetApplication() {
    }

    public static void main(String[] args) {
        requirePerformanceEnvironment();
        try (var context = new SpringApplicationBuilder(CleaningApplication.class)
                .web(WebApplicationType.SERVLET)
                .properties("server.port=0")
                .run(args)) {
            Environment environment = context.getEnvironment();
            if (!Arrays.asList(environment.getActiveProfiles()).contains("performance")) {
                throw new IllegalStateException("The performance profile must be active");
            }
            var seeder = new PerformanceDatasetSeeder(
                    context.getBean(JdbcTemplate.class),
                    context.getBean(PlatformTransactionManager.class),
                    context.getBean(ObjectMapper.class),
                    environment
            );
            seeder.seed();
        }
    }

    private static void requirePerformanceEnvironment() {
        String profiles = System.getenv("SPRING_PROFILES_ACTIVE");
        String databaseUrl = System.getenv("DB_URL");
        if (profiles == null || Arrays.stream(profiles.split(","))
                .map(String::trim)
                .noneMatch("performance"::equals)) {
            throw new IllegalStateException("SPRING_PROFILES_ACTIVE must include performance");
        }
        if (databaseUrl == null || !databaseUrl.matches(
                "jdbc:postgresql://(127\\.0\\.0\\.1|localhost|postgres)(:[0-9]+)?/loco_performance(?:[?].*)?"
        )) {
            throw new IllegalStateException(
                    "performanceSeed only accepts the dedicated local loco_performance database"
            );
        }
    }
}
