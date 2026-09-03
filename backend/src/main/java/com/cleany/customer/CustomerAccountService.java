package com.cleany.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.order.PhoneNumberNormalizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerAccountService {

    private final CustomerIdentityProvider identityProvider;
    private final CustomerAccountRepository accountRepository;
    private final CustomerAccountResolutionService resolutionService;
    private final CurrentCustomerRequestCache requestCache;
    private final PhoneNumberNormalizer phoneNumberNormalizer;

    public CurrentCustomer currentCustomer() {
        return requestCache.current().orElseGet(() -> {
            CurrentCustomer customer = resolutionService.resolve(identityProvider.currentIdentity());
            requestCache.store(customer);
            return customer;
        });
    }

    public CurrentCustomer resolveCustomer(AuthenticatedCustomerIdentity authenticatedIdentity) {
        return resolutionService.resolve(authenticatedIdentity);
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
    public void updateNormalizedPhone(long customerId, String normalizedPhone) {
        CustomerAccount account = accountRepository.findById(customerId)
                .orElseThrow(() -> new IllegalStateException("Customer account not found: " + customerId));
        if (normalizedPhone == null || normalizedPhone.isBlank()) {
            throw new IllegalArgumentException("normalizedPhone must not be blank");
        }
        account.updatePhone(normalizedPhone);
    }

    public void savePhoneForExternalIdentity(
            ExternalIdentityProvider provider,
            String externalSubject,
            String username,
            String displayName,
            String languageCode,
            String rawPhone
    ) {
        CurrentCustomer customer = resolutionService.resolve(new AuthenticatedCustomerIdentity(
                provider,
                requireExternalSubject(externalSubject),
                username,
                displayName,
                languageCode,
                null,
                false
        ));
        CustomerAccount account = accountRepository.findById(customer.customerId())
                .orElseThrow(() -> new IllegalStateException(
                        "Customer account not found: " + customer.customerId()
                ));
        account.updatePhone(phoneNumberNormalizer.normalize(rawPhone));
        accountRepository.save(account);
    }

    public void recordTelegramWriteAccess(
            String externalSubject,
            String username,
            String displayName,
            String languageCode
    ) {
        resolutionService.resolve(new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.TELEGRAM,
                requireExternalSubject(externalSubject),
                username,
                displayName,
                languageCode,
                null,
                false,
                true
        ));
    }

    @Transactional
    public void lock(long customerId) {
        accountRepository.findByIdForUpdate(customerId)
                .orElseThrow(() -> new IllegalStateException("Customer account not found: " + customerId));
    }

    private static String requireExternalSubject(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("External identity subject must not be blank");
        }
        return value.trim();
    }

}
