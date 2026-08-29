package com.cleany.catalog;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlatformServiceStateCache {

    public static final String CACHE_NAME = "platformServiceState";

    private final PlatformServiceStateRepository repository;

    @Cacheable(cacheNames = CACHE_NAME, key = "#service")
    @Transactional(readOnly = true)
    public PlatformServiceStateResponse get(PlatformService service) {
        return repository.findById(service)
                .map(PlatformServiceStateResponse::from)
                .orElseThrow(() -> PlatformServiceAccessService.missingState(service));
    }
}
