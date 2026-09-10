package com.cleany.communication;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.cleany.action.ActionTarget;
import com.cleany.authentication.SessionTokenCrypto;
import com.cleany.notification.CustomerNotification;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class FcmNotificationDeliveryChannelTest {
    private MockRestServiceServer server;
    private FcmNotificationDeliveryChannel channel;
    private CommunicationEndpoint endpoint;
    private CustomerNotification notification;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        var accessTokens = Mockito.mock(GoogleServiceAccountAccessTokenService.class);
        Mockito.when(accessTokens.accessToken()).thenReturn("access-token");
        var crypto = Mockito.mock(SessionTokenCrypto.class);
        Mockito.when(crypto.decrypt("encrypted-registration")).thenReturn("registration-token");
        channel = new FcmNotificationDeliveryChannel(builder, accessTokens, crypto,
                new ObjectMapper(), new FcmProperties(true, "test-project", "fcm@example.test",
                "private-key", null, null));
        endpoint = Mockito.mock(CommunicationEndpoint.class);
        Mockito.when(endpoint.getAddressCiphertext()).thenReturn("encrypted-registration");
        Mockito.when(endpoint.getLocale()).thenReturn("en");
        notification = Mockito.mock(CustomerNotification.class);
        Mockito.when(notification.action()).thenReturn(new ActionTarget.OpenCleaningHistory());
    }

    @Test
    void rateLimitIsRetryableAndHonorsRetryAfter() {
        expect().andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, "240"));

        DeliveryFailureException failure = assertThrows(DeliveryFailureException.class,
                () -> channel.deliver(17L, endpoint, notification));

        assertTrue(failure.retryable());
        assertFalse(failure.invalidEndpoint());
        assertEquals(Duration.ofMinutes(4), failure.retryAfter());
        server.verify();
    }

    @Test
    void fcmUnregisteredDetailDisablesOnlyTheInvalidEndpoint() {
        expect().andRespond(withStatus(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"error":{"details":[{"@type":"type.googleapis.com/google.firebase.fcm.v1.FcmError",
                        "errorCode":"UNREGISTERED"}]}}
                        """));

        DeliveryFailureException failure = assertThrows(DeliveryFailureException.class,
                () -> channel.deliver(18L, endpoint, notification));

        assertFalse(failure.retryable());
        assertTrue(failure.invalidEndpoint());
        server.verify();
    }

    @Test
    void genericInvalidArgumentDoesNotDetachAHealthyRegistration() {
        expect().andRespond(withStatus(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\":{\"status\":\"INVALID_ARGUMENT\"}}"));

        DeliveryFailureException failure = assertThrows(DeliveryFailureException.class,
                () -> channel.deliver(19L, endpoint, notification));

        assertFalse(failure.retryable());
        assertFalse(failure.invalidEndpoint());
        server.verify();
    }

    private org.springframework.test.web.client.ResponseActions expect() {
        return server.expect(once(), requestTo(
                        "https://fcm.googleapis.com/v1/projects/test-project/messages:send"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer access-token"));
    }
}
