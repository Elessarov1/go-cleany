package com.cleany.rental;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.cleany.pagination.InvalidCursorException;

@Component
class RentalSearchCursorCodec {

    private static final String VERSION = "1";
    private static final String SEPARATOR = ":";

    String criteriaFingerprint(RentalSearchCriteriaResponse criteria) {
        String value = String.join(
                SEPARATOR,
                criteria.mode().name(),
                string(criteria.termType()),
                string(criteria.checkInDate()),
                string(criteria.checkOutDate()),
                string(criteria.rentalMonths()),
                string(criteria.durationDays()),
                string(criteria.guests())
        );
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    String encode(RentalSearchCursor cursor) {
        String value = String.join(
                SEPARATOR,
                VERSION,
                cursor.searchExecutionId().toString(),
                cursor.criteriaFingerprint(),
                Integer.toString(cursor.displayOrder()),
                Long.toString(cursor.propertyId())
        );
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    RentalSearchCursor decode(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            throw new InvalidCursorException();
        }
        try {
            String value = new String(
                    Base64.getUrlDecoder().decode(encoded),
                    StandardCharsets.UTF_8
            );
            String[] parts = value.split(SEPARATOR, -1);
            if (parts.length != 5 || !VERSION.equals(parts[0])) {
                throw new InvalidCursorException();
            }
            RentalSearchCursor cursor = new RentalSearchCursor(
                    UUID.fromString(parts[1]),
                    parts[2],
                    Integer.parseInt(parts[3]),
                    Long.parseLong(parts[4])
            );
            if (cursor.criteriaFingerprint().length() != 64
                    || cursor.displayOrder() < 0
                    || cursor.propertyId() <= 0) {
                throw new InvalidCursorException();
            }
            return cursor;
        } catch (IllegalArgumentException exception) {
            throw new InvalidCursorException();
        }
    }

    private static String string(Object value) {
        return value == null ? "" : value.toString();
    }
}
