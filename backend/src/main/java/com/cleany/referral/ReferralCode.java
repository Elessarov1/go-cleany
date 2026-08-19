package com.cleany.referral;

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
@Table(name = "referral_code")
public class ReferralCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 32)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 16)
    private ReferralCodeOwnerType ownerType;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ReferralCode() {
    }

    private ReferralCode(
            String code,
            ReferralCodeOwnerType ownerType,
            Long customerId,
            Long partnerId,
            Instant createdAt
    ) {
        this.code = Objects.requireNonNull(code);
        this.ownerType = Objects.requireNonNull(ownerType);
        this.customerId = customerId;
        this.partnerId = partnerId;
        this.active = true;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    static ReferralCode customer(String code, long customerId, Instant createdAt) {
        return new ReferralCode(code, ReferralCodeOwnerType.CUSTOMER, customerId, null, createdAt);
    }

    static ReferralCode partner(String code, long partnerId, Instant createdAt) {
        return new ReferralCode(code, ReferralCodeOwnerType.PARTNER, null, partnerId, createdAt);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public ReferralCodeOwnerType getOwnerType() {
        return ownerType;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getPartnerId() {
        return partnerId;
    }
}
