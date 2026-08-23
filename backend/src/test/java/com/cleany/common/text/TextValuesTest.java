package com.cleany.common.text;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TextValuesTest {

    @Test
    void optionalAndRequiredValues_areTrimmedWithStableValidation() {
        Assertions.assertAll(
                () -> Assertions.assertNull(TextValues.normalizeOptional("  ")),
                () -> Assertions.assertEquals("value", TextValues.normalizeOptional(" value ")),
                () -> Assertions.assertEquals(
                        "value",
                        TextValues.requireNonBlank(
                                " value ",
                                5,
                                IllegalArgumentException::new
                        )
                ),
                () -> Assertions.assertEquals(
                        "must not be blank",
                        Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> TextValues.requireNonBlank(
                                        " ",
                                        5,
                                        IllegalArgumentException::new
                                )
                        ).getMessage()
                ),
                () -> Assertions.assertEquals(
                        "is too long",
                        Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> TextValues.requireNonBlank(
                                        "value!",
                                        5,
                                        IllegalArgumentException::new
                                )
                        ).getMessage()
                )
        );
    }
}
