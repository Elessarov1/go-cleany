package com.cleany.rental;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.stereotype.Component;

import com.cleany.media.ImageMediaTypeDetector;
import com.cleany.media.MediaUpload;

import net.coobird.thumbnailator.Thumbnails;

@Component
class RentalImageNormalizer {

    static final int FULL_MAX_LONG_SIDE = 1600;
    static final int CARD_MAX_LONG_SIDE = 960;
    static final int THUMBNAIL_MAX_LONG_SIDE = 320;
    private static final long MAX_DECODED_PIXELS = 40_000_000L;
    private static final float FULL_JPEG_QUALITY = 0.82F;
    private static final float CARD_JPEG_QUALITY = 0.78F;
    private static final float THUMBNAIL_JPEG_QUALITY = 0.72F;
    private static final String CANONICAL_CONTENT_TYPE = "image/jpeg";

    RentalImageVariants normalize(byte[] content) {
        String detectedType = ImageMediaTypeDetector.detect(content)
                .filter(type -> "image/jpeg".equals(type) || "image/png".equals(type))
                .orElseThrow(() -> invalid("Property image must be JPEG or PNG"));
        Dimensions dimensions = validateDimensions(content, detectedType);
        try {
            BufferedImage normalized = resizeAndOrient(content, dimensions);
            BufferedImage full = onWhiteBackground(normalized);
            BufferedImage card = resize(full, CARD_MAX_LONG_SIDE);
            BufferedImage thumbnail = resize(full, THUMBNAIL_MAX_LONG_SIDE);
            return new RentalImageVariants(
                    upload(full, FULL_JPEG_QUALITY),
                    upload(card, CARD_JPEG_QUALITY),
                    upload(thumbnail, THUMBNAIL_JPEG_QUALITY)
            );
        } catch (IOException | RuntimeException exception) {
            if (exception instanceof InvalidRentalPropertyMediaException invalid) {
                throw invalid;
            }
            throw invalid("Property image cannot be decoded", exception);
        }
    }

    private static Dimensions validateDimensions(byte[] content, String detectedType) {
        try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(content))) {
            if (input == null) {
                throw invalid("Property image cannot be decoded");
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw invalid("Property image cannot be decoded");
            }
            ImageReader reader = readers.next();
            try {
                String format = reader.getFormatName().toLowerCase(java.util.Locale.ROOT);
                boolean formatMatches = "image/jpeg".equals(detectedType)
                        ? format.contains("jpeg") || format.contains("jpg")
                        : format.contains("png");
                if (!formatMatches) {
                    throw invalid("Property image content does not match its signature");
                }
                reader.setInput(input, true, true);
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);
                if (width <= 0 || height <= 0
                        || (long) width * height > MAX_DECODED_PIXELS) {
                    throw invalid("Property image dimensions are not supported");
                }
                return new Dimensions(width, height);
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw invalid("Property image cannot be decoded");
        }
    }

    private static BufferedImage resizeAndOrient(
            byte[] content,
            Dimensions dimensions
    ) throws IOException {
        var builder = Thumbnails.of(new ByteArrayInputStream(content))
                .useExifOrientation(true);
        if (Math.max(dimensions.width(), dimensions.height()) > FULL_MAX_LONG_SIDE) {
            builder.size(FULL_MAX_LONG_SIDE, FULL_MAX_LONG_SIDE).keepAspectRatio(true);
        } else {
            builder.scale(1.0);
        }
        return builder.asBufferedImage();
    }

    private static BufferedImage resize(BufferedImage source, int maxLongSide) throws IOException {
        if (Math.max(source.getWidth(), source.getHeight()) <= maxLongSide) {
            return source;
        }
        return Thumbnails.of(source)
                .size(maxLongSide, maxLongSide)
                .keepAspectRatio(true)
                .asBufferedImage();
    }

    private static MediaUpload upload(BufferedImage image, float quality) throws IOException {
        return new MediaUpload(writeJpeg(image, quality), CANONICAL_CONTENT_TYPE);
    }

    private static BufferedImage onWhiteBackground(BufferedImage source) {
        BufferedImage rgb = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D graphics = rgb.createGraphics();
        try {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
            graphics.drawImage(source, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return rgb;
    }

    private static byte[] writeJpeg(BufferedImage image, float quality) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        try (var output = new ByteArrayOutputStream();
             ImageOutputStream imageOutput = ImageIO.createImageOutputStream(output)) {
            writer.setOutput(imageOutput);
            ImageWriteParam parameters = writer.getDefaultWriteParam();
            parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            parameters.setCompressionQuality(quality);
            writer.write(null, new IIOImage(image, null, null), parameters);
            imageOutput.flush();
            return output.toByteArray();
        } finally {
            writer.dispose();
        }
    }

    private static InvalidRentalPropertyMediaException invalid(String message) {
        return new InvalidRentalPropertyMediaException(message);
    }

    private static InvalidRentalPropertyMediaException invalid(
            String message,
            Throwable cause
    ) {
        return new InvalidRentalPropertyMediaException(message, cause);
    }

    private record Dimensions(int width, int height) {
    }
}
