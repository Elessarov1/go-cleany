package com.cleany.whatsapp;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.cleany.configuration.WhatsAppProperties;

@ConditionalOnProperty(prefix = "whatsapp", name = "enabled", havingValue = "true")
@Component
public class WhatsAppWebhookAuthenticator {

    private static final String SIGNATURE_PREFIX = "sha256=";

    private final byte[] appSecret;
    private final byte[] webhookVerifyToken;

    public WhatsAppWebhookAuthenticator(WhatsAppProperties properties) {
        appSecret = properties.appSecret().getBytes(StandardCharsets.UTF_8);
        webhookVerifyToken = properties.webhookVerifyToken().getBytes(StandardCharsets.UTF_8);
    }

    public boolean isValidVerificationRequest(String mode, String suppliedToken) {
        byte[] suppliedBytes = suppliedToken == null
                ? new byte[0]
                : suppliedToken.getBytes(StandardCharsets.UTF_8);
        return "subscribe".equals(mode)
                && MessageDigest.isEqual(webhookVerifyToken, suppliedBytes);
    }

    public void validateSignature(String suppliedSignature, byte[] payload) {
        byte[] suppliedDigest = parseSignature(suppliedSignature);
        byte[] expectedDigest = hmac(payload == null ? new byte[0] : payload);
        if (!MessageDigest.isEqual(expectedDigest, suppliedDigest)) {
            throw new WhatsAppWebhookAuthenticationException();
        }
    }

    private byte[] hmac(byte[] payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret, "HmacSHA256"));
            return mac.doFinal(payload);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("HmacSHA256 is unavailable", exception);
        }
    }

    private static byte[] parseSignature(String suppliedSignature) {
        if (suppliedSignature == null || !suppliedSignature.startsWith(SIGNATURE_PREFIX)) {
            return new byte[0];
        }
        String hexDigest = suppliedSignature.substring(SIGNATURE_PREFIX.length());
        if (!hexDigest.matches("[0-9a-fA-F]{64}")) {
            return new byte[0];
        }
        return HexFormat.of().parseHex(hexDigest);
    }
}
