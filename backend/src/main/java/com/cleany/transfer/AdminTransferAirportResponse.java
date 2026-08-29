package com.cleany.transfer;

import java.time.Instant;

public record AdminTransferAirportResponse(
        long id,
        String code,
        String nameRu,
        String nameEn,
        boolean enabled,
        int sortOrder,
        Instant createdAt,
        Instant updatedAt,
        long version
) {

    static AdminTransferAirportResponse from(TransferAirport airport) {
        return new AdminTransferAirportResponse(
                airport.getId(), airport.getCode(), airport.getNameRu(), airport.getNameEn(),
                airport.isEnabled(), airport.getSortOrder(), airport.getCreatedAt(),
                airport.getUpdatedAt(), airport.getVersion()
        );
    }
}
