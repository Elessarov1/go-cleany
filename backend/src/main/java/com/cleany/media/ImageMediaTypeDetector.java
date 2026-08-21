package com.cleany.media;

import java.util.Optional;

public final class ImageMediaTypeDetector {

    private static final byte[] PNG_SIGNATURE = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    private ImageMediaTypeDetector() {
    }

    public static Optional<String> detect(byte[] content) {
        if (content == null) {
            return Optional.empty();
        }
        if (content.length >= 3
                && Byte.toUnsignedInt(content[0]) == 0xFF
                && Byte.toUnsignedInt(content[1]) == 0xD8
                && Byte.toUnsignedInt(content[2]) == 0xFF) {
            return Optional.of("image/jpeg");
        }
        if (content.length >= PNG_SIGNATURE.length) {
            for (int index = 0; index < PNG_SIGNATURE.length; index++) {
                if (content[index] != PNG_SIGNATURE[index]) {
                    return Optional.empty();
                }
            }
            return Optional.of("image/png");
        }
        return Optional.empty();
    }
}
