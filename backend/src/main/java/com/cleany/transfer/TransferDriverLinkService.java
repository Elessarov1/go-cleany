package com.cleany.transfer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.cleany.admin.AdminAccessService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferDriverLinkService {

    private static final String START_PREFIX = "driver_";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AdminAccessService adminAccessService;
    private final TransferDriverRepository driverRepository;
    private final TransferDriverLinkTokenRepository tokenRepository;
    private final TransferDriverLinkProperties properties;
    private final Clock clock;

    @Transactional
    public TransferDriverLinkResponse createLink(long driverId) {
        adminAccessService.requireCurrentAdmin();
        TransferDriver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new TransferConfigurationNotFoundException("driver", driverId));
        if (driver.getConfiguredTelegramUserId() == null) {
            throw new TransferDriverLinkException("Configure the driver's Telegram ID first");
        }

        var now = clock.instant();
        tokenRepository.consumeOutstanding(driverId, now);
        String rawToken = newToken();
        var expiresAt = now.plus(properties.tokenTtl());
        tokenRepository.save(new TransferDriverLinkToken(hash(rawToken), driver, now, expiresAt));
        String url = UriComponentsBuilder.fromUriString("https://t.me/" + properties.botUsername())
                .queryParam("start", START_PREFIX + rawToken)
                .build(true)
                .toUriString();
        return new TransferDriverLinkResponse(url, expiresAt);
    }

    @Transactional
    public TransferDriver authorize(String rawToken, long telegramUserId, long telegramChatId) {
        TransferDriverLinkToken token = tokenRepository.findByTokenHashForUpdate(hash(rawToken))
                .orElseThrow(() -> new TransferDriverLinkException("Driver link is invalid"));
        var now = clock.instant();
        if (token.getConsumedAt() != null) {
            throw new TransferDriverLinkException("Driver link has already been used");
        }
        if (token.isExpired(now)) {
            throw new TransferDriverLinkException("Driver link has expired");
        }
        TransferDriver driver = token.getDriver();
        driver.authorizeTelegram(telegramUserId, telegramChatId, now);
        token.consume(now);
        return driver;
    }

    public static String extractToken(String startParameter) {
        if (startParameter == null || !startParameter.startsWith(START_PREFIX)) {
            return null;
        }
        String token = startParameter.substring(START_PREFIX.length());
        return token.matches("[A-Za-z0-9_-]{43}") ? token : null;
    }

    private static String newToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new TransferDriverLinkException("Driver link is invalid");
        }
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(rawToken.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
