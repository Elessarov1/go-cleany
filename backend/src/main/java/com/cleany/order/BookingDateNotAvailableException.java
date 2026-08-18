package com.cleany.order;

import java.time.LocalDate;

public class BookingDateNotAvailableException extends RuntimeException {

    public BookingDateNotAvailableException(LocalDate requestedDate, LocalDate earliest, LocalDate latest) {
        super("Requested date %s must be between %s and %s"
                .formatted(requestedDate, earliest, latest));
    }
}

