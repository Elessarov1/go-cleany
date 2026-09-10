package com.cleany.communication;

import java.time.Duration;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.cleany.authentication.SessionTokenCrypto;
import com.cleany.notification.CustomerNotification;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "fcm", name = "enabled", havingValue = "true")
class FcmNotificationDeliveryChannel implements NotificationDeliveryChannel {
    private final RestClient.Builder restClientBuilder;
    private final GoogleServiceAccountAccessTokenService accessTokenService;
    private final SessionTokenCrypto crypto;
    private final ObjectMapper objectMapper;
    private final FcmProperties properties;

    @Override
    public CommunicationEndpointType endpointType() {
        return CommunicationEndpointType.FCM;
    }

    @Override
    public void deliver(long notificationId, CommunicationEndpoint endpoint,
                        CustomerNotification notification) {
        Map<String, Object> body = Map.of("message", Map.of(
                "token", crypto.decrypt(endpoint.getAddressCiphertext()),
                "notification", Map.of("title", "Loco Place", "body", genericBody(endpoint)),
                "data", Map.of("notificationId", Long.toString(notificationId),
                        "action", actionJson(notification))));
        try {
            restClientBuilder.build().post()
                    .uri("https://fcm.googleapis.com/v1/projects/{project}/messages:send",
                            properties.projectId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenService.accessToken())
                    .contentType(MediaType.APPLICATION_JSON).body(body).retrieve().toBodilessEntity();
        } catch (RestClientResponseException exception) {
            int status = exception.getStatusCode().value();
            String response = exception.getResponseBodyAsString();
            String compactResponse = response == null ? "" : response.replaceAll("\\s", "");
            boolean tokenError = compactResponse.contains(
                    "type.googleapis.com/google.firebase.fcm.v1.FcmError");
            boolean invalid = compactResponse.contains("\"errorCode\":\"UNREGISTERED\"")
                    || (tokenError && compactResponse.contains(
                    "\"errorCode\":\"INVALID_ARGUMENT\""));
            boolean retryable = status == 429 || status >= 500;
            throw new DeliveryFailureException("fcm_http_" + status, retryable, invalid,
                    retryAfter(exception.getResponseHeaders()), exception);
        } catch (RuntimeException exception) {
            if (exception instanceof DeliveryFailureException failure) throw failure;
            throw new DeliveryFailureException("fcm_network_error", true, false, null, exception);
        }
    }

    private String actionJson(CustomerNotification notification) {
        try {
            return objectMapper.writeValueAsString(notification.action());
        } catch (JacksonException exception) {
            throw new DeliveryFailureException("fcm_action_invalid", false, false, null, exception);
        }
    }

    private static String genericBody(CommunicationEndpoint endpoint) {
        return endpoint.getLocale() != null && endpoint.getLocale().toLowerCase().startsWith("en")
                ? "There is an update in your account"
                : "В вашем аккаунте есть обновление";
    }

    private static Duration retryAfter(HttpHeaders headers) {
        if (headers == null) return null;
        String value = headers.getFirst(HttpHeaders.RETRY_AFTER);
        if (value == null) return null;
        try {
            return Duration.ofSeconds(Long.parseLong(value));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
