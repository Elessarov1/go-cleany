package com.cleany.referral;

import java.math.BigDecimal;
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
@Table(name = "partner_payout")
public class PartnerPayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partner_id", nullable = false)
    private long partnerId;

    @Column(name = "source_order_id", nullable = false)
    private long sourceOrderId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private PartnerPayoutStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    protected PartnerPayout() {
    }

    PartnerPayout(long partnerId, long sourceOrderId, BigDecimal amount, String currency, Instant createdAt) {
        this.partnerId = partnerId;
        this.sourceOrderId = sourceOrderId;
        this.amount = Objects.requireNonNull(amount);
        this.currency = Objects.requireNonNull(currency);
        this.status = PartnerPayoutStatus.PAYABLE;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    void markPaid(Instant at) {
        if (status != PartnerPayoutStatus.PAYABLE) {
            throw new ReferralNotApplicableException("Partner payout is already paid");
        }
        status = PartnerPayoutStatus.PAID;
        paidAt = Objects.requireNonNull(at);
    }

    public Long getId() {
        return id;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public long getSourceOrderId() {
        return sourceOrderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PartnerPayoutStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPaidAt() {
        return paidAt;
    }
}
