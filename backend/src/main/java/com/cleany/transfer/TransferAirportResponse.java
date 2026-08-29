package com.cleany.transfer;

public record TransferAirportResponse(
        long id,
        String code,
        String nameRu,
        String nameEn
) {

    static TransferAirportResponse from(TransferAirport airport) {
        return new TransferAirportResponse(
                airport.getId(),
                airport.getCode(),
                airport.getNameRu(),
                airport.getNameEn()
        );
    }
}
