package com.cleany.catalog;

import java.time.Clock;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.admin.AdminAccessService;
import com.cleany.authorization.PlatformRole;
import com.cleany.authorization.PlatformRoleService;
import com.cleany.customer.CustomerAccountService;
import com.cleany.authentication.CustomerAuthenticationRequiredException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlatformServiceAccessService {

    private static final Comparator<PlatformServiceStateResponse> DISPLAY_ORDER = Comparator
            .comparingInt(PlatformServiceStateResponse::displayOrder)
            .thenComparing(state -> state.service().name());

    private final PlatformServiceStateRepository repository;
    private final PlatformServiceStateCache stateCache;
    private final PlatformRoleService roleService;
    private final CustomerAccountService customerAccountService;
    private final AdminAccessService adminAccessService;
    private final Clock clock;

    @Transactional(readOnly = true)
    public boolean canStartCustomerFlow(PlatformService service, long customerId) {
        PlatformServiceStatus status = requireState(service).status();
        return status == PlatformServiceStatus.ENABLED
                || status == PlatformServiceStatus.IN_TEST
                && roleService.hasRole(customerId, PlatformRole.ADMIN);
    }

    @Transactional(readOnly = true)
    public void requireCanStartCustomerFlow(PlatformService service, long customerId) {
        if (!canStartCustomerFlow(service, customerId)) {
            throw new PlatformServiceNotAvailableException(service);
        }
    }

    @Transactional(readOnly = true)
    public List<PlatformServiceStateResponse> currentCustomerCatalog() {
        Long customerId = currentCustomerId();
        return Arrays.stream(PlatformService.values())
                .map(this::requireState)
                .filter(state -> state.status() == PlatformServiceStatus.ENABLED
                        || customerId != null && canStartCustomerFlow(
                                state.service(),
                                customerId
                        ))
                .sorted(DISPLAY_ORDER)
                .toList();
    }

    private Long currentCustomerId() {
        try {
            return customerAccountService.currentCustomer().customerId();
        } catch (CustomerAuthenticationRequiredException exception) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<PlatformServiceStateResponse> adminStates() {
        adminAccessService.requireCurrentAdmin();
        return Arrays.stream(PlatformService.values())
                .map(this::requireState)
                .sorted(DISPLAY_ORDER)
                .toList();
    }

    @Transactional
    @CacheEvict(cacheNames = PlatformServiceStateCache.CACHE_NAME, key = "#service")
    public PlatformServiceStateResponse update(
            PlatformService service,
            PlatformServiceStatus status,
            Integer displayOrder
    ) {
        long adminCustomerId = adminAccessService.requireCurrentAdmin();
        PlatformServiceState state = repository.findById(service)
                .orElseThrow(() -> missingState(service));
        state.updateConfiguration(status, displayOrder, adminCustomerId, clock.instant());
        PlatformServiceStateResponse response = PlatformServiceStateResponse.from(
                repository.saveAndFlush(state)
        );
        return response;
    }

    private PlatformServiceStateResponse requireState(PlatformService service) {
        return stateCache.get(service);
    }

    static IllegalStateException missingState(PlatformService service) {
        return new IllegalStateException("Platform service state is not configured: " + service);
    }
}
