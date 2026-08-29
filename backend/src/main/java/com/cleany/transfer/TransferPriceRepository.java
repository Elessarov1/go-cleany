package com.cleany.transfer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferPriceRepository extends JpaRepository<TransferPrice, Long> {

    @EntityGraph(attributePaths = {"airport", "vehicleType"})
    Optional<TransferPrice> findByAirport_IdAndVehicleType_IdAndDirectionAndEnabledTrue(
            long airportId,
            long vehicleTypeId,
            TransferDirection direction
    );

    @EntityGraph(attributePaths = {"airport", "vehicleType"})
    Optional<TransferPrice> findByAirport_IdAndVehicleType_IdAndDirection(
            long airportId,
            long vehicleTypeId,
            TransferDirection direction
    );

    @EntityGraph(attributePaths = {"airport", "vehicleType"})
    List<TransferPrice> findAllByOrderByAirport_SortOrderAscVehicleType_SortOrderAscDirectionAsc();

    @EntityGraph(attributePaths = {"airport", "vehicleType"})
    List<TransferPrice> findAllByEnabledTrueAndAirport_EnabledTrueAndVehicleType_EnabledTrue();
}
