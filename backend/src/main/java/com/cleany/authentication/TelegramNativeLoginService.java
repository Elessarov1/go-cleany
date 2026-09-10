package com.cleany.authentication;

import java.time.Clock;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.telegram.bot.TelegramUpdate;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class TelegramNativeLoginService {
    private final AuthenticationChallengeRepository challengeRepository;
    private final CustomerAccountService accountService;
    private final SessionTokenService sessionTokenService;
    private final SessionTokenCrypto crypto;
    private final NativeAuthProperties properties;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Transactional
    public TelegramLoginAttemptResponse create() {
        if (!properties.enabled()) throw error("native_auth_unavailable", "Native authentication is unavailable");
        String publicToken = crypto.newToken("");
        String verifier = crypto.newToken("lp_tv_");
        var now = clock.instant();
        AuthenticationChallenge challenge = challengeRepository.save(new AuthenticationChallenge(
                UUID.randomUUID(), AuthenticationChallengePurpose.LOGIN, ExternalIdentityProvider.TELEGRAM,
                null, null, crypto.hash(publicToken), crypto.hash(verifier), now,
                now.plus(properties.challengeTtl())
        ));
        String botUrl = UriComponentsBuilder.fromUriString(
                        "https://t.me/" + properties.telegramBotUsername())
                .queryParam("start", "login_" + publicToken).build(true).toUriString();
        return new TelegramLoginAttemptResponse(challenge.getId(), botUrl, verifier, challenge.getExpiresAt());
    }

    @Transactional
    public boolean approve(String startParameter, TelegramUpdate.TelegramUser user) {
        if (startParameter == null || user == null) return false;
        AuthenticationChallengePurpose purpose;
        String rawToken;
        if (startParameter.startsWith("login_")) {
            purpose = AuthenticationChallengePurpose.LOGIN;
            rawToken = startParameter.substring("login_".length());
        } else if (startParameter.startsWith("link_")) {
            purpose = AuthenticationChallengePurpose.LINK;
            rawToken = startParameter.substring("link_".length());
        } else {
            return false;
        }
        AuthenticationChallenge challenge = challengeRepository.findByProviderAndNonceHashForUpdate(
                ExternalIdentityProvider.TELEGRAM, crypto.hash(rawToken)).orElse(null);
        if (challenge == null) return false;
        var now = clock.instant();
        challenge.requireUsable(purpose, ExternalIdentityProvider.TELEGRAM, now);
        AuthenticatedCustomerIdentity identity = new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.TELEGRAM,
                Long.toString(user.id()),
                user.username(),
                displayName(user),
                user.languageCode()
        );
        if (challenge.getStatus() == AuthenticationChallengeStatus.PROVED) {
            if (!identity.externalSubject().equals(challenge.getProvedSubject())) {
                throw error("auth_challenge_replayed",
                        "Authentication challenge already belongs to another Telegram identity");
            }
            return true;
        }
        try {
            challenge.prove(identity, objectMapper.writeValueAsString(identity), now);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not persist Telegram login proof", exception);
        }
        return true;
    }

    @Transactional
    public SessionTokensResponse exchange(UUID attemptId, TelegramLoginExchangeRequest request) {
        AuthenticationChallenge challenge = challengeRepository.findByIdForUpdate(attemptId)
                .orElseThrow(() -> error("telegram_login_attempt_invalid", "Telegram login attempt is invalid"));
        var now = clock.instant();
        challenge.requireUsable(AuthenticationChallengePurpose.LOGIN, ExternalIdentityProvider.TELEGRAM, now);
        if (challenge.getStatus() != AuthenticationChallengeStatus.PROVED) {
            throw error("telegram_login_pending", "Telegram login is waiting for bot confirmation");
        }
        if (!crypto.hash(request.verifier()).equals(challenge.getVerifierHash())) {
            throw error("telegram_login_verifier_invalid", "Telegram login verifier is invalid");
        }
        try {
            AuthenticatedCustomerIdentity identity = objectMapper.readValue(
                    challenge.getProvedProfile(), AuthenticatedCustomerIdentity.class);
            var customer = accountService.resolveCustomer(identity);
            challenge.consume(now);
            return sessionTokenService.issue(customer, request.clientType());
        } catch (JacksonException exception) {
            throw new IllegalStateException("Stored Telegram proof is invalid", exception);
        }
    }

    private static String displayName(TelegramUpdate.TelegramUser user) {
        String value = ((user.firstName() == null ? "" : user.firstName()) + " "
                + (user.lastName() == null ? "" : user.lastName())).trim();
        return value.isBlank() ? "Telegram user " + user.id() : value;
    }

    private static NativeAuthenticationException error(String code, String message) {
        return new NativeAuthenticationException(code, message);
    }
}
