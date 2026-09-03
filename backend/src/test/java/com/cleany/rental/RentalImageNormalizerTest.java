package com.cleany.rental;

import java.awt.Color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RentalImageNormalizerTest {

    private final RentalImageNormalizer normalizer = new RentalImageNormalizer();

    @Test
    void largeJpeg_isResizedWithoutAspectRatioDistortion() {
        RentalImageVariants variants = normalizer.normalize(
                RentalTestImages.jpeg(2400, 1200, Color.BLUE)
        );
        var full = RentalTestImages.read(variants.full().content());
        var card = RentalTestImages.read(variants.card().content());
        var thumbnail = RentalTestImages.read(variants.thumbnail().content());

        Assertions.assertAll(
                () -> Assertions.assertEquals("image/jpeg", variants.full().contentType()),
                () -> Assertions.assertEquals(1600, full.getWidth()),
                () -> Assertions.assertEquals(800, full.getHeight()),
                () -> Assertions.assertEquals(960, card.getWidth()),
                () -> Assertions.assertEquals(480, card.getHeight()),
                () -> Assertions.assertEquals(320, thumbnail.getWidth()),
                () -> Assertions.assertEquals(160, thumbnail.getHeight())
        );
    }

    @Test
    void smallPng_isAcceptedConvertedAndNotUpscaled() {
        RentalImageVariants variants = normalizer.normalize(
                RentalTestImages.png(400, 300, Color.RED)
        );
        var full = RentalTestImages.read(variants.full().content());
        var card = RentalTestImages.read(variants.card().content());
        var thumbnail = RentalTestImages.read(variants.thumbnail().content());

        Assertions.assertAll(
                () -> Assertions.assertEquals("image/jpeg", variants.full().contentType()),
                () -> Assertions.assertEquals(400, full.getWidth()),
                () -> Assertions.assertEquals(300, full.getHeight()),
                () -> Assertions.assertEquals(400, card.getWidth()),
                () -> Assertions.assertEquals(300, card.getHeight()),
                () -> Assertions.assertEquals(320, thumbnail.getWidth()),
                () -> Assertions.assertEquals(240, thumbnail.getHeight())
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
