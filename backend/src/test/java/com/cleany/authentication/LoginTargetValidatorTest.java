package com.cleany.authentication;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginTargetValidatorTest {

    private final LoginTargetValidator validator = new LoginTargetValidator();

    @Test
    void acceptsOnlyKnownLocalApplicationRoutes() {
        assertThat(validator.normalize("/cleaning?ref=GC123"))
                .isEqualTo("/cleaning?ref=GC123");
        assertThat(validator.normalize("/rent/properties/sea-view?from=catalog"))
                .isEqualTo("/rent/properties/sea-view?from=catalog");
        assertThat(validator.normalize("/admin/rent/bookings/42"))
                .isEqualTo("/admin/rent/bookings/42");
    }

    @Test
    void rejectsExternalProtocolRelativeEncodedAndUnknownTargets() {
        assertThat(validator.normalize("https://attacker.example/admin")).isEqualTo("/");
        assertThat(validator.normalize("//attacker.example/admin")).isEqualTo("/");
        assertThat(validator.normalize("/%2Fattacker.example")).isEqualTo("/");
        assertThat(validator.normalize("/api/v1/customer/profile")).isEqualTo("/");
        assertThat(validator.normalize("/cleaning\\..\\admin")).isEqualTo("/");
        assertThat(validator.normalize("/rent%0d%0aLocation:%20https://evil.test"))
                .isEqualTo("/");
    }
}
