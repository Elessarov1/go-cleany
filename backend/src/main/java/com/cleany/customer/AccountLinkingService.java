package com.cleany.customer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountLinkingService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final CustomerAccountService customerAccountService;
    private final CustomerIdentityLinkRequestRepository requestRepository;
    private final CustomerExternalIdentityRepository identityRepository;
    private final CustomerAccountMergeService mergeService;
    private final AccountLinkingProperties properties;
    private final Clock clock;

    @Transactional
    public AccountLinkInitiatedResponse initiateTelegramLink() {
        CurrentCustomer current = customerAccountService.currentCustomer();
        if (current.provider() != ExternalIdentityProvider.GOOGLE) {
            throw new AccountLinkProviderException("Telegram linking must be initiated from the web account");
        }
        if (identityRepository.findByCustomerIdAndProvider(
                current.customerId(),
                ExternalIdentityProvider.TELEGRAM
        ).isPresent()) {
            throw new AccountLinkConflictException("A Telegram identity is already linked");
        }

        var now = clock.instant();
        requestRepository.consumeOutstanding(
                current.customerId(),
                ExternalIdentityProvider.TELEGRAM,
                now
        );
        String token = newToken();
        var expiresAt = now.plus(properties.requestTtl());
        requestRepository.save(new CustomerIdentityLinkRequest(
                hash(token),
                current.customerId(),
                ExternalIdentityProvider.TELEGRAM,
                now,
                expiresAt
        ));
        return new AccountLinkInitiatedResponse(deepLink(token), expiresAt);
    }

    @Transactional
    public AccountIdentitiesResponse confirmTelegramLink(String rawToken) {
        CustomerIdentityLinkRequest request = requestRepository.findByTokenHashForUpdate(hash(rawToken))
                .orElseThrow(AccountLinkTokenInvalidException::new);
        var now = clock.instant();
        if (request.getConsumedAt() != null) {
            throw new AccountLinkTokenConsumedException();
        }
        if (request.isExpired(now)) {
            throw new AccountLinkTokenExpiredException();
        }
        if (request.getProvider() != ExternalIdentityProvider.TELEGRAM) {
            throw new AccountLinkTokenInvalidException();
        }

        CurrentCustomer telegramCustomer = customerAccountService.currentCustomer();
        if (telegramCustomer.provider() != ExternalIdentityProvider.TELEGRAM) {
            throw new AccountLinkProviderException("Telegram Mini App authentication is required");
        }
        CustomerExternalIdentity telegramIdentity = identityRepository
                .findByProviderAndExternalSubjectForUpdate(
                        ExternalIdentityProvider.TELEGRAM,
                        telegramCustomer.externalSubject()
                )
                .orElseThrow(() -> new AccountLinkConflictException("Telegram identity no longer exists"));
        mergeService.mergeInto(request.getTargetCustomerId(), telegramIdentity.getCustomerId());
        request.consume(now);
        return identitiesFor(request.getTargetCustomerId());
    }

    private AccountIdentitiesResponse identitiesFor(long customerId) {
        return new AccountIdentitiesResponse(identityRepository
                .findAllByCustomerIdOrderByProvider(customerId)
                .stream()
                .filter(identity -> identity.getProvider() == ExternalIdentityProvider.GOOGLE
                        || identity.getProvider() == ExternalIdentityProvider.TELEGRAM)
                .map(identity -> new AccountIdentityResponse(
                        identity.getProvider(),
                        true,
                        identity.getProvider() == ExternalIdentityProvider.TELEGRAM
                                ? identity.getUsername()
                                : null,
                        identity.isWriteAccessAllowed()
                ))
                .toList());
    }

    private String deepLink(String token) {
        String base = properties.miniAppLinkBase();
        if (base.isBlank()) {
            throw new AccountLinkProviderException("Telegram Mini App link is not configured");
        }
        if (base.contains("{token}")) {
            return base.replace("{token}", token);
        }
        return UriComponentsBuilder.fromUriString(base)
                .queryParam("startapp", token)
                .build(true)
                .toUriString();
    }

    private static String newToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new AccountLinkTokenInvalidException();
        }
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(rawToken.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
