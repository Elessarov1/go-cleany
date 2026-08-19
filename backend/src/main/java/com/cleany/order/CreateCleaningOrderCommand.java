package com.cleany.order;

import java.time.LocalDate;

public record CreateCleaningOrderCommand(
        ServiceArea area,
        String address,
        ApartmentType apartmentType,
        boolean duplex,
        CleaningType cleaningType,
        LocalDate requestedDate,
        String phone,
        String comment,
        String referralCode
) {
}
