package com.cleany.communication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunicationEndpointRepository extends JpaRepository<CommunicationEndpoint, UUID> {
    Optional<CommunicationEndpoint> findByTypeAndAddressHash(CommunicationEndpointType type,
                                                             String addressHash);
    Optional<CommunicationEndpoint> findByIdAndCustomerId(UUID id, long customerId);
    List<CommunicationEndpoint> findAllByCustomerIdAndStatus(long customerId,
                                                             CommunicationEndpointStatus status);
    List<CommunicationEndpoint> findAllBySessionId(UUID sessionId);
    List<CommunicationEndpoint> findAllByCustomerId(long customerId);
}
