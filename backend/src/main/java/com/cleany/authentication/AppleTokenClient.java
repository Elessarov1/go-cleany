package com.cleany.authentication;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Clock;
import java.util.Base64;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AppleTokenClient {
    private final RestClient.Builder restClientBuilder;
    private final AppleAuthProperties properties;
    private final Clock clock;

    AppleTokens exchange(String authorizationCode) {
        properties.requireConfigured();
        var form = new LinkedMultiValueMap<String, String>();
        form.add("client_id", properties.clientId());
        form.add("client_secret", clientSecret());
        form.add("code", authorizationCode);
        form.add("grant_type", "authorization_code");
        try {
            AppleTokens tokens = restClientBuilder.build().post().uri(properties.tokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve()
                    .body(AppleTokens.class);
            if (tokens == null || tokens.refresh_token() == null || tokens.refresh_token().isBlank()) {
                throw new NativeAuthenticationException("apple_token_exchange_invalid",
                        "Apple token exchange returned no refresh token");
            }
            return tokens;
        } catch (RestClientResponseException exception) {
            throw new NativeAuthenticationException("apple_token_exchange_failed",
                    "Apple authorization code could not be exchanged");
        }
    }

    void revoke(String refreshToken) {
        properties.requireConfigured();
        var form = new LinkedMultiValueMap<String, String>();
        form.add("client_id", properties.clientId());
        form.add("client_secret", clientSecret());
        form.add("token", refreshToken);
        form.add("token_type_hint", "refresh_token");
        restClientBuilder.build().post().uri(properties.revokeUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve()
                .toBodilessEntity();
    }

    private String clientSecret() {
        try {
            var now = clock.instant();
            SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.ES256)
                    .keyID(properties.keyId()).build(), new JWTClaimsSet.Builder()
                    .issuer(properties.teamId()).subject(properties.clientId())
                    .audience("https://appleid.apple.com")
                    .issueTime(Date.from(now)).expirationTime(Date.from(now.plusSeconds(300))).build());
            jwt.sign(new ECDSASigner(privateKey()));
            return jwt.serialize();
        } catch (GeneralSecurityException | JOSEException exception) {
            throw new IllegalStateException("Could not sign Apple client secret", exception);
        }
    }

    private ECPrivateKey privateKey() throws GeneralSecurityException {
        String pem = properties.privateKey().replace("\\n", "\n")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        return (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));
    }

    record AppleTokens(String access_token, String token_type, long expires_in,
                       String refresh_token, String id_token) {
    }
}
