package com.cleany.telegram;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final class TelegramInitDataTestFactory {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private TelegramInitDataTestFactory() {
    }

    static String signed(String botToken, Instant authDate, String userJson) {
        Map<String, String> fields = new TreeMap<>();
        fields.put("auth_date", Long.toString(authDate.getEpochSecond()));
        fields.put("query_id", "AAExampleQuery");
        fields.put("user", userJson);

        String dataCheckString = fields.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("\n"));
        byte[] secretKey = hmac(
                "WebAppData".getBytes(StandardCharsets.UTF_8),
                botToken.getBytes(StandardCharsets.UTF_8)
        );
        fields.put("hash", HexFormat.of().formatHex(
                hmac(secretKey, dataCheckString.getBytes(StandardCharsets.UTF_8))
        ));

        return fields.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static byte[] hmac(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
            return mac.doFinal(data);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
