package com.cleany.customer;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountIdentityService {

    private final CustomerAccountService customerAccountService;
    private final CustomerExternalIdentityRepository identityRepository;

    @Transactional(readOnly = true)
    public AccountIdentitiesResponse current() {
        CurrentCustomer customer = customerAccountService.currentCustomer();
        Map<ExternalIdentityProvider, CustomerExternalIdentity> linked = identityRepository
                .findAllByCustomerIdOrderByProvider(customer.customerId())
                .stream()
                .collect(Collectors.toMap(CustomerExternalIdentity::getProvider, Function.identity()));
        return new AccountIdentitiesResponse(Arrays.stream(ExternalIdentityProvider.values())
                .filter(provider -> provider == ExternalIdentityProvider.GOOGLE
                        || provider == ExternalIdentityProvider.TELEGRAM)
                .map(provider -> response(provider, linked.get(provider)))
                .toList());
    }

    private static AccountIdentityResponse response(
            ExternalIdentityProvider provider,
            CustomerExternalIdentity identity
    ) {
        return new AccountIdentityResponse(
                provider,
                identity != null,
                identity == null || provider != ExternalIdentityProvider.TELEGRAM
                        ? null
                        : identity.getUsername(),
                identity != null && identity.isWriteAccessAllowed()
        );
    }
}
