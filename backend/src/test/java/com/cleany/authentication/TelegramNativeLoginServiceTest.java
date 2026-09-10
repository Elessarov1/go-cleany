package com.cleany.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;
import com.cleany.telegram.bot.TelegramUpdate;

import tools.jackson.databind.ObjectMapper;

class TelegramNativeLoginServiceTest {

    @Test
    void telegramProofIsIdempotentForSameUserAndRejectsReplayByAnotherUser() {
        NativeAuthProperties properties = properties();
        SessionTokenCrypto crypto = new SessionTokenCrypto(properties);
        AuthenticationChallengeRepository challenges = mock(AuthenticationChallengeRepository.class);
        Instant now = Instant.parse("2026-09-10T10:00:00Z");
        String token = "single-use-token";
        AuthenticationChallenge challenge = new AuthenticationChallenge(
                UUID.randomUUID(), AuthenticationChallengePurpose.LOGIN,
                ExternalIdentityProvider.TELEGRAM, null, null, crypto.hash(token),
                crypto.hash("verifier"), now, now.plus(Duration.ofMinutes(10)));
        when(challenges.findByProviderAndNonceHashForUpdate(
                ExternalIdentityProvider.TELEGRAM, crypto.hash(token)))
                .thenReturn(Optional.of(challenge));
        TelegramNativeLoginService service = new TelegramNativeLoginService(
                challenges, mock(CustomerAccountService.class), mock(SessionTokenService.class),
                crypto, properties, new ObjectMapper(), Clock.fixed(now, ZoneOffset.UTC));

        TelegramUpdate.TelegramUser owner = user(101L);
        assertTrue(service.approve("login_" + token, owner));
        assertTrue(service.approve("login_" + token, owner));

        NativeAuthenticationException replay = assertThrows(NativeAuthenticationException.class,
                () -> service.approve("login_" + token, user(202L)));
        assertEquals("auth_challenge_replayed", replay.code());
        assertEquals("101", challenge.getProvedSubject());
    }

    private static TelegramUpdate.TelegramUser user(long id) {
        return new TelegramUpdate.TelegramUser(id, "user" + id, "Test", "User", "en");
    }

    private static NativeAuthProperties properties() {
        return new NativeAuthProperties(
                true, Duration.ofMinutes(15), Duration.ofDays(30), Duration.ofDays(90),
                Duration.ofMinutes(2), Duration.ofMinutes(10), Duration.ofMinutes(5),
                Base64.getEncoder().encodeToString(new byte[32]),
                Collections.emptyList(), Collections.emptyList(), "loco_place_bot");
    }
}
