package com.cleany.crossservice.rentalcleaning;

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
import jakarta.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rental_cleaning_benefit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalCleaningBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rental_booking_id", nullable = false)
    private long rentalBookingId;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Column(name = "code", nullable = false, length = 32)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private RentalCleaningBenefitStatus status;

    @Column(name = "reserved_cleaning_order_id")
    private Long reservedCleaningOrderId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "available_at", nullable = false)
    private Instant availableAt;

    @Column(name = "redeemed_at")
    private Instant redeemedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    RentalCleaningBenefit(
            long rentalBookingId,
            long customerId,
            String code,
            Instant availableAt
    ) {
        if (rentalBookingId <= 0 || customerId <= 0) {
            throw new IllegalArgumentException("Rental booking and customer ids must be positive");
        }
        this.rentalBookingId = rentalBookingId;
        this.customerId = customerId;
        this.code = requireCode(code);
        this.status = RentalCleaningBenefitStatus.AVAILABLE;
        this.createdAt = Objects.requireNonNull(availableAt, "availableAt");
        this.availableAt = availableAt;
    }

    void reserve(long cleaningOrderId) {
        if (cleaningOrderId <= 0) {
            throw new IllegalArgumentException("cleaningOrderId must be positive");
        }
        if (status != RentalCleaningBenefitStatus.AVAILABLE) {
            throw new RentalCleaningBenefitNotApplicableException(
                    "Rental cleaning benefit is no longer available"
            );
        }
        status = RentalCleaningBenefitStatus.RESERVED;
        reservedCleaningOrderId = cleaningOrderId;
    }

    void release(long cleaningOrderId) {
        if (status == RentalCleaningBenefitStatus.RESERVED
                && Objects.equals(reservedCleaningOrderId, cleaningOrderId)) {
            status = RentalCleaningBenefitStatus.AVAILABLE;
            reservedCleaningOrderId = null;
        }
    }

    void redeem(long cleaningOrderId, Instant at) {
        if (status != RentalCleaningBenefitStatus.RESERVED
                || !Objects.equals(reservedCleaningOrderId, cleaningOrderId)) {
            throw new RentalCleaningBenefitNotApplicableException(
                    "Rental cleaning benefit is not reserved for this order"
            );
        }
        status = RentalCleaningBenefitStatus.REDEEMED;
        reservedCleaningOrderId = null;
        redeemedAt = Objects.requireNonNull(at, "at");
    }

    boolean revokeIfAvailable(Instant at) {
        if (status != RentalCleaningBenefitStatus.AVAILABLE) {
            return false;
        }
        status = RentalCleaningBenefitStatus.REVOKED;
        revokedAt = Objects.requireNonNull(at, "at");
        return true;
    }

    private static String requireCode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
        return value.trim().toUpperCase();
    }
}
