package com.cleany.customer;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.order.PhoneNumberNormalizer;
import com.cleany.telegram.CustomerIdentityProvider;
import com.cleany.telegram.TelegramPrincipal;

@Service
public class CustomerAccountService {

    private final CustomerIdentityProvider identityProvider;
    private final CustomerAccountRepository accountRepository;
    private final CustomerExternalIdentityRepository externalIdentityRepository;
    private final PhoneNumberNormalizer phoneNumberNormalizer;
    private final Clock clock;

    public CustomerAccountService(
            CustomerIdentityProvider identityProvider,
            CustomerAccountRepository accountRepository,
            CustomerExternalIdentityRepository externalIdentityRepository,
            PhoneNumberNormalizer phoneNumberNormalizer,
            Clock clock
    ) {
        this.identityProvider = identityProvider;
        this.accountRepository = accountRepository;
        this.externalIdentityRepository = externalIdentityRepository;
        this.phoneNumberNormalizer = phoneNumberNormalizer;
        this.clock = clock;
    }

    @Transactional
    public CurrentCustomer currentCustomer() {
        TelegramPrincipal principal = identityProvider.currentCustomer();
        String externalSubject = Long.toString(principal.id());
        CustomerAccount account = resolveAccount(
                ExternalIdentityProvider.TELEGRAM,
                externalSubject,
                principal.username(),
                principal.displayName()
        );

        return new CurrentCustomer(
                account.getId(),
                principal.id(),
                normalizeOptional(principal.username()),
                principal.displayName()
        );
    }

    @Transactional
    public CustomerProfileResponse currentProfile() {
        CurrentCustomer customer = currentCustomer();
        CustomerAccount account = accountRepository.findById(customer.id())
                .orElseThrow(() -> new IllegalStateException("Customer account not found: " + customer.id()));
        return new CustomerProfileResponse(account.getPhone());
    }

    @Transactional
    public void updatePhone(long customerId, String rawPhone) {
        CustomerAccount account = accountRepository.findById(customerId)
                .orElseThrow(() -> new IllegalStateException("Customer account not found: " + customerId));
        account.updatePhone(phoneNumberNormalizer.normalize(rawPhone));
    }

    @Transactional
    public void savePhoneForExternalIdentity(
            ExternalIdentityProvider provider,
            String externalSubject,
            String username,
            String displayName,
            String rawPhone
    ) {
        CustomerAccount account = resolveAccount(
                provider,
                requireExternalSubject(externalSubject),
                username,
                displayName
        );
        account.updatePhone(phoneNumberNormalizer.normalize(rawPhone));
    }

    @Transactional
    public void lock(long customerId) {
        accountRepository.findByIdForUpdate(customerId)
                .orElseThrow(() -> new IllegalStateException("Customer account not found: " + customerId));
    }

    private CustomerAccount resolveAccount(
            ExternalIdentityProvider provider,
            String externalSubject,
            String username,
            String displayName
    ) {
        var now = clock.instant();
        var existingIdentity = externalIdentityRepository.findByProviderAndExternalSubject(
                provider,
                externalSubject
        );
        if (existingIdentity.isPresent()) {
            CustomerExternalIdentity identity = existingIdentity.get();
            identity.refresh(normalizeOptional(username), normalizeDisplayName(displayName, externalSubject), now);
            return accountRepository.findById(identity.getCustomerId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Customer account not found: " + identity.getCustomerId()
                    ));
        }

        CustomerAccount account = accountRepository.save(new CustomerAccount(now));
        externalIdentityRepository.save(new CustomerExternalIdentity(
                account.getId(),
                provider,
                externalSubject,
                normalizeOptional(username),
                normalizeDisplayName(displayName, externalSubject),
                now
        ));
        return account;
    }

    private static String requireExternalSubject(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("External identity subject must not be blank");
        }
        return value.trim();
    }

    private static String normalizeDisplayName(String value, String externalSubject) {
        String normalized = normalizeOptional(value);
        return normalized == null ? "Customer " + externalSubject : normalized;
    }

    private static String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
