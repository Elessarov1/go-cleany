package com.cleany.customer;

import java.time.Clock;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.order.PhoneNumberNormalizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerAccountService {

    private final CustomerIdentityProvider identityProvider;
    private final CustomerAccountRepository accountRepository;
    private final CustomerExternalIdentityRepository externalIdentityRepository;
    private final PhoneNumberNormalizer phoneNumberNormalizer;
    private final Clock clock;

    @Transactional
    public CurrentCustomer currentCustomer() {
        return resolveCustomer(identityProvider.currentIdentity());
    }

    @Transactional
    public CurrentCustomer resolveCustomer(AuthenticatedCustomerIdentity authenticatedIdentity) {
        Objects.requireNonNull(authenticatedIdentity, "authenticatedIdentity");
        ResolvedCustomer resolved = resolveAccount(
                authenticatedIdentity.provider(),
                authenticatedIdentity.externalSubject(),
                authenticatedIdentity.username(),
                authenticatedIdentity.displayName(),
                authenticatedIdentity.languageCode()
        );
        CustomerExternalIdentity externalIdentity = resolved.externalIdentity();

        return new CurrentCustomer(
                resolved.account().getId(),
                requireIdentityId(externalIdentity),
                externalIdentity.getProvider(),
                externalIdentity.getExternalSubject(),
                externalIdentity.getUsername(),
                externalIdentity.getDisplayName(),
                externalIdentity.getLanguageCode()
        );
    }

    @Transactional
    public CustomerProfileResponse currentProfile() {
        CurrentCustomer customer = currentCustomer();
        CustomerAccount account = accountRepository.findById(customer.customerId())
                .orElseThrow(() -> new IllegalStateException(
                        "Customer account not found: " + customer.customerId()
                ));
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
            String languageCode,
            String rawPhone
    ) {
        CustomerAccount account = resolveAccount(
                provider,
                requireExternalSubject(externalSubject),
                username,
                displayName,
                languageCode
        ).account();
        account.updatePhone(phoneNumberNormalizer.normalize(rawPhone));
    }

    @Transactional
    public void lock(long customerId) {
        accountRepository.findByIdForUpdate(customerId)
                .orElseThrow(() -> new IllegalStateException("Customer account not found: " + customerId));
    }

    private ResolvedCustomer resolveAccount(
            ExternalIdentityProvider provider,
            String externalSubject,
            String username,
            String displayName,
            String languageCode
    ) {
        var now = clock.instant();
        var existingIdentity = externalIdentityRepository.findByProviderAndExternalSubject(
                provider,
                externalSubject
        );
        if (existingIdentity.isPresent()) {
            CustomerExternalIdentity identity = existingIdentity.get();
            identity.refresh(
                    normalizeOptional(username),
                    normalizeDisplayName(displayName, externalSubject),
                    normalizeLanguageCode(languageCode),
                    now
            );
            CustomerAccount account = accountRepository.findById(identity.getCustomerId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Customer account not found: " + identity.getCustomerId()
                    ));
            return new ResolvedCustomer(account, identity);
        }

        CustomerAccount account = accountRepository.save(new CustomerAccount(now));
        CustomerExternalIdentity identity = externalIdentityRepository.save(new CustomerExternalIdentity(
                account.getId(),
                provider,
                externalSubject,
                normalizeOptional(username),
                normalizeDisplayName(displayName, externalSubject),
                normalizeLanguageCode(languageCode),
                now
        ));
        return new ResolvedCustomer(account, identity);
    }

    private static long requireIdentityId(CustomerExternalIdentity identity) {
        if (identity.getId() == null) {
            throw new IllegalStateException("Persisted external identity has no id");
        }
        return identity.getId();
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

    private static String normalizeLanguageCode(String value) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            return null;
        }
        normalized = normalized.toLowerCase(java.util.Locale.ROOT).replace('_', '-');
        return normalized.length() <= 16 ? normalized : normalized.substring(0, 16);
    }

    private record ResolvedCustomer(
            CustomerAccount account,
            CustomerExternalIdentity externalIdentity
    ) {
    }
}
