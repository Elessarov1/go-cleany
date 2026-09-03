package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import org.hibernate.annotations.BatchSize;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rental_property")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RentalProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", length = 120, unique = true)
    private String slug;

    @Column(name = "title_ru")
    private String titleRu;

    @Column(name = "title_en")
    private String titleEn;

    @Column(name = "description_en", length = 5000)
    private String descriptionEn;

    @Column(name = "area")
    private String area;

    @Column(name = "address", length = 1000)
    private String address;

    @Column(name = "bedrooms")
    private Integer bedrooms;

    @Column(name = "beds")
    private Integer beds;

    @Column(name = "bathrooms")
    private Integer bathrooms;

    @Column(name = "max_guests")
    private Integer maxGuests;

    @Column(name = "area_sqm", precision = 8, scale = 2)
    private BigDecimal areaSqm;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "base_daily_price", precision = 12, scale = 2)
    private BigDecimal baseDailyPrice;

    @Column(name = "currency", length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private RentalPropertyStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "rental_property_amenity",
            joinColumns = @JoinColumn(name = "property_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "amenity", nullable = false, length = 64)
    @BatchSize(size = 50)
    private Set<RentalAmenity> amenities = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public RentalProperty(Instant createdAt) {
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = createdAt;
        this.status = RentalPropertyStatus.DRAFT;
        this.currency = "TRY";
    }

    public void updateDetails(RentalPropertyDetails details, Instant updatedAt) {
        RentalPropertyDetails required = Objects.requireNonNull(details, "details");
        this.titleRu = required.titleRu();
        this.titleEn = required.titleEn();
        this.descriptionEn = required.descriptionEn();
        this.area = required.area();
        this.address = required.address();
        this.bedrooms = required.bedrooms();
        this.beds = required.beds();
        this.bathrooms = required.bathrooms();
        this.maxGuests = required.maxGuests();
        this.areaSqm = required.areaSqm();
        this.floor = required.floor();
        this.baseDailyPrice = required.baseDailyPrice();
        if (required.currency() != null) {
            this.currency = required.currency();
        }
        this.amenities.clear();
        this.amenities.addAll(required.amenities());
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public void publish(boolean hasImage, Instant publishedAt) {
        var missing = new ArrayList<String>();
        requirePresent(missing, slug, "slug");
        requirePresent(missing, titleEn, "titleEn");
        requirePresent(missing, descriptionEn, "descriptionEn");
        requirePresent(missing, area, "area");
        requirePresent(missing, address, "address");
        requirePresent(missing, bedrooms, "bedrooms");
        requirePresent(missing, maxGuests, "maxGuests");
        requirePresent(missing, areaSqm, "areaSqm");
        requirePresent(missing, baseDailyPrice, "baseDailyPrice");
        requirePresent(missing, currency, "currency");
        if (!hasImage) {
            missing.add("image");
        }
        if (!missing.isEmpty()) {
            throw new RentalPropertyCannotBePublishedException(
                    "Rental property is incomplete: " + String.join(", ", missing)
            );
        }
        status = RentalPropertyStatus.PUBLISHED;
        updatedAt = Objects.requireNonNull(publishedAt, "publishedAt");
    }

    public void archive(Instant archivedAt) {
        status = RentalPropertyStatus.ARCHIVED;
        updatedAt = Objects.requireNonNull(archivedAt, "archivedAt");
    }

    public void touch(Instant changedAt) {
        updatedAt = Objects.requireNonNull(changedAt, "changedAt");
    }

    public void unpublish(Instant unpublishedAt) {
        if (status != RentalPropertyStatus.PUBLISHED) {
            throw new RentalPropertyCannotBeUnpublishedException(requireId());
        }
        status = RentalPropertyStatus.DRAFT;
        updatedAt = Objects.requireNonNull(unpublishedAt, "unpublishedAt");
    }

    public void assignSlug(String generatedSlug) {
        if (slug != null) {
            throw new IllegalStateException("Rental property slug is already assigned");
        }
        slug = Objects.requireNonNull(generatedSlug, "generatedSlug");
    }

    private static void requirePresent(ArrayList<String> missing, Object value, String field) {
        if (value == null || value instanceof String text && text.isBlank()) {
            missing.add(field);
        }
    }

    private long requireId() {
        return id == null ? 0 : id;
    }
}
