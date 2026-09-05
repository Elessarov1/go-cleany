package com.cleany.rental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReorderRentalPropertiesRequest(
        @NotNull List<@Positive Long> propertyIds
) {

    public ReorderRentalPropertiesRequest {
        propertyIds = propertyIds == null
                ? null
                : Collections.unmodifiableList(new ArrayList<>(propertyIds));
    }
}
