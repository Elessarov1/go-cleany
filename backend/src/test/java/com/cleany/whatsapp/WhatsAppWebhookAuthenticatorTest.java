package com.cleany.whatsapp;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.cleany.configuration.WhatsAppProperties;

class WhatsAppWebhookAuthenticatorTest {

    private static final String APP_SECRET = "test-app-secret";
    private static final String VERIFY_TOKEN = "test_verify_token_1234567890123456";

    private final WhatsAppWebhookAuthenticator authenticator = new WhatsAppWebhookAuthenticator(properties());

    @Test
    void matchingVerificationToken_accepted() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(
                        authenticator.isValidVerificationRequest("subscribe", VERIFY_TOKEN)
                ),
                () -> Assertions.assertFalse(
                        authenticator.isValidVerificationRequest("subscribe", "different")
                ),
                () -> Assertions.assertFalse(
                        authenticator.isValidVerificationRequest("unsubscribe", VERIFY_TOKEN)
                )
        );
    }

    @Test
    void validPayloadSignature_accepted() throws Exception {
        byte[] payload = "{\"object\":\"whatsapp_business_account\"}".getBytes(StandardCharsets.UTF_8);

        Assertions.assertDoesNotThrow(() -> authenticator.validateSignature(signature(payload), payload));
    }

    @Test
    void missingOrDifferentPayloadSignature_rejected() {
        byte[] payload = "{}".getBytes(StandardCharsets.UTF_8);

        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        WhatsAppWebhookAuthenticationException.class,
                        () -> authenticator.validateSignature(null, payload)
                ),
                () -> Assertions.assertThrows(
                        WhatsAppWebhookAuthenticationException.class,
                        () -> authenticator.validateSignature("sha256=" + "0".repeat(64), payload)
                )
        );
    }

    private static String signature(byte[] payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(APP_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return "sha256=" + HexFormat.of().formatHex(mac.doFinal(payload));
    }

    private static WhatsAppProperties properties() {
        return new WhatsAppProperties(
                true,
                URI.create("https://graph.facebook.com"),
                "v25.0",
                "1070289752200337",
                "1438307131692197",
                "1580401900215347",
                "1239590005912301",
                "access-token",
                APP_SECRET,
                VERIFY_TOKEN,
                true
        );
    }
}
