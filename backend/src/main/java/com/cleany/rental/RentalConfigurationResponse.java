package com.cleany.rental;

import java.math.BigDecimal;

public record RentalConfigurationResponse(
        int minStayDays,
        int longTermMinDays,
        BigDecimal longTermDiscountRate,
        int maxStayDays,
        int bookingStartMonthsAhead,
        int maxActiveBookingsPerCustomer
) {

    static RentalConfigurationResponse from(RentalProperties properties) {
        return new RentalConfigurationResponse(
                properties.minStayDays(),
                properties.longTermMinDays(),
                properties.longTermDiscountRate(),
                properties.maxStayDays(),
                properties.bookingStartMonthsAhead(),
                properties.maxActiveBookingsPerCustomer()
        );
    }
}
