package com.cleany.transfer;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferAirportRepository extends JpaRepository<TransferAirport, Long> {

    List<TransferAirport> findAllByOrderBySortOrderAscIdAsc();

    List<TransferAirport> findAllByEnabledTrueOrderBySortOrderAscIdAsc();

    Optional<TransferAirport> findByIdAndEnabledTrue(long id);
}
