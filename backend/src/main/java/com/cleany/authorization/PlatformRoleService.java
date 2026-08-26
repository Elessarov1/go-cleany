package com.cleany.authorization;

import java.time.Clock;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlatformRoleService {

    private final CustomerRoleRepository repository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public boolean hasRole(long customerId, PlatformRole role) {
        return repository.existsByCustomerIdAndRole(customerId, role);
    }

    @Transactional(readOnly = true)
    public Set<PlatformRole> roles(long customerId) {
        return repository.findAllByCustomerId(customerId).stream()
                .map(CustomerRole::getRole)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Transactional
    public void ensureRole(long customerId, PlatformRole role) {
        repository.ensureRole(customerId, role.name(), clock.instant());
    }
}
