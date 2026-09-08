package com.cleany.telegram.bot;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.cleany.configuration.TelegramProperties;
import com.cleany.configuration.TelegramUpdateMode;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class HttpTelegramBotClientTest {

    private MockRestServiceServer server;
    private HttpTelegramBotClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new HttpTelegramBotClient(builder, telegramProperties());
    }

    @Test
    void sendMessage_webAppButtonSerializedAsWebAppInfo() {
        server.expect(once(), requestTo("https://api.telegram.org/bot123456789:test-token/sendMessage"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.chat_id").value(101))
                .andExpect(jsonPath("$.reply_markup.inline_keyboard[0][0].text").value("Open"))
                .andExpect(jsonPath("$.reply_markup.inline_keyboard[0][0].web_app.url")
                        .value("https://loco-place.com/"))
                .andRespond(withSuccess("{\"ok\":true}", MediaType.APPLICATION_JSON));

        client.sendMessage(
                101L,
                "Welcome",
                TelegramBotClient.InlineKeyboard.ofRows(List.of(
                        TelegramBotClient.InlineButton.webApp("Open", "https://loco-place.com/")
                ))
        );

        server.verify();
    }

    @Test
    void presentationMethods_serializeLocalizedCommandsAndGlobalMenu() {
        server.expect(once(), requestTo("https://api.telegram.org/bot123456789:test-token/setMyCommands"))
                .andExpect(jsonPath("$.language_code").value("en"))
                .andExpect(jsonPath("$.commands[0].command").value("start"))
                .andExpect(jsonPath("$.commands[0].description").value("Open Loco Place"))
                .andRespond(withSuccess("{\"ok\":true}", MediaType.APPLICATION_JSON));
        server.expect(once(), requestTo("https://api.telegram.org/bot123456789:test-token/setChatMenuButton"))
                .andExpect(jsonPath("$.menu_button.type").value("web_app"))
                .andExpect(jsonPath("$.menu_button.text").value("Loco Place"))
                .andExpect(jsonPath("$.menu_button.web_app.url").value("https://loco-place.com/"))
                .andRespond(withSuccess("{\"ok\":true}", MediaType.APPLICATION_JSON));

        client.setCommands(
                List.of(new TelegramBotClient.BotCommand("start", "Open Loco Place")),
                "en"
        );
        client.setDefaultMenuButton("Loco Place", "https://loco-place.com/");

        server.verify();
    }

    private static TelegramProperties telegramProperties() {
        return new TelegramProperties(
                "123456789:test-token",
                "",
                Duration.ofHours(1),
                Duration.ofSeconds(30),
                true,
                URI.create("https://api.telegram.org"),
                TelegramUpdateMode.POLLING,
                25,
                Duration.ofSeconds(1),
                false
        );
    }
}
