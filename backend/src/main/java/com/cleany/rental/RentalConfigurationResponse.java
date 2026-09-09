package com.cleany.rental;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalConfigurationResponse(
        int minStayDays,
        int longTermMinDays,
        BigDecimal longTermDiscountRate,
        int maxStayDays,
        int bookingStartMonthsAhead,
        int maxActiveBookingsPerCustomer,
        LocalDate today,
        LocalDate latestCheckInDate
) {

    static RentalConfigurationResponse from(RentalProperties properties, LocalDate today) {
        return new RentalConfigurationResponse(
                properties.minStayDays(),
                properties.longTermMinDays(),
                properties.longTermDiscountRate(),
                properties.maxStayDays(),
                properties.bookingStartMonthsAhead(),
                properties.maxActiveBookingsPerCustomer(),
                today,
                today.plusMonths(properties.bookingStartMonthsAhead())
        );
    }
}
