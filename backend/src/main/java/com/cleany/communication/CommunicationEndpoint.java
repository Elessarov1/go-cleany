package com.cleany.communication;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "communication_endpoint")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunicationEndpoint {
    @Id private UUID id;
    @Column(name = "customer_id", nullable = false) private long customerId;
    @Column(name = "session_id") private UUID sessionId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private CommunicationEndpointType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private CommunicationPlatform platform;
    @Column(name = "address_hash", nullable = false, length = 64) private String addressHash;
    @Column(name = "address_ciphertext", nullable = false) private String addressCiphertext;
    @Column(name = "installation_id", length = 128) private String installationId;
    @Column(length = 16) private String locale;
    @Column(name = "app_version", length = 32) private String appVersion;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private CommunicationEndpointStatus status;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Column(name = "disabled_at") private Instant disabledAt;

    CommunicationEndpoint(UUID id, long customerId, UUID sessionId, CommunicationEndpointType type,
                          CommunicationPlatform platform, String addressHash, String addressCiphertext,
                          String installationId, String locale, String appVersion, Instant now) {
        this.id = id;
        this.customerId = customerId;
        this.sessionId = sessionId;
        this.type = type;
        this.platform = platform;
        this.addressHash = addressHash;
        this.addressCiphertext = addressCiphertext;
        this.installationId = installationId;
        this.locale = locale;
        this.appVersion = appVersion;
        this.status = CommunicationEndpointStatus.ACTIVE;
        this.createdAt = now;
        this.updatedAt = now;
    }

    void reactivate(long customerId, UUID sessionId, String ciphertext, String installationId,
                    String locale, String appVersion, Instant now) {
        this.customerId = customerId;
        this.sessionId = sessionId;
        this.addressCiphertext = ciphertext;
        this.installationId = installationId;
        this.locale = locale;
        this.appVersion = appVersion;
        this.status = CommunicationEndpointStatus.ACTIVE;
        this.disabledAt = null;
        this.updatedAt = now;
    }

    public void disable(Instant now) {
        if (status != CommunicationEndpointStatus.DISABLED) {
            status = CommunicationEndpointStatus.DISABLED;
            disabledAt = now;
            updatedAt = now;
        }
    }

    public boolean active() {
        return status == CommunicationEndpointStatus.ACTIVE;
    }
}
