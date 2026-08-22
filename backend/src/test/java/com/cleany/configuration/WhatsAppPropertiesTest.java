package com.cleany.configuration;

import java.net.URI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WhatsAppPropertiesTest {

    @Test
    void disabled_allowsCredentialsToBeAbsent() {
        Assertions.assertDoesNotThrow(() -> properties(false, "", "", ""));
    }

    @Test
    void enabled_requiresRuntimeCredentials() {
        Assertions.assertAll(
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> properties(true, "", "app-secret", validVerifyToken())
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> properties(true, "access-token", "", validVerifyToken())
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> properties(true, "access-token", "app-secret", "short")
                )
        );
    }

    @Test
    void enabled_acceptsCompleteConfiguration() {
        Assertions.assertDoesNotThrow(() -> properties(
                true,
                "access-token",
                "app-secret",
                validVerifyToken()
        ));
    }

    private static WhatsAppProperties properties(
            boolean enabled,
            String accessToken,
            String appSecret,
            String verifyToken
    ) {
        return new WhatsAppProperties(
                enabled,
                URI.create("https://graph.facebook.com"),
                "v25.0",
                "1070289752200337",
                "1438307131692197",
                "1580401900215347",
                "1239590005912301",
                accessToken,
                appSecret,
                verifyToken,
                true
        );
    }

    private static String validVerifyToken() {
        return "test_verify_token_1234567890123456";
    }
}
