package com.cleany.catalog;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

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

    private final PlatformServiceStateRepository repository;
    private final PlatformRoleService roleService;
    private final CustomerAccountService customerAccountService;
    private final AdminAccessService adminAccessService;
    private final Clock clock;

    @Transactional(readOnly = true)
    public boolean canStartCustomerFlow(PlatformService service, long customerId) {
        PlatformServiceStatus status = requireState(service).getStatus();
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
                .filter(state -> state.getStatus() == PlatformServiceStatus.ENABLED
                        || customerId != null && canStartCustomerFlow(
                                state.getService(),
                                customerId
                        ))
                .map(PlatformServiceStateResponse::from)
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
                .map(PlatformServiceStateResponse::from)
                .toList();
    }

    @Transactional
    public PlatformServiceStateResponse update(
            PlatformService service,
            PlatformServiceStatus status
    ) {
        long adminCustomerId = adminAccessService.requireCurrentAdmin();
        PlatformServiceState state = requireState(service);
        state.changeStatus(status, adminCustomerId, clock.instant());
        return PlatformServiceStateResponse.from(repository.saveAndFlush(state));
    }

    private PlatformServiceState requireState(PlatformService service) {
        return repository.findById(service)
                .orElseThrow(() -> new IllegalStateException(
                        "Platform service state is not configured: " + service
                ));
    }
}
