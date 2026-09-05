package com.cleany.crossservice.rentaltransfer;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rental_transfer_benefit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalTransferBenefit {

    @Id
    @Column(name = "rental_booking_id", nullable = false)
    private Long rentalBookingId;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RentalTransferBenefitStatus status;

    @Column(name = "transfer_booking_id")
    private Long transferBookingId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "reserved_at")
    private Instant reservedAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Column(name = "released_at")
    private Instant releasedAt;

    @Version
    @Column(nullable = false)
    private long version;

    RentalTransferBenefit(long rentalBookingId, long customerId, long transferBookingId, Instant now) {
        if (rentalBookingId <= 0 || customerId <= 0 || transferBookingId <= 0) {
            throw new IllegalArgumentException("Rental, customer and transfer ids must be positive");
        }
        this.rentalBookingId = rentalBookingId;
        this.customerId = customerId;
        this.status = RentalTransferBenefitStatus.RESERVED;
        this.transferBookingId = transferBookingId;
        this.createdAt = Objects.requireNonNull(now, "now");
        this.updatedAt = now;
        this.reservedAt = now;
    }

    void reserve(long transferBookingId, Instant now) {
        if (status != RentalTransferBenefitStatus.RELEASED || transferBookingId <= 0) {
            throw new IllegalStateException("Rental transfer benefit cannot be reserved");
        }
        status = RentalTransferBenefitStatus.RESERVED;
        this.transferBookingId = transferBookingId;
        updatedAt = Objects.requireNonNull(now, "now");
        reservedAt = now;
        consumedAt = null;
        releasedAt = null;
    }

    void consume(Instant now) {
        if (status != RentalTransferBenefitStatus.RESERVED) {
            return;
        }
        status = RentalTransferBenefitStatus.CONSUMED;
        updatedAt = Objects.requireNonNull(now, "now");
        consumedAt = now;
    }

    void release(Instant now) {
        if (status != RentalTransferBenefitStatus.RESERVED) {
            return;
        }
        status = RentalTransferBenefitStatus.RELEASED;
        transferBookingId = null;
        updatedAt = Objects.requireNonNull(now, "now");
        releasedAt = now;
    }
}
