package com.cleany.order;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cleaning_order_photo")
public class CleaningOrderPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private CleaningOrder order;

    @Column(name = "telegram_file_id", nullable = false, length = 512)
    private String telegramFileId;

    @Column(name = "telegram_file_unique_id", nullable = false, length = 255)
    private String telegramFileUniqueId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CleaningOrderPhoto() {
    }

    public CleaningOrderPhoto(
            CleaningOrder order,
            String telegramFileId,
            String telegramFileUniqueId,
            Instant createdAt
    ) {
        this.order = Objects.requireNonNull(order);
        this.telegramFileId = Objects.requireNonNull(telegramFileId);
        this.telegramFileUniqueId = Objects.requireNonNull(telegramFileUniqueId);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public Long getId() {
        return id;
    }

    public CleaningOrder getOrder() {
        return order;
    }

    public String getTelegramFileId() {
        return telegramFileId;
    }

    public String getTelegramFileUniqueId() {
        return telegramFileUniqueId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
