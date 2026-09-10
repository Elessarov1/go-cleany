package com.cleany.communication;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "fcm", name = "enabled", havingValue = "true")
class GoogleServiceAccountAccessTokenService {
    private final RestClient.Builder restClientBuilder;
    private final FcmProperties properties;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private volatile CachedToken cached;

    synchronized String accessToken() {
        Instant now = clock.instant();
        if (cached != null && now.isBefore(cached.expiresAt().minusSeconds(60))) return cached.value();
        String assertion = assertion(now);
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        form.add("assertion", assertion);
        TokenResponse response = restClientBuilder.build().post().uri(properties.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve()
                .body(TokenResponse.class);
        if (response == null || response.access_token() == null || response.expires_in() <= 0) {
            throw new IllegalStateException("Google service account token response is invalid");
        }
        cached = new CachedToken(response.access_token(), now.plusSeconds(response.expires_in()));
        return cached.value();
    }

    private String assertion(Instant now) {
        try {
            String header = json(Map.of("alg", "RS256", "typ", "JWT"));
            String claims = json(Map.of("iss", properties.clientEmail(), "scope", properties.scope(),
                    "aud", properties.tokenUri(), "iat", now.getEpochSecond(),
                    "exp", now.plusSeconds(3600).getEpochSecond()));
            String unsigned = base64(header) + "." + base64(claims);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey());
            signature.update(unsigned.getBytes(StandardCharsets.UTF_8));
            return unsigned + "." + Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(signature.sign());
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Could not sign Google service account assertion", exception);
        }
    }

    private PrivateKey privateKey() throws GeneralSecurityException {
        String pem = properties.privateKey().replace("\\n", "\n")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        return KeyFactory.getInstance("RSA").generatePrivate(
                new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));
    }

    private String json(Map<String, Object> value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not encode Google service account assertion", exception);
        }
    }

    private static String base64(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private record TokenResponse(String access_token, long expires_in) {
    }
    private record CachedToken(String value, Instant expiresAt) {
    }
}
