package com.cleany.transfer;

import java.time.Instant;

public record AdminTransferVehicleTypeResponse(
        long id,
        String code,
        String nameRu,
        String nameEn,
        int maxPassengers,
        int maxLuggage,
        boolean enabled,
        int sortOrder,
        Instant createdAt,
        Instant updatedAt,
        long version
) {

    static AdminTransferVehicleTypeResponse from(TransferVehicleType vehicle) {
        return new AdminTransferVehicleTypeResponse(
                vehicle.getId(), vehicle.getCode(), vehicle.getNameRu(), vehicle.getNameEn(),
                vehicle.getMaxPassengers(), vehicle.getMaxLuggage(), vehicle.isEnabled(),
                vehicle.getSortOrder(), vehicle.getCreatedAt(), vehicle.getUpdatedAt(), vehicle.getVersion()
        );
    }
}
