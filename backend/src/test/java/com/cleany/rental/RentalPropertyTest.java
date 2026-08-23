package com.cleany.rental;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RentalPropertyTest {

    private static final Instant NOW = Instant.parse("2026-08-23T09:00:00Z");

    @Test
    void newDraft_mayRemainIncomplete() {
        RentalProperty property = new RentalProperty(NOW);

        Assertions.assertAll(
                () -> Assertions.assertEquals(RentalPropertyStatus.DRAFT, property.getStatus()),
                () -> Assertions.assertNull(property.getSlug()),
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
                () -> Assertions.assertTrue(exception.getMessage().contains("titleRu")),
                () -> Assertions.assertTrue(exception.getMessage().contains("image")),
                () -> Assertions.assertEquals(RentalPropertyStatus.DRAFT, property.getStatus())
        );
    }

    @Test
    void completeDraft_withImage_publishedAndCanBeArchived() {
        RentalProperty property = new RentalProperty(NOW);
        property.updateDetails(completeDetails(new BigDecimal("2500.00")), NOW.plusSeconds(1));

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

    static RentalPropertyDetails completeDetails(BigDecimal dailyPrice) {
        return new RentalPropertyDetails(
                " Orange-Residence ",
                "Апартаменты Orange Residence",
                "Orange Residence apartment",
                "Светлые апартаменты рядом с морем",
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
