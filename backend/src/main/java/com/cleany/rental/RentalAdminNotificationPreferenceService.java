package com.cleany.rental;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.admin.AdminAccessService;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentity;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.customer.TelegramIdentityNotLinkedException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalAdminNotificationPreferenceService {

    private final AdminAccessService accessService;
    private final CustomerAccountService customerAccountService;
    private final CustomerExternalIdentityRepository identityRepository;
    private final RentalAdminNotificationPreferenceRepository repository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public RentalAdminNotificationPreferenceResponse current() {
        long customerId = currentAdminCustomerId();
        CustomerExternalIdentity telegram = telegramIdentity(customerId);
        boolean enabled = telegram != null && repository.findById(customerId)
                .map(RentalAdminNotificationPreference::isTelegramEnabled)
                .orElse(true);
        return response(telegram, enabled);
    }

    @Transactional
    public RentalAdminNotificationPreferenceResponse update(
            UpdateRentalAdminNotificationPreferenceRequest request
    ) {
        long customerId = currentAdminCustomerId();
        CustomerExternalIdentity telegram = telegramIdentity(customerId);
        if (telegram == null) {
            throw new TelegramIdentityNotLinkedException();
        }
        RentalAdminNotificationPreference preference = repository.findById(customerId)
                .orElseGet(() -> new RentalAdminNotificationPreference(
                        customerId,
                        request.telegramEnabled(),
                        clock.instant()
                ));
        preference.update(request.telegramEnabled(), clock.instant());
        repository.save(preference);
        return response(telegram, request.telegramEnabled());
    }

    private long currentAdminCustomerId() {
        long customerId = customerAccountService.currentCustomer().customerId();
        accessService.requireAdmin(customerId);
        return customerId;
    }

    private CustomerExternalIdentity telegramIdentity(long customerId) {
        return identityRepository.findByCustomerIdAndProvider(
                customerId,
                ExternalIdentityProvider.TELEGRAM
        ).orElse(null);
    }

    private static RentalAdminNotificationPreferenceResponse response(
            CustomerExternalIdentity telegram,
            boolean enabled
    ) {
        return new RentalAdminNotificationPreferenceResponse(
                telegram != null,
                telegram != null && enabled,
                telegram != null && telegram.isWriteAccessAllowed(),
                telegram == null ? null : telegram.getUsername()
        );
    }
}
