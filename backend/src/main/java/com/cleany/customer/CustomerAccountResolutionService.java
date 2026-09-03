package com.cleany.customer;

import static com.cleany.common.text.TextValues.normalizeOptional;

import java.time.Clock;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.authorization.CustomerRoleBootstrapService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class CustomerAccountResolutionService {

    private final CustomerAccountRepository accountRepository;
    private final CustomerExternalIdentityRepository externalIdentityRepository;
    private final CustomerRoleBootstrapService roleBootstrapService;
    private final Clock clock;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CurrentCustomer resolve(AuthenticatedCustomerIdentity authenticatedIdentity) {
        Objects.requireNonNull(authenticatedIdentity, "authenticatedIdentity");
        ResolvedCustomer resolved = resolveAccount(
                authenticatedIdentity.provider(),
                authenticatedIdentity.externalSubject(),
                authenticatedIdentity.username(),
                authenticatedIdentity.displayName(),
                authenticatedIdentity.languageCode(),
                authenticatedIdentity.email(),
                authenticatedIdentity.emailVerified()
        );
        CustomerExternalIdentity externalIdentity = resolved.externalIdentity();
        if (authenticatedIdentity.provider() == ExternalIdentityProvider.TELEGRAM
                && authenticatedIdentity.allowsWriteToPm()) {
            externalIdentity.allowWriteAccess(clock.instant());
        }
        roleBootstrapService.bootstrap(authenticatedIdentity, resolved.account().getId());

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

    private ResolvedCustomer resolveAccount(
            ExternalIdentityProvider provider,
            String externalSubject,
            String username,
            String displayName,
            String languageCode,
            String email,
            boolean emailVerified
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
                    normalizeOptional(email),
                    emailVerified,
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
                normalizeOptional(email),
                emailVerified,
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

    private static String normalizeDisplayName(String value, String externalSubject) {
        String normalized = normalizeOptional(value);
        return normalized == null ? "Customer " + externalSubject : normalized;
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
