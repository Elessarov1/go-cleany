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
@Table(name = "customer_account")
public class CustomerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "phone", length = 40)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private CustomerAccountStatus status = CustomerAccountStatus.ACTIVE;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected CustomerAccount() {
    }

    public CustomerAccount(Instant createdAt) {
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getPhone() {
        return phone;
    }

    public CustomerAccountStatus getStatus() {
        return status;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    void updatePhone(String phone) {
        this.phone = Objects.requireNonNull(phone);
    }

    void mergeProfile(Instant earliestCreatedAt, String mergedPhone) {
        createdAt = Objects.requireNonNull(earliestCreatedAt, "earliestCreatedAt");
        phone = mergedPhone;
    }

    void anonymize(Instant at) {
        status = CustomerAccountStatus.DELETED;
        deletedAt = Objects.requireNonNull(at, "at");
        phone = null;
    }
}
