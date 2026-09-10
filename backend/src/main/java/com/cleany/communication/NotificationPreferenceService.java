package com.cleany.communication;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.customer.CustomerAccountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {
    private final CustomerNotificationPreferenceRepository repository;
    private final CustomerAccountService accountService;
    private final Clock clock;

    @Transactional
    public NotificationPreferencesResponse current() {
        long customerId = accountService.currentCustomer().customerId();
        return response(repository.findById(customerId)
                .orElseGet(() -> repository.save(new CustomerNotificationPreference(customerId, clock.instant()))));
    }

    @Transactional
    public NotificationPreferencesResponse update(UpdateNotificationPreferencesRequest request) {
        long customerId = accountService.currentCustomer().customerId();
        CustomerNotificationPreference preference = repository.findById(customerId)
                .orElseGet(() -> new CustomerNotificationPreference(customerId, clock.instant()));
        preference.update(request.telegramEnabled(), request.pushEnabled(), clock.instant());
        return response(repository.save(preference));
    }

    @Transactional(readOnly = true)
    public NotificationPreferencesResponse forCustomer(long customerId) {
        return repository.findById(customerId).map(NotificationPreferenceService::response)
                .orElse(new NotificationPreferencesResponse(true, true));
    }

    private static NotificationPreferencesResponse response(CustomerNotificationPreference preference) {
        return new NotificationPreferencesResponse(preference.isTelegramEnabled(), preference.isPushEnabled());
    }
}
