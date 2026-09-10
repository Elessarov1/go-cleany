package com.cleany.communication;

import java.util.UUID;

public record CommunicationEndpointResponse(UUID id, CommunicationEndpointType type,
                                            CommunicationPlatform platform, String installationId,
                                            boolean active) {
    static CommunicationEndpointResponse from(CommunicationEndpoint endpoint) {
        return new CommunicationEndpointResponse(endpoint.getId(), endpoint.getType(),
                endpoint.getPlatform(), endpoint.getInstallationId(), endpoint.active());
    }
}
