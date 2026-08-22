package com.cleany.whatsapp;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.cleany.configuration.WhatsAppProperties;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class HttpWhatsAppCloudApiClientTest {

    @Test
    void sendText_postsCloudApiMessageWithBearerToken() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        var client = new HttpWhatsAppCloudApiClient(builder, properties());
        server.expect(requestTo(
                        "https://graph.facebook.com/v25.0/1239590005912301/messages"
                ))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer access-token"))
                .andExpect(content().json("""
                        {
                          "messaging_product": "whatsapp",
                          "recipient_type": "individual",
                          "to": "905551234567",
                          "type": "text",
                          "text": {
                            "preview_url": false,
                            "body": "hello"
                          }
                        }
                        """))
                .andRespond(withSuccess(
                        "{\"messages\":[{\"id\":\"wamid.test\"}]}",
                        MediaType.APPLICATION_JSON
                ));

        client.sendText("905551234567", "hello");

        server.verify();
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
                "app-secret",
                "test_verify_token_1234567890123456",
                true
        );
    }
}
