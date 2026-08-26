package com.cleany.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformServiceStateRepository
        extends JpaRepository<PlatformServiceState, PlatformService> {
}
