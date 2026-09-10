package com.cleany.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import com.cleany.customer.ExternalIdentityIssuer;
import com.cleany.customer.ExternalIdentityProvider;

class NativeIdentityTokenVerifierTest {
    private static final Instant NOW = Instant.parse("2026-09-10T10:00:00Z");

    @Test
    void validatesIssuerAudienceNonceAndFreshAuthentication() {
        Harness harness = harness();
        when(harness.google().decode("valid")).thenReturn(jwt(
                ExternalIdentityIssuer.GOOGLE, "android-client", "nonce", NOW.minusSeconds(30)));

        var identity = harness.verifier().verify(
                ExternalIdentityProvider.GOOGLE, "valid", harness.crypto().hash("nonce"),
                Duration.ofMinutes(5));

        assertEquals("customer-subject", identity.externalSubject());
        assertEquals(ExternalIdentityIssuer.GOOGLE, identity.issuer());
    }

    @Test
    void rejectsSignatureExpiryIssuerAudienceNonceAndStaleProof() {
        Harness harness = harness();
        when(harness.google().decode("bad-signature")).thenThrow(new JwtException("signature"));
        when(harness.google().decode("expired")).thenThrow(new JwtException("expired"));
        when(harness.google().decode("issuer")).thenReturn(jwt(
                "https://attacker.example", "android-client", "nonce", NOW));
        when(harness.google().decode("audience")).thenReturn(jwt(
                ExternalIdentityIssuer.GOOGLE, "another-client", "nonce", NOW));
        when(harness.google().decode("nonce")).thenReturn(jwt(
                ExternalIdentityIssuer.GOOGLE, "android-client", "other", NOW));
        when(harness.google().decode("stale")).thenReturn(jwt(
                ExternalIdentityIssuer.GOOGLE, "android-client", "nonce", NOW.minusSeconds(301)));

        assertCode(harness, "bad-signature", "invalid_identity_token");
        assertCode(harness, "expired", "invalid_identity_token");
        assertCode(harness, "issuer", "identity_token_issuer_invalid");
        assertCode(harness, "audience", "identity_token_audience_invalid");
        assertCode(harness, "nonce", "identity_token_nonce_mismatch");
        NativeAuthenticationException stale = assertThrows(NativeAuthenticationException.class,
                () -> harness.verifier().verify(ExternalIdentityProvider.GOOGLE, "stale",
                        harness.crypto().hash("nonce"), Duration.ofMinutes(5)));
        assertEquals("identity_proof_too_old", stale.code());
    }

    @Test
    void acceptsAppleHashedNonce() {
        Harness harness = harness();
        String nonceHash = harness.crypto().hash("apple-raw-nonce");
        when(harness.apple().decode("apple")).thenReturn(jwt(
                ExternalIdentityIssuer.APPLE, "com.locoplace.app", nonceHash, NOW));

        var identity = harness.verifier().verify(ExternalIdentityProvider.APPLE, "apple", nonceHash);

        assertEquals(ExternalIdentityProvider.APPLE, identity.provider());
    }

    private static void assertCode(Harness harness, String token, String expectedCode) {
        NativeAuthenticationException exception = assertThrows(NativeAuthenticationException.class,
                () -> harness.verifier().verify(ExternalIdentityProvider.GOOGLE, token,
                        harness.crypto().hash("nonce")));
        assertEquals(expectedCode, exception.code());
    }

    private static Jwt jwt(String issuer, String audience, String nonce, Instant issuedAt) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuer(issuer)
                .subject("customer-subject")
                .audience(List.of(audience))
                .issuedAt(issuedAt)
                .expiresAt(NOW.plus(Duration.ofMinutes(10)))
                .claim("nonce", nonce)
                .claim("email", "customer@example.test")
                .claim("email_verified", true)
                .build();
    }

    private static Harness harness() {
        NativeAuthProperties properties = new NativeAuthProperties(
                true, Duration.ofMinutes(15), Duration.ofDays(30), Duration.ofDays(90),
                Duration.ofMinutes(2), Duration.ofMinutes(10), Duration.ofMinutes(5),
                Base64.getEncoder().encodeToString(new byte[32]),
                List.of("android-client"), List.of("com.locoplace.app"), "loco_place_bot");
        SessionTokenCrypto crypto = new SessionTokenCrypto(properties);
        JwtDecoder google = mock(JwtDecoder.class);
        JwtDecoder apple = mock(JwtDecoder.class);
        NativeIdentityTokenVerifier verifier = new NativeIdentityTokenVerifier(
                properties, crypto, Clock.fixed(NOW, ZoneOffset.UTC), google, apple);
        return new Harness(verifier, crypto, google, apple);
    }

    private record Harness(NativeIdentityTokenVerifier verifier, SessionTokenCrypto crypto,
                           JwtDecoder google, JwtDecoder apple) {
    }
}
