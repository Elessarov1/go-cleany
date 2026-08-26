package com.cleany.customer;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_external_identity")
public class CustomerExternalIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 32)
    private ExternalIdentityProvider provider;

    @Column(name = "external_subject", nullable = false, length = 128)
    private String externalSubject;

    @Column(name = "username", length = 128)
    private String username;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(name = "language_code", length = 16)
    private String languageCode;

    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    protected CustomerExternalIdentity() {
    }

    CustomerExternalIdentity(
            long customerId,
            ExternalIdentityProvider provider,
            String externalSubject,
            String username,
            String displayName,
            String languageCode,
            Instant lastSeenAt
    ) {
        this(
                customerId,
                provider,
                externalSubject,
                username,
                displayName,
                languageCode,
                null,
                false,
                lastSeenAt
        );
    }

    CustomerExternalIdentity(
            long customerId,
            ExternalIdentityProvider provider,
            String externalSubject,
            String username,
            String displayName,
            String languageCode,
            String email,
            boolean emailVerified,
            Instant lastSeenAt
    ) {
        this.customerId = customerId;
        this.provider = Objects.requireNonNull(provider);
        this.externalSubject = Objects.requireNonNull(externalSubject);
        this.username = username;
        this.displayName = Objects.requireNonNull(displayName);
        this.languageCode = languageCode;
        this.email = email;
        this.emailVerified = email != null && emailVerified;
        this.lastSeenAt = Objects.requireNonNull(lastSeenAt);
    }

    void refresh(String username, String displayName, String languageCode, Instant seenAt) {
        refresh(username, displayName, languageCode, null, false, seenAt);
    }

    void refresh(
            String username,
            String displayName,
            String languageCode,
            String email,
            boolean emailVerified,
            Instant seenAt
    ) {
        this.username = username;
        this.displayName = Objects.requireNonNull(displayName);
        this.languageCode = languageCode;
        this.email = email;
        this.emailVerified = email != null && emailVerified;
        this.lastSeenAt = Objects.requireNonNull(seenAt);
    }

    public long getCustomerId() {
        return customerId;
    }

    public Long getId() {
        return id;
    }

    public ExternalIdentityProvider getProvider() {
        return provider;
    }

    public String getExternalSubject() {
        return externalSubject;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}
