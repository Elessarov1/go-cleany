package com.cleany.transfer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferVehicleTypeRepository extends JpaRepository<TransferVehicleType, Long> {

    List<TransferVehicleType> findAllByOrderBySortOrderAscIdAsc();

    List<TransferVehicleType> findAllByEnabledTrueOrderBySortOrderAscIdAsc();

    Optional<TransferVehicleType> findByIdAndEnabledTrue(long id);
}
