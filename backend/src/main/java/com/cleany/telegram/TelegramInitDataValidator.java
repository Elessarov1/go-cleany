package com.cleany.telegram;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

import com.cleany.authentication.CustomerAuthenticationRequiredException;

import com.cleany.configuration.TelegramProperties;

@Component
public class TelegramInitDataValidator {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final byte[] WEB_APP_DATA_KEY = "WebAppData".getBytes(StandardCharsets.UTF_8);
    private static final int SHA_256_HEX_LENGTH = 64;
    private static final int MAX_INIT_DATA_LENGTH = 16_384;

    private final TelegramProperties properties;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public TelegramInitDataValidator(
            TelegramProperties properties,
            ObjectMapper objectMapper,
            Clock clock
    ) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    public TelegramPrincipal validate(String initData) {
        try {
            Map<String, String> fields = parseFields(initData);
            verifyHash(fields);
            verifyAuthDate(fields.get("auth_date"));
            return parsePrincipal(fields.get("user"));
        } catch (CustomerAuthenticationRequiredException exception) {
            throw exception;
        } catch (IllegalArgumentException | DateTimeException | JacksonException exception) {
            throw new CustomerAuthenticationRequiredException();
        }
    }

    private Map<String, String> parseFields(String initData) {
        if (initData == null || initData.isBlank() || initData.length() > MAX_INIT_DATA_LENGTH) {
            throw new CustomerAuthenticationRequiredException();
        }

        Map<String, String> fields = new TreeMap<>();
        for (String pair : initData.split("&", -1)) {
            int separatorIndex = pair.indexOf('=');
            if (separatorIndex < 1) {
                throw new CustomerAuthenticationRequiredException();
            }

            String key = decode(pair.substring(0, separatorIndex));
            String value = decode(pair.substring(separatorIndex + 1));
            if (key.isBlank() || fields.putIfAbsent(key, value) != null) {
                throw new CustomerAuthenticationRequiredException();
            }
        }
        return fields;
    }

    private void verifyHash(Map<String, String> fields) {
        String suppliedHash = fields.remove("hash");
        if (suppliedHash == null || suppliedHash.length() != SHA_256_HEX_LENGTH) {
            throw new CustomerAuthenticationRequiredException();
        }

        byte[] suppliedHashBytes = HexFormat.of().parseHex(suppliedHash);
        String dataCheckString = fields.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + "\n" + right)
                .orElseThrow(CustomerAuthenticationRequiredException::new);

        byte[] secretKey = hmac(
                WEB_APP_DATA_KEY,
                properties.botToken().getBytes(StandardCharsets.UTF_8)
        );
        byte[] expectedHash = hmac(secretKey, dataCheckString.getBytes(StandardCharsets.UTF_8));
        if (!MessageDigest.isEqual(expectedHash, suppliedHashBytes)) {
            throw new CustomerAuthenticationRequiredException();
        }
    }

    private void verifyAuthDate(String authDateValue) {
        if (authDateValue == null) {
            throw new CustomerAuthenticationRequiredException();
        }

        Instant authDate = Instant.ofEpochSecond(Long.parseLong(authDateValue));
        Instant now = clock.instant();
        if (authDate.isBefore(now.minus(properties.initDataMaxAge()))
                || authDate.isAfter(now.plus(properties.initDataAllowedClockSkew()))) {
            throw new CustomerAuthenticationRequiredException();
        }
    }

    private TelegramPrincipal parsePrincipal(String userJson) {
        if (userJson == null || userJson.isBlank()) {
            throw new CustomerAuthenticationRequiredException();
        }

        JsonNode user = objectMapper.readTree(userJson);
        JsonNode id = user.get("id");
        JsonNode firstName = user.get("first_name");
        if (!user.isObject()
                || id == null
                || !id.isIntegralNumber()
                || !id.canConvertToLong()
                || id.longValue() <= 0
                || firstName == null
                || !firstName.isTextual()
                || firstName.textValue().isBlank()) {
            throw new CustomerAuthenticationRequiredException();
        }

        return new TelegramPrincipal(
                id.longValue(),
                optionalText(user, "username"),
                firstName.textValue(),
                optionalText(user, "last_name"),
                optionalText(user, "language_code")
        );
    }

    private static String optionalText(JsonNode object, String fieldName) {
        JsonNode value = object.get(fieldName);
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isTextual()) {
            throw new CustomerAuthenticationRequiredException();
        }
        return value.textValue().isBlank() ? null : value.textValue();
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static byte[] hmac(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
            return mac.doFinal(data);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("HmacSHA256 is not available", exception);
        }
    }
}
