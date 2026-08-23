package com.cleany.rental;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReorderRentalPropertyMediaRequest(
        @NotEmpty List<@NotNull @Positive Long> mediaIds
) {

    public ReorderRentalPropertyMediaRequest {
        mediaIds = mediaIds == null ? List.of() : List.copyOf(mediaIds);
    }
}
