package com.cleany.authorization;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_role")
@IdClass(CustomerRoleId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerRole {

    @Id
    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 32)
    private PlatformRole role;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public CustomerRole(long customerId, PlatformRole role, Instant createdAt) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("customerId must be positive");
        }
        this.customerId = customerId;
        this.role = Objects.requireNonNull(role, "role");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }
}
