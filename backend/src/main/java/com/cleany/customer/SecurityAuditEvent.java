package com.cleany.customer;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "security_audit_event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class SecurityAuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "customer_id") private Long customerId;
    @Column(name = "event_type", nullable = false, length = 64) private String eventType;
    @Enumerated(EnumType.STRING) @Column(name = "provider", length = 32)
    private ExternalIdentityProvider provider;
    @Column(name = "identity_id") private Long identityId;
    @Column(name = "session_id") private UUID sessionId;
    @Column(nullable = false, length = 32) private String outcome;
    @Column(name = "request_id", length = 128) private String requestId;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;

    SecurityAuditEvent(Long customerId, String eventType, ExternalIdentityProvider provider,
                       Long identityId, UUID sessionId, String outcome, String requestId,
                       Instant occurredAt) {
        this.customerId = customerId;
        this.eventType = eventType;
        this.provider = provider;
        this.identityId = identityId;
        this.sessionId = sessionId;
        this.outcome = outcome;
        this.requestId = requestId;
        this.occurredAt = occurredAt;
    }
}
