package com.cleany.common.text;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

public final class AddressNormalizer {

    private AddressNormalizer() {
    }

    public static String normalize(String value) {
        return Normalizer.normalize(Objects.requireNonNull(value, "value").strip(), Normalizer.Form.NFKC)
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
