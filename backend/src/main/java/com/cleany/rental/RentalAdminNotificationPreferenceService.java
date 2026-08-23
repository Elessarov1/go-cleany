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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalAdminNotificationPreferenceService {

    private final AdminAccessService accessService;
    private final RentalAdminNotificationPreferenceRepository repository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public RentalAdminNotificationPreferenceResponse current() {
        long adminId = accessService.requireCurrentAdmin();
        return new RentalAdminNotificationPreferenceResponse(isEnabled(adminId));
    }

    @Transactional
    public RentalAdminNotificationPreferenceResponse update(
            UpdateRentalAdminNotificationPreferenceRequest request
    ) {
        long adminId = accessService.requireCurrentAdmin();
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
}
