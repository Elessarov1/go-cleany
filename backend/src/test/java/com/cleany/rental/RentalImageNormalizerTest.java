package com.cleany.rental;

import java.awt.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RentalImageNormalizerTest {

    private final RentalImageNormalizer normalizer = new RentalImageNormalizer();

    @Test
    void largeJpeg_isResizedWithoutAspectRatioDistortion() {
        var upload = normalizer.normalize(RentalTestImages.jpeg(2400, 1200, Color.BLUE));
        var image = RentalTestImages.read(upload.content());

        Assertions.assertAll(
                () -> Assertions.assertEquals("image/jpeg", upload.contentType()),
                () -> Assertions.assertEquals(1920, image.getWidth()),
                () -> Assertions.assertEquals(960, image.getHeight())
        );
    }

    @Test
    void smallPng_isAcceptedConvertedAndNotUpscaled() {
        var upload = normalizer.normalize(RentalTestImages.png(400, 300, Color.RED));
        var image = RentalTestImages.read(upload.content());

        Assertions.assertAll(
                () -> Assertions.assertEquals("image/jpeg", upload.contentType()),
                () -> Assertions.assertEquals(400, image.getWidth()),
                () -> Assertions.assertEquals(300, image.getHeight())
        );
    }

    @Test
    void invalidBytes_areRejectedEvenWhenTheyStartLikeJpeg() {
        byte[] invalid = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x01};

        Assertions.assertThrows(
                InvalidRentalPropertyMediaException.class,
                () -> normalizer.normalize(invalid)
        );
    }
}
