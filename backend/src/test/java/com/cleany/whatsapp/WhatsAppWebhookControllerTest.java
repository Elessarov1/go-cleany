package com.cleany.whatsapp;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cleany.configuration.WhatsAppProperties;

import tools.jackson.databind.ObjectMapper;

class WhatsAppWebhookControllerTest {

    private static final String APP_SECRET = "test-app-secret";
    private static final String VERIFY_TOKEN = "test_verify_token_1234567890123456";

    private final WhatsAppWebhookService webhookService = Mockito.mock(WhatsAppWebhookService.class);
    private final WhatsAppWebhookController controller = new WhatsAppWebhookController(
            new ObjectMapper(),
            new WhatsAppWebhookAuthenticator(properties()),
            webhookService
    );

    @Test
    void validVerificationRequest_returnsChallenge() {
        var response = controller.verify("subscribe", VERIFY_TOKEN, "123456789");

        Assertions.assertAll(
                () -> Assertions.assertEquals(200, response.getStatusCode().value()),
                () -> Assertions.assertEquals("123456789", response.getBody())
        );
    }

    @Test
    void invalidVerificationRequest_rejected() {
        var response = controller.verify("subscribe", "different", "123456789");

        Assertions.assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void signedWebhook_deserializedAndHandled() throws Exception {
        byte[] payload = """
                {
                  "object": "whatsapp_business_account",
                  "entry": [{"id": "1580401900215347", "changes": []}]
                }
                """.getBytes(StandardCharsets.UTF_8);

        var response = controller.receive(signature(payload), payload);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Mockito.verify(webhookService).handle(Mockito.argThat(update ->
                "whatsapp_business_account".equals(update.object())
                        && update.entry().size() == 1
                        && "1580401900215347".equals(update.entry().getFirst().id())
        ));
    }

    @Test
    void unsignedWebhook_rejectedBeforeHandling() {
        byte[] payload = "{}".getBytes(StandardCharsets.UTF_8);

        Assertions.assertThrows(
                WhatsAppWebhookAuthenticationException.class,
                () -> controller.receive(null, payload)
        );
        Mockito.verifyNoInteractions(webhookService);
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
