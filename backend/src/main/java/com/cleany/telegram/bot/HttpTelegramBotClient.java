package com.cleany.telegram.bot;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.cleany.configuration.TelegramProperties;

@ConditionalOnProperty(prefix = "telegram", name = "bot-enabled", havingValue = "true")
@Component
public class HttpTelegramBotClient implements TelegramBotClient {

    private final RestClient restClient;
    private final String botApiEndpoint;
    private final String fileApiEndpoint;

    public HttpTelegramBotClient(RestClient.Builder restClientBuilder, TelegramProperties properties) {
        restClient = restClientBuilder.build();
        botApiEndpoint = withoutTrailingSlash(properties.apiBaseUrl().toString())
                + "/bot"
                + properties.botToken();
        fileApiEndpoint = withoutTrailingSlash(properties.apiBaseUrl().toString())
                + "/file/bot"
                + properties.botToken();
    }

    @Override
    public void deleteWebhook(boolean dropPendingUpdates) {
        invoke("deleteWebhook", Map.of("drop_pending_updates", dropPendingUpdates), TelegramApiResponse.class);
    }

    @Override
    public List<TelegramUpdate> getUpdates(long offset, int timeoutSeconds) {
        TelegramUpdatesResponse response = invoke(
                "getUpdates",
                Map.of(
                        "offset", offset,
                        "timeout", timeoutSeconds,
                        "allowed_updates", List.of("message", "callback_query")
                ),
                TelegramUpdatesResponse.class
        );
        return response.result() == null ? Collections.emptyList() : List.copyOf(response.result());
    }

    @Override
    public void sendMessage(long chatId, String text, InlineKeyboard keyboard) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("chat_id", chatId);
        request.put("text", text);
        if (keyboard != null && !keyboard.rows().isEmpty()) {
            request.put("reply_markup", Map.of("inline_keyboard", serializeRows(keyboard)));
        }
        invoke("sendMessage", request, TelegramApiResponse.class);
    }

    @Override
    public void sendPhoto(long chatId, String telegramFileId) {
        invoke("sendPhoto", Map.of(
                "chat_id", chatId,
                "photo", telegramFileId
        ), TelegramApiResponse.class);
    }

    @Override
    public byte[] downloadFile(String telegramFileId) {
        TelegramFileResponse response = invoke(
                "getFile",
                Map.of("file_id", telegramFileId),
                TelegramFileResponse.class
        );
        String filePath = response.result() == null ? null : response.result().filePath();
        if (filePath == null
                || filePath.isBlank()
                || filePath.startsWith("/")
                || filePath.contains("..")) {
            throw new TelegramBotApiException("Telegram getFile returned an invalid file path");
        }
        try {
            byte[] content = restClient.get()
                    .uri(fileApiEndpoint + "/" + filePath)
                    .retrieve()
                    .body(byte[].class);
            if (content == null) {
                throw new TelegramBotApiException("Telegram file download returned an empty response");
            }
            return content;
        } catch (TelegramBotApiException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new TelegramBotApiException(
                    "Telegram file download failed: " + exception.getClass().getSimpleName()
            );
        }
    }

    @Override
    public void answerCallbackQuery(String callbackQueryId, String text, boolean showAlert) {
        invoke("answerCallbackQuery", Map.of(
                "callback_query_id", callbackQueryId,
                "text", text,
                "show_alert", showAlert
        ), TelegramApiResponse.class);
    }

    @Override
    public void setName(String name, String languageCode) {
        invoke("setMyName", localizedRequest("name", name, languageCode), TelegramApiResponse.class);
    }

    @Override
    public void setDescription(String description, String languageCode) {
        invoke(
                "setMyDescription",
                localizedRequest("description", description, languageCode),
                TelegramApiResponse.class
        );
    }

    @Override
    public void setShortDescription(String shortDescription, String languageCode) {
        invoke(
                "setMyShortDescription",
                localizedRequest("short_description", shortDescription, languageCode),
                TelegramApiResponse.class
        );
    }

    @Override
    public void setCommands(List<BotCommand> commands, String languageCode) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("commands", commands.stream()
                .map(command -> Map.of(
                        "command", command.command(),
                        "description", command.description()
                ))
                .toList());
        putLanguageCode(request, languageCode);
        invoke("setMyCommands", request, TelegramApiResponse.class);
    }

    @Override
    public void setDefaultMenuButton(String text, String webAppUrl) {
        invoke("setChatMenuButton", Map.of(
                "menu_button", Map.of(
                        "type", "web_app",
                        "text", text,
                        "web_app", Map.of("url", webAppUrl)
                )
        ), TelegramApiResponse.class);
    }

    private <T extends ApiResponse> T invoke(
            String method,
            Map<String, Object> request,
            Class<T> responseType
    ) {
        try {
            T response = restClient.post()
                    .uri(botApiEndpoint + "/" + method)
                    .body(request)
                    .retrieve()
                    .body(responseType);
            if (response == null || !response.ok()) {
                String description = response == null ? "empty response" : response.description();
                throw new TelegramBotApiException("Telegram " + method + " failed: " + description);
            }
            return response;
        } catch (TelegramBotApiException exception) {
            throw exception;
        } catch (RestClientException exception) {
            // RestClient exception messages may contain the request URI, whose path includes the bot token.
            throw new TelegramBotApiException(
                    "Telegram " + method + " request failed: " + exception.getClass().getSimpleName()
            );
        }
    }

    private static List<List<Map<String, Object>>> serializeRows(InlineKeyboard keyboard) {
        return keyboard.rows().stream()
                .map(row -> row.stream().map(HttpTelegramBotClient::serializeButton).toList())
                .toList();
    }

    private static Map<String, Object> serializeButton(InlineButton button) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("text", button.text());
        if (button.callbackData() != null) {
            result.put("callback_data", button.callbackData());
        } else if (button.url() != null) {
            result.put("url", button.url());
        } else {
            result.put("web_app", Map.of("url", button.webAppUrl()));
        }
        return result;
    }

    private static Map<String, Object> localizedRequest(String key, String value, String languageCode) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put(key, value);
        putLanguageCode(request, languageCode);
        return request;
    }

    private static void putLanguageCode(Map<String, Object> request, String languageCode) {
        if (languageCode != null && !languageCode.isBlank()) {
            request.put("language_code", languageCode);
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
    private record TelegramApiResponse(boolean ok, String description) implements ApiResponse {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TelegramUpdatesResponse(
            boolean ok,
            String description,
            List<TelegramUpdate> result
    ) implements ApiResponse {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TelegramFileResponse(
            boolean ok,
            String description,
            TelegramFileResult result
    ) implements ApiResponse {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TelegramFileResult(
            @com.fasterxml.jackson.annotation.JsonProperty("file_path") String filePath
    ) {
    }

    private interface ApiResponse {

        boolean ok();

        String description();
    }
}
