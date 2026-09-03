package com.cleany.base;

import java.util.Objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.cleany.catalog.PlatformServiceStateCache;
import com.cleany.rental.RentalPublicMediaCache;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withReuse(true);

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RentalPublicMediaCache rentalPublicMediaCache;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @BeforeEach
    void clearSharedCachesBeforeTest() {
        clearPlatformServiceStateCache();
        rentalPublicMediaCache.clear();
    }

    protected final void clearPlatformServiceStateCache() {
        Objects.requireNonNull(cacheManager.getCache(PlatformServiceStateCache.CACHE_NAME)).clear();
    }
}
