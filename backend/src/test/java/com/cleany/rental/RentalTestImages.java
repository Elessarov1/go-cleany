package com.cleany.rental;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

final class RentalTestImages {

    private RentalTestImages() {
    }

    static byte[] jpeg(int width, int height, Color color) {
        return image(width, height, color, "jpeg", BufferedImage.TYPE_INT_RGB);
    }

    static byte[] png(int width, int height, Color color) {
        return image(width, height, color, "png", BufferedImage.TYPE_INT_ARGB);
    }

    static BufferedImage read(byte[] content) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(content));
            if (image == null) {
                throw new IllegalStateException("Test image could not be decoded");
            }
            return image;
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static byte[] image(
            int width,
            int height,
            Color color,
            String format,
            int imageType
    ) {
        BufferedImage image = new BufferedImage(width, height, imageType);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setColor(color);
            graphics.fillRect(0, 0, width, height);
        } finally {
            graphics.dispose();
        }
        try (var output = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, format, output)) {
                throw new IllegalStateException("No test image writer for " + format);
            }
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
