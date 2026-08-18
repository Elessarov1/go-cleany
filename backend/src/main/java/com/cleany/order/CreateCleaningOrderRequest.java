package com.cleany.order;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCleaningOrderRequest(
        @NotNull ServiceArea area,
        @NotBlank @Size(max = 1000) String address,
        @NotNull ApartmentType apartmentType,
        boolean duplex,
        @NotNull CleaningType cleaningType,
        @NotNull LocalDate requestedDate,
        @NotBlank @Size(max = 40) String phone,
        @Size(max = 1000) String comment
) {

    CreateCleaningOrderCommand toCommand() {
        return new CreateCleaningOrderCommand(
                area,
                address,
                apartmentType,
                duplex,
                cleaningType,
                requestedDate,
                phone,
                comment
        );
    }
}

