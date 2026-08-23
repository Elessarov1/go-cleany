package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RentalPropertyTest {

    private static final Instant NOW = Instant.parse("2026-08-23T09:00:00Z");

    @Test
    void newDraft_mayRemainIncompleteAndDefaultsToTry() {
        RentalProperty property = new RentalProperty(NOW);

        Assertions.assertAll(
                () -> Assertions.assertEquals(RentalPropertyStatus.DRAFT, property.getStatus()),
                () -> Assertions.assertNull(property.getSlug()),
                () -> Assertions.assertEquals("TRY", property.getCurrency()),
                () -> Assertions.assertTrue(property.getAmenities().isEmpty())
        );
    }

    @Test
    void incompleteDraft_publishRejectedWithMissingFields() {
        RentalProperty property = new RentalProperty(NOW);

        RentalPropertyCannotBePublishedException exception = Assertions.assertThrows(
                RentalPropertyCannotBePublishedException.class,
                () -> property.publish(false, NOW.plusSeconds(1))
        );

        Assertions.assertAll(
                () -> Assertions.assertTrue(exception.getMessage().contains("titleEn")),
                () -> Assertions.assertFalse(exception.getMessage().contains("descriptionRu")),
                () -> Assertions.assertTrue(exception.getMessage().contains("image")),
                () -> Assertions.assertEquals(RentalPropertyStatus.DRAFT, property.getStatus())
        );
    }

    @Test
    void completeDraft_withImage_publishedAndCanBeArchived() {
        RentalProperty property = new RentalProperty(NOW);
        property.updateDetails(completeDetails(new BigDecimal("2500.00")), NOW.plusSeconds(1));
        property.assignSlug("orange-residence");

        property.publish(true, NOW.plusSeconds(2));

        Assertions.assertAll(
                () -> Assertions.assertEquals(RentalPropertyStatus.PUBLISHED, property.getStatus()),
                () -> Assertions.assertEquals("orange-residence", property.getSlug()),
                () -> Assertions.assertEquals("TRY", property.getCurrency()),
                () -> Assertions.assertEquals(Set.of(RentalAmenity.WIFI, RentalAmenity.POOL),
                        property.getAmenities())
        );

        property.archive(NOW.plusSeconds(3));
        Assertions.assertEquals(RentalPropertyStatus.ARCHIVED, property.getStatus());
    }

    @Test
    void changingEnglishTitle_doesNotChangeAssignedSlug() {
        RentalProperty property = new RentalProperty(NOW);
        property.assignSlug("stable-public-url");
        property.updateDetails(completeDetails(new BigDecimal("2500.00")), NOW.plusSeconds(1));

        property.updateDetails(
                new RentalPropertyDetails(
                        "Новое название",
                        "Completely Different English Title",
                        "Updated English description",
                        "Кестель",
                        "New address",
                        1,
                        2,
                        1,
                        3,
                        new BigDecimal("70.00"),
                        2,
                        new BigDecimal("1800.00"),
                        null,
                        Set.of(RentalAmenity.WIFI)
                ),
                NOW.plusSeconds(2)
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals("stable-public-url", property.getSlug()),
                () -> Assertions.assertEquals("Completely Different English Title", property.getTitleEn()),
                () -> Assertions.assertEquals("TRY", property.getCurrency())
        );
    }

    static RentalPropertyDetails completeDetails(BigDecimal dailyPrice) {
        return new RentalPropertyDetails(
                "Апартаменты Orange Residence",
                "Orange Residence",
                "Bright apartment near the sea",
                "Махмутлар",
                "Barbaros Cd. 24",
                2,
                3,
                1,
                5,
                new BigDecimal("95.50"),
                7,
                dailyPrice,
                "try",
                Set.of(RentalAmenity.WIFI, RentalAmenity.POOL)
        );
    }
}
