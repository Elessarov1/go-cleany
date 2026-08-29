package com.cleany.transfer;

public record TransferVehicleTypeResponse(
        long id,
        String code,
        String nameRu,
        String nameEn,
        int maxPassengers,
        int maxLuggage
) {

    static TransferVehicleTypeResponse from(TransferVehicleType vehicle) {
        return new TransferVehicleTypeResponse(
                vehicle.getId(),
                vehicle.getCode(),
                vehicle.getNameRu(),
                vehicle.getNameEn(),
                vehicle.getMaxPassengers(),
                vehicle.getMaxLuggage()
        );
    }
}
