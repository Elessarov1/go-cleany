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
@Table(name = "referral_reward")
public class ReferralReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Column(name = "source_order_id", nullable = false)
    private long sourceOrderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private ReferralRewardStatus status;

    @Column(name = "reserved_order_id")
    private Long reservedOrderId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "redeemed_at")
    private Instant redeemedAt;

    protected ReferralReward() {
    }

    ReferralReward(long customerId, long sourceOrderId, Instant createdAt) {
        this.customerId = customerId;
        this.sourceOrderId = sourceOrderId;
        this.status = ReferralRewardStatus.AVAILABLE;
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    void reserve(long orderId) {
        if (status != ReferralRewardStatus.AVAILABLE) {
            throw new ReferralNotApplicableException("Referral reward is no longer available");
        }
        status = ReferralRewardStatus.RESERVED;
        reservedOrderId = orderId;
    }

    void release(long orderId) {
        if (status == ReferralRewardStatus.RESERVED && Objects.equals(reservedOrderId, orderId)) {
            status = ReferralRewardStatus.AVAILABLE;
            reservedOrderId = null;
        }
    }

    void redeem(long orderId, Instant at) {
        if (status != ReferralRewardStatus.RESERVED || !Objects.equals(reservedOrderId, orderId)) {
            throw new ReferralNotApplicableException("Referral reward is not reserved for this order");
        }
        status = ReferralRewardStatus.REDEEMED;
        reservedOrderId = null;
        redeemedAt = Objects.requireNonNull(at);
    }

    public Long getId() {
        return id;
    }
}
