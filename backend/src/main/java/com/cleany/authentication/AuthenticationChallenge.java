package com.cleany.authentication;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.ExternalIdentityProvider;

@Entity
@Table(name = "authentication_challenge")
class AuthenticationChallenge {
    @Id private UUID id;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private AuthenticationChallengePurpose purpose;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32)
    private ExternalIdentityProvider provider;
    @Column(name = "customer_id") private Long customerId;
    @Column(name = "session_id") private UUID sessionId;
    @Column(name = "nonce_hash", nullable = false, length = 64) private String nonceHash;
    @Column(name = "verifier_hash", length = 64) private String verifierHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16)
    private AuthenticationChallengeStatus status;
    @Column(name = "proved_issuer") private String provedIssuer;
    @Column(name = "proved_subject") private String provedSubject;
    @Column(name = "proved_profile") private String provedProfile;
    @Column(name = "proved_credential_ciphertext") private String provedCredentialCiphertext;
    @Column(name = "proved_at") private Instant provedAt;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "expires_at", nullable = false) private Instant expiresAt;
    @Column(name = "consumed_at") private Instant consumedAt;

    protected AuthenticationChallenge() {
    }

    AuthenticationChallenge(UUID id, AuthenticationChallengePurpose purpose,
                            ExternalIdentityProvider provider, Long customerId, UUID sessionId,
                            String nonceHash, String verifierHash, Instant now, Instant expiresAt) {
        this.id = Objects.requireNonNull(id);
        this.purpose = Objects.requireNonNull(purpose);
        this.provider = Objects.requireNonNull(provider);
        this.customerId = customerId;
        this.sessionId = sessionId;
        this.nonceHash = Objects.requireNonNull(nonceHash);
        this.verifierHash = verifierHash;
        this.status = AuthenticationChallengeStatus.PENDING;
        this.createdAt = Objects.requireNonNull(now);
        this.expiresAt = Objects.requireNonNull(expiresAt);
    }

    void requireUsable(AuthenticationChallengePurpose expectedPurpose,
                       ExternalIdentityProvider expectedProvider, Instant now) {
        if (purpose != expectedPurpose || provider != expectedProvider) {
            throw new NativeAuthenticationException("auth_challenge_mismatch", "Authentication challenge does not match request");
        }
        if (status == AuthenticationChallengeStatus.CONSUMED) {
            throw new NativeAuthenticationException("auth_challenge_consumed", "Authentication challenge is already consumed");
        }
        if (!now.isBefore(expiresAt)) {
            throw new NativeAuthenticationException("auth_challenge_expired", "Authentication challenge is expired");
        }
    }

    void prove(AuthenticatedCustomerIdentity identity, String profile, Instant now) {
        requireUsable(purpose, provider, now);
        provedIssuer = identity.issuer();
        provedSubject = identity.externalSubject();
        provedProfile = profile;
        provedAt = now;
        status = AuthenticationChallengeStatus.PROVED;
    }

    void attachProvedCredential(String ciphertext) {
        if (status != AuthenticationChallengeStatus.PROVED) {
            throw new NativeAuthenticationException("auth_challenge_not_proved", "Authentication challenge is not proved");
        }
        provedCredentialCiphertext = Objects.requireNonNull(ciphertext);
    }

    void consume(Instant now) {
        if (status == AuthenticationChallengeStatus.CONSUMED) {
            throw new NativeAuthenticationException("auth_challenge_consumed", "Authentication challenge is already consumed");
        }
        status = AuthenticationChallengeStatus.CONSUMED;
        consumedAt = now;
    }

    UUID getId() { return id; }
    ExternalIdentityProvider getProvider() { return provider; }
    String getNonceHash() { return nonceHash; }
    String getVerifierHash() { return verifierHash; }
    Long getCustomerId() { return customerId; }
    UUID getSessionId() { return sessionId; }
    String getProvedIssuer() { return provedIssuer; }
    String getProvedSubject() { return provedSubject; }
    String getProvedProfile() { return provedProfile; }
    String getProvedCredentialCiphertext() { return provedCredentialCiphertext; }
    AuthenticationChallengeStatus getStatus() { return status; }
    Instant getExpiresAt() { return expiresAt; }
    Instant getCreatedAt() { return createdAt; }
    Instant getProvedAt() { return provedAt; }
}
