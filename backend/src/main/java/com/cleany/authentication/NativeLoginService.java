package com.cleany.authentication;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NativeLoginService {
    private final AuthenticationChallengeRepository challengeRepository;
    private final NativeLoginTransactionService transactionService;
    private final AppleTokenClient appleTokenClient;
    private final SessionTokenCrypto crypto;
    private final NativeAuthProperties properties;
    private final Clock clock;

    @Transactional
    public NativeChallengeResponse challenge(CreateNativeChallengeRequest request) {
        if (!properties.enabled()) throw error("native_auth_unavailable", "Native authentication is unavailable");
        if (request.provider() != ExternalIdentityProvider.GOOGLE
                && request.provider() != ExternalIdentityProvider.APPLE) {
            throw error("native_provider_unsupported", "Use Telegram handoff for Telegram login");
        }
        String nonce = crypto.newToken("lp_nonce_");
        var now = clock.instant();
        AuthenticationChallenge challenge = challengeRepository.save(new AuthenticationChallenge(
                UUID.randomUUID(), AuthenticationChallengePurpose.LOGIN, request.provider(),
                null, null, crypto.hash(nonce), null, now, now.plus(properties.challengeTtl())
        ));
        return new NativeChallengeResponse(challenge.getId(), request.provider(), nonce, challenge.getExpiresAt());
    }

    public SessionTokensResponse complete(ExternalIdentityProvider provider, NativeProviderLoginRequest request) {
        if (provider == ExternalIdentityProvider.APPLE
                && (request.authorizationCode() == null || request.authorizationCode().isBlank())) {
            throw error("apple_authorization_code_required", "Apple authorization code is required");
        }
        NativeLoginTransactionService.PreparedLogin prepared = transactionService.prepare(provider, request);
        AppleTokenClient.AppleTokens tokens = provider == ExternalIdentityProvider.APPLE
                ? appleTokenClient.exchange(request.authorizationCode()) : null;
        if (provider == ExternalIdentityProvider.APPLE) {
            transactionService.verifyAppleExchange(prepared, tokens);
        }
        return transactionService.finish(prepared, request.clientType(), tokens);
    }

    private static NativeAuthenticationException error(String code, String message) {
        return new NativeAuthenticationException(code, message);
    }
}
