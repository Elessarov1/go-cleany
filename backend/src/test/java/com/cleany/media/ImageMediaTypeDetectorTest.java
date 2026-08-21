package com.cleany.media;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ImageMediaTypeDetectorTest {

    @Test
    void supportedSignatures_detectedWithoutTrustingExternalMetadata() {
        byte[] jpeg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00};
        byte[] png = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        "image/jpeg",
                        ImageMediaTypeDetector.detect(jpeg).orElseThrow()
                ),
                () -> Assertions.assertEquals(
                        "image/png",
                        ImageMediaTypeDetector.detect(png).orElseThrow()
                ),
                () -> Assertions.assertTrue(
                        ImageMediaTypeDetector.detect(new byte[]{1, 2, 3}).isEmpty()
                ),
                () -> Assertions.assertTrue(ImageMediaTypeDetector.detect(null).isEmpty())
        );
    }
}
