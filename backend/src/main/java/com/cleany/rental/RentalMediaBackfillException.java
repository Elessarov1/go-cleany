package com.cleany.rental;

public class RentalMediaBackfillException extends IllegalStateException {

    public RentalMediaBackfillException(
            long propertyId,
            long mediaId,
            Throwable cause
    ) {
        super(
                "Rental media backfill failed for property " + propertyId + ", media " + mediaId,
                cause
        );
    }
}
