package com.cleany.authentication;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.ExternalIdentityIssuer;
import com.cleany.customer.ExternalIdentityProvider;

@Component
class NativeIdentityTokenVerifier {
    private static final Set<String> GOOGLE_ISSUERS = Set.of(
            "accounts.google.com", ExternalIdentityIssuer.GOOGLE);
    private final NativeAuthProperties properties;
    private final SessionTokenCrypto crypto;
    private final Clock clock;
    private final JwtDecoder googleDecoder;
    private final JwtDecoder appleDecoder;

    @Autowired
    NativeIdentityTokenVerifier(NativeAuthProperties properties, SessionTokenCrypto crypto, Clock clock) {
        this(properties, crypto, clock,
                NimbusJwtDecoder.withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs").build(),
                NimbusJwtDecoder.withJwkSetUri("https://appleid.apple.com/auth/keys").build());
        ((NimbusJwtDecoder) googleDecoder).setJwtValidator(JwtValidators.createDefault());
        ((NimbusJwtDecoder) appleDecoder).setJwtValidator(JwtValidators.createDefault());
    }

    NativeIdentityTokenVerifier(NativeAuthProperties properties, SessionTokenCrypto crypto, Clock clock,
                                JwtDecoder googleDecoder, JwtDecoder appleDecoder) {
        this.properties = properties;
        this.crypto = crypto;
        this.clock = clock;
        this.googleDecoder = googleDecoder;
        this.appleDecoder = appleDecoder;
    }

    AuthenticatedCustomerIdentity verify(ExternalIdentityProvider provider, String token, String nonceHash) {
        return verify(provider, token, nonceHash, null);
    }

    AuthenticatedCustomerIdentity verify(ExternalIdentityProvider provider, String token, String nonceHash,
                                         Duration maximumAuthenticationAge) {
        if (provider != ExternalIdentityProvider.GOOGLE && provider != ExternalIdentityProvider.APPLE) {
            throw error("native_provider_unsupported", "Native identity-token provider is unsupported");
        }
        Jwt jwt;
        try {
            jwt = (provider == ExternalIdentityProvider.GOOGLE ? googleDecoder : appleDecoder).decode(token);
        } catch (JwtException exception) {
            throw error("invalid_identity_token", "Identity token is invalid");
        }
        validateIssuer(provider, jwt.getIssuer() == null ? null : jwt.getIssuer().toString());
        validateAudience(provider, jwt.getAudience());
        if (maximumAuthenticationAge != null && (jwt.getIssuedAt() == null
                || jwt.getIssuedAt().isBefore(clock.instant().minus(maximumAuthenticationAge))
                || jwt.getIssuedAt().isAfter(clock.instant().plusSeconds(30)))) {
            throw error("identity_proof_too_old", "Identity proof is too old");
        }
        String nonce = jwt.getClaimAsString("nonce");
        boolean rawNonceMatches = nonce != null && constantTimeEquals(crypto.hash(nonce), nonceHash);
        boolean appleHashedNonceMatches = provider == ExternalIdentityProvider.APPLE
                && nonce != null && constantTimeEquals(nonce, nonceHash);
        if (!rawNonceMatches && !appleHashedNonceMatches) {
            throw error("identity_token_nonce_mismatch", "Identity token nonce does not match challenge");
        }
        String subject = jwt.getSubject();
        if (subject == null || subject.isBlank()) throw error("identity_token_subject_missing", "Identity token has no subject");
        String email = jwt.getClaimAsString("email");
        Object emailVerifiedClaim = jwt.getClaim("email_verified");
        boolean emailVerified = Boolean.TRUE.equals(emailVerifiedClaim)
                || "true".equalsIgnoreCase(String.valueOf(emailVerifiedClaim));
        String name = first(jwt.getClaimAsString("name"), jwt.getClaimAsString("given_name"), email, subject);
        return new AuthenticatedCustomerIdentity(
                provider,
                provider == ExternalIdentityProvider.GOOGLE ? ExternalIdentityIssuer.GOOGLE : ExternalIdentityIssuer.APPLE,
                subject,
                email,
                name,
                normalizeLocale(jwt.getClaimAsString("locale")),
                email,
                emailVerified,
                false
        );
    }

    private void validateIssuer(ExternalIdentityProvider provider, String issuer) {
        boolean valid = provider == ExternalIdentityProvider.GOOGLE
                ? GOOGLE_ISSUERS.contains(issuer)
                : ExternalIdentityIssuer.APPLE.equals(issuer);
        if (!valid) throw error("identity_token_issuer_invalid", "Identity token issuer is invalid");
    }

    private void validateAudience(ExternalIdentityProvider provider, List<String> audiences) {
        List<String> allowed = provider == ExternalIdentityProvider.GOOGLE
                ? properties.googleAudiences() : properties.appleAudiences();
        if (allowed.isEmpty()) throw error("native_provider_unconfigured", "Native provider audience is not configured");
        if (audiences == null || audiences.stream().noneMatch(allowed::contains)) {
            throw error("identity_token_audience_invalid", "Identity token audience is invalid");
        }
    }

    private static String first(String... values) {
        for (String value : values) if (value != null && !value.isBlank()) return value.trim();
        return null;
    }

    private static String normalizeLocale(String value) {
        return value == null ? null : value.toLowerCase(Locale.ROOT).replace('_', '-');
    }

    private static NativeAuthenticationException error(String code, String message) {
        return new NativeAuthenticationException(code, message);
    }

    private static boolean constantTimeEquals(String first, String second) {
        if (first == null || second == null) return false;
        return MessageDigest.isEqual(first.getBytes(StandardCharsets.US_ASCII),
                second.getBytes(StandardCharsets.US_ASCII));
    }
}
