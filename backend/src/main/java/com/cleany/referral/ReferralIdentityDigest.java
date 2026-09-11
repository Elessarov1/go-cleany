package com.cleany.referral;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.cleany.customer.CustomerExternalIdentity;

import org.springframework.stereotype.Component;

@Component
final class ReferralIdentityDigest {

    private static final String ALGORITHM = "HmacSHA256";

    private final SecretKeySpec key;

    ReferralIdentityDigest(ReferralAntiAbuseProperties properties) {
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(properties.hmacKey());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("referral.anti-abuse.hmac-key must be Base64", exception);
        }
        if (decoded.length != 32) {
            throw new IllegalStateException("referral.anti-abuse.hmac-key must contain exactly 32 bytes");
        }
        key = new SecretKeySpec(decoded, ALGORITHM);
    }

    String digest(CustomerExternalIdentity identity) {
        String canonical = "v1\n" + identity.getProvider().name() + "\n"
                + identity.getIssuer() + "\n" + identity.getExternalSubject();
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            return HexFormat.of().formatHex(mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("HmacSHA256 is unavailable", exception);
        }
    }
}
