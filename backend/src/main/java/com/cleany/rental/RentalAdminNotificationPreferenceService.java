package com.cleany.rental;

import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.admin.AdminAccessService;
import com.cleany.admin.AdminNotAuthorizedException;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.ExternalIdentityProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalAdminNotificationPreferenceService {

    private final AdminAccessService accessService;
    private final CustomerAccountService customerAccountService;
    private final RentalAdminNotificationPreferenceRepository repository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public RentalAdminNotificationPreferenceResponse current() {
        return new RentalAdminNotificationPreferenceResponse(isEnabled(currentTelegramAdminId()));
    }

    @Transactional
    public RentalAdminNotificationPreferenceResponse update(
            UpdateRentalAdminNotificationPreferenceRequest request
    ) {
        long adminId = currentTelegramAdminId();
        boolean enabled = request.telegramEnabled();
        RentalAdminNotificationPreference preference = repository.findById(adminId)
                .orElseGet(() -> new RentalAdminNotificationPreference(
                        adminId,
                        enabled,
                        clock.instant()
                ));
        preference.update(enabled, clock.instant());
        repository.save(preference);
        return new RentalAdminNotificationPreferenceResponse(enabled);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<Long> enabledAdminIds(Collection<Long> configuredAdminIds) {
        if (configuredAdminIds.isEmpty()) {
            return List.of();
        }
        Map<Long, RentalAdminNotificationPreference> preferences = repository
                .findAllByAdminIdIn(configuredAdminIds)
                .stream()
                .collect(Collectors.toMap(
                        RentalAdminNotificationPreference::getAdminId,
                        Function.identity()
                ));
        return configuredAdminIds.stream()
                .distinct()
                .filter(adminId -> {
                    RentalAdminNotificationPreference preference = preferences.get(adminId);
                    return preference == null || preference.isTelegramEnabled();
                })
                .toList();
    }

    private boolean isEnabled(long adminId) {
        return repository.findById(adminId)
                .map(RentalAdminNotificationPreference::isTelegramEnabled)
                .orElse(true);
    }

    private long currentTelegramAdminId() {
        CurrentCustomer customer = customerAccountService.currentCustomer();
        accessService.requireAdmin(customer.customerId());
        if (customer.provider() != ExternalIdentityProvider.TELEGRAM) {
            throw new AdminNotAuthorizedException();
        }
        try {
            return Long.parseLong(customer.externalSubject());
        } catch (NumberFormatException exception) {
            throw new AdminNotAuthorizedException();
        }
    }
}
