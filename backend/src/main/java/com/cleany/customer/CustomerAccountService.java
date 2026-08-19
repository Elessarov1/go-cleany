package com.cleany.customer;

import java.time.Clock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.telegram.CustomerIdentityProvider;
import com.cleany.telegram.TelegramPrincipal;

@Service
public class CustomerAccountService {

    private final CustomerIdentityProvider identityProvider;
    private final CustomerAccountRepository accountRepository;
    private final CustomerExternalIdentityRepository externalIdentityRepository;
    private final Clock clock;

    public CustomerAccountService(
            CustomerIdentityProvider identityProvider,
            CustomerAccountRepository accountRepository,
            CustomerExternalIdentityRepository externalIdentityRepository,
            Clock clock
    ) {
        this.identityProvider = identityProvider;
        this.accountRepository = accountRepository;
        this.externalIdentityRepository = externalIdentityRepository;
        this.clock = clock;
    }

    @Transactional
    public CurrentCustomer currentCustomer() {
        TelegramPrincipal principal = identityProvider.currentCustomer();
        String externalSubject = Long.toString(principal.id());
        var existingIdentity = externalIdentityRepository.findByProviderAndExternalSubject(
                ExternalIdentityProvider.TELEGRAM,
                externalSubject
        );

        long customerId;
        if (existingIdentity.isPresent()) {
            CustomerExternalIdentity identity = existingIdentity.get();
            identity.refresh(normalizeOptional(principal.username()), principal.displayName(), clock.instant());
            customerId = identity.getCustomerId();
        } else {
            CustomerAccount account = accountRepository.save(new CustomerAccount(clock.instant()));
            customerId = account.getId();
            externalIdentityRepository.save(new CustomerExternalIdentity(
                    customerId,
                    ExternalIdentityProvider.TELEGRAM,
                    externalSubject,
                    normalizeOptional(principal.username()),
                    principal.displayName(),
                    clock.instant()
            ));
        }

        return new CurrentCustomer(
                customerId,
                principal.id(),
                normalizeOptional(principal.username()),
                principal.displayName()
        );
    }

    @Transactional
    public void lock(long customerId) {
        accountRepository.findByIdForUpdate(customerId)
                .orElseThrow(() -> new IllegalStateException("Customer account not found: " + customerId));
    }

    private static String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
