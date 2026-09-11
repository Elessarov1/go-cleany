package com.cleany.rental;

import java.util.UUID;

record RentalSearchCursor(
        UUID searchExecutionId,
        String criteriaFingerprint,
        int displayOrder,
        long propertyId
) {
}
