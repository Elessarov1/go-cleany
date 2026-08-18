package com.cleany.telegram;

import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import com.cleany.configuration.TelegramProperties;
import com.cleany.configuration.TelegramUpdateMode;

class TelegramInitDataValidatorTest {

    private static final String BOT_TOKEN = "123456789:test-token";
    private static final Instant NOW = Instant.parse("2026-08-17T09:00:00Z");
    private static final String USER_JSON = """
            {"id":900001,"first_name":"Alex","last_name":"Cleaner","username":"alex"}
            """.strip();
    private static final String FIXED_VALID_INIT_DATA = "query_id=AAExampleQuery"
            + "&user=%7B%22id%22%3A900001%2C%22first_name%22%3A%22Alex%22%2C%22last_name%22%3A%22Cleaner%22%2C%22username%22%3A%22alex%22%7D"
            + "&auth_date=1786957200"
            + "&hash=099237af29b4dfecaabdcab4792c35f4a839c5b46e685730eb4d64faeef39d1e";

    private final TelegramInitDataValidator validator = new TelegramInitDataValidator(
            new TelegramProperties(
                    BOT_TOKEN,
                    "test-secret",
                    Duration.ofHours(1),
                    Duration.ofSeconds(30),
                    false,
                    URI.create("https://api.telegram.org"),
                    TelegramUpdateMode.POLLING,
                    25,
                    Duration.ofSeconds(3),
                    false
            ),
            new ObjectMapper(),
            Clock.fixed(NOW, ZoneOffset.UTC)
    );

    @Test
    void signedFreshInitData_trustedPrincipalReturned() {
        TelegramPrincipal principal = validator.validate(FIXED_VALID_INIT_DATA);

        Assertions.assertEquals(900001L, principal.id());
        Assertions.assertEquals("alex", principal.username());
        Assertions.assertEquals("Alex", principal.firstName());
        Assertions.assertEquals("Cleaner", principal.lastName());
    }

    @Test
    void signedDataChangedAfterSigning_authenticationRejected() {
        String tampered = FIXED_VALID_INIT_DATA.replace("Alex", "Mallory");

        Assertions.assertThrows(
                CustomerAuthenticationRequiredException.class,
                () -> validator.validate(tampered)
        );
    }

    @Test
    void correctlySignedButExpiredData_authenticationRejected() {
        String expired = TelegramInitDataTestFactory.signed(
                BOT_TOKEN,
                NOW.minus(Duration.ofHours(1).plusSeconds(1)),
                USER_JSON
        );

        Assertions.assertThrows(
                CustomerAuthenticationRequiredException.class,
                () -> validator.validate(expired)
        );
    }

    @Test
    void correctlySignedDataTooFarInFuture_authenticationRejected() {
        String fromFuture = TelegramInitDataTestFactory.signed(
                BOT_TOKEN,
                NOW.plusSeconds(31),
                USER_JSON
        );

        Assertions.assertThrows(
                CustomerAuthenticationRequiredException.class,
                () -> validator.validate(fromFuture)
        );
    }

    @Test
    void duplicateQueryParameter_authenticationRejected() {
        String duplicate = FIXED_VALID_INIT_DATA + "&auth_date=1786957200";

        Assertions.assertThrows(
                CustomerAuthenticationRequiredException.class,
                () -> validator.validate(duplicate)
        );
    }
}
