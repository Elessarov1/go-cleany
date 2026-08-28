package com.cleany.configuration;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AdminPropertiesTest {

    @Test
    void googleEmails_areNormalizedAndDeduplicated() {
        AdminProperties properties = new AdminProperties(List.of(
                " Admin@Example.com ",
                "admin@example.com",
                "second@example.com"
        ));

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        List.of("admin@example.com", "second@example.com"),
                        properties.googleEmails()
                ),
                () -> Assertions.assertTrue(properties.containsGoogleEmail("ADMIN@example.com")),
                () -> Assertions.assertFalse(properties.containsGoogleEmail("other@example.com"))
        );
    }
}
