package com.cleany.rental;

import java.util.Objects;

import com.cleany.media.MediaUpload;

record RentalImageVariants(
        MediaUpload full,
        MediaUpload card,
        MediaUpload thumbnail
) {

    RentalImageVariants {
        full = Objects.requireNonNull(full, "full");
        card = Objects.requireNonNull(card, "card");
        thumbnail = Objects.requireNonNull(thumbnail, "thumbnail");
    }
}
