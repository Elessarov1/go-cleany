package com.cleany.whatsapp;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.cleany.configuration.WhatsAppProperties;

@ConditionalOnProperty(prefix = "whatsapp", name = "enabled", havingValue = "true")
@Component
public class HttpWhatsAppCloudApiClient implements WhatsAppCloudApiClient {

    private final RestClient restClient;
    private final String messagesEndpoint;
    private final String accessToken;

    public HttpWhatsAppCloudApiClient(RestClient.Builder restClientBuilder, WhatsAppProperties properties) {
        restClient = restClientBuilder.build();
        messagesEndpoint = withoutTrailingSlash(properties.graphApiBaseUrl().toString())
                + "/"
                + properties.graphApiVersion()
                + "/"
                + properties.phoneNumberId()
                + "/messages";
        accessToken = properties.accessToken();
    }

    @Override
    public void sendText(String recipientWaId, String text) {
        if (recipientWaId == null || !recipientWaId.matches("[0-9]{7,15}")) {
            throw new IllegalArgumentException("WhatsApp recipient must be a 7-15 digit wa_id");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("WhatsApp message text must not be blank");
        }

        Map<String, Object> request = Map.of(
                "messaging_product", "whatsapp",
                "recipient_type", "individual",
                "to", recipientWaId,
                "type", "text",
                "text", Map.of(
                        "preview_url", false,
                        "body", text
                )
        );
        try {
            SendMessageResponse response = restClient.post()
                    .uri(messagesEndpoint)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .body(request)
                    .retrieve()
                    .body(SendMessageResponse.class);
            if (response == null
                    || response.messages() == null
                    || response.messages().isEmpty()
                    || response.messages().getFirst().id() == null
                    || response.messages().getFirst().id().isBlank()) {
                throw new WhatsAppCloudApiException("WhatsApp send message returned an invalid response");
            }
        } catch (WhatsAppCloudApiException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new WhatsAppCloudApiException(
                    "WhatsApp send message request failed: " + exception.getClass().getSimpleName()
            );
        }
    }

    private static String withoutTrailingSlash(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == '/') {
            end--;
        }
        return value.substring(0, end);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SendMessageResponse(List<SentMessage> messages) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SentMessage(String id) {
    }
}
