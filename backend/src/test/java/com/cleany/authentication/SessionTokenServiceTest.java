package com.cleany.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountRepository;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.customer.ExternalIdentityProvider;

class SessionTokenServiceTest {

    @Test
    void lostRefreshResponseCanBeReplayedWithSameKeyButReuseWithAnotherKeyRevokesFamily() {
        CustomerSessionRepository sessions = mock(CustomerSessionRepository.class);
        UsedRefreshTokenRepository usedTokens = mock(UsedRefreshTokenRepository.class);
        NativeAuthProperties properties = properties();
        SessionTokenCrypto crypto = new SessionTokenCrypto(properties);
        SessionTokenService service = new SessionTokenService(
                sessions,
                usedTokens,
                mock(CustomerExternalIdentityRepository.class),
                mock(CustomerAccountRepository.class),
                crypto,
                properties,
                Clock.fixed(Instant.parse("2026-09-10T10:00:00Z"), ZoneOffset.UTC)
        );
        CurrentCustomer customer = new CurrentCustomer(
                7L, 11L, ExternalIdentityProvider.GOOGLE, "subject", null, "Customer", "en");

        final CustomerSession[] captured = new CustomerSession[1];
        when(sessions.save(any(CustomerSession.class))).thenAnswer(invocation -> {
            captured[0] = invocation.getArgument(0);
            return captured[0];
        });
        SessionTokensResponse issued = service.issue(customer, NativeClientType.IOS);
        CustomerSession session = captured[0];
        String issuedRefreshHash = crypto.hash(issued.refreshToken());
        when(sessions.findByRefreshHashForUpdate(issuedRefreshHash))
                .thenReturn(Optional.of(session), Optional.empty(), Optional.empty());

        SessionTokensResponse rotated = service.refresh(issued.refreshToken(), "attempt-one");
        when(usedTokens.findById(issuedRefreshHash)).thenReturn(Optional.of(
                new UsedRefreshToken(issuedRefreshHash, session.getId(),
                        Instant.parse("2026-09-10T10:00:00Z"))));
        when(sessions.findByIdForUpdate(session.getId())).thenReturn(Optional.of(session));
        SessionTokensResponse replayed = service.refresh(issued.refreshToken(), "attempt-one");
        assertEquals(rotated, replayed);
        assertNotEquals(issued.refreshToken(), rotated.refreshToken());
        assertEquals(Instant.parse("2026-12-09T10:00:00Z"), rotated.absoluteExpiresAt());

        SessionTokenException reuse = assertThrows(SessionTokenException.class,
                () -> service.refresh(issued.refreshToken(), "attempt-two"));
        assertEquals("refresh_token_reused", reuse.code());
    }

    @Test
    void refreshReuseFromOlderRotationIsResolvedThroughUsedTokenHistory() {
        CustomerSessionRepository sessions = mock(CustomerSessionRepository.class);
        UsedRefreshTokenRepository usedTokens = mock(UsedRefreshTokenRepository.class);
        NativeAuthProperties properties = properties();
        SessionTokenCrypto crypto = new SessionTokenCrypto(properties);
        SessionTokenService service = new SessionTokenService(
                sessions, usedTokens,
                mock(CustomerExternalIdentityRepository.class),
                mock(CustomerAccountRepository.class), crypto, properties,
                Clock.fixed(Instant.parse("2026-09-10T10:00:00Z"), ZoneOffset.UTC));
        final CustomerSession[] captured = new CustomerSession[1];
        when(sessions.save(any(CustomerSession.class))).thenAnswer(invocation -> {
            captured[0] = invocation.getArgument(0);
            return captured[0];
        });
        CurrentCustomer customer = new CurrentCustomer(
                7L, 11L, ExternalIdentityProvider.GOOGLE, "subject", null, "Customer", "en");
        SessionTokensResponse first = service.issue(customer, NativeClientType.ANDROID);
        when(sessions.findByRefreshHashForUpdate(any())).thenReturn(Optional.of(captured[0]));
        SessionTokensResponse second = service.refresh(first.refreshToken(), "first-rotation");
        service.refresh(second.refreshToken(), "second-rotation");

        String oldestHash = crypto.hash(first.refreshToken());
        when(sessions.findByRefreshHashForUpdate(oldestHash)).thenReturn(Optional.empty());
        when(usedTokens.findById(oldestHash)).thenReturn(Optional.of(
                new UsedRefreshToken(oldestHash, captured[0].getId(), Instant.parse("2026-09-10T10:00:00Z"))));
        when(sessions.findByIdForUpdate(captured[0].getId())).thenReturn(Optional.of(captured[0]));

        SessionTokenException reuse = assertThrows(SessionTokenException.class,
                () -> service.refresh(first.refreshToken(), "late-reuse"));
        assertEquals("refresh_token_reused", reuse.code());
    }

    private static NativeAuthProperties properties() {
        return new NativeAuthProperties(
                true,
                Duration.ofMinutes(15),
                Duration.ofDays(30),
                Duration.ofDays(90),
                Duration.ofMinutes(2),
                Duration.ofMinutes(10),
                Duration.ofMinutes(5),
                Base64.getEncoder().encodeToString(new byte[32]),
                Collections.emptyList(),
                Collections.emptyList(),
                "loco_place_bot"
        );
    }
}
