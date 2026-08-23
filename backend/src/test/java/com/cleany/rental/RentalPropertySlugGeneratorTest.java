package com.cleany.rental;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RentalPropertySlugGeneratorTest {

    @Test
    void baseSlug_normalizesEnglishTitle() {
        Assertions.assertEquals(
                "sea-view-1-1-residence",
                RentalPropertySlugGenerator.baseSlug("  Sea View 1+1 Résidence  ")
        );
    }

    @Test
    void generate_addsNextAvailableCollisionSuffix() {
        RentalPropertyRepository repository = Mockito.mock(RentalPropertyRepository.class);
        Mockito.when(repository.existsBySlugIgnoreCaseAndIdNot("sea-view-apartment", 42L))
                .thenReturn(true);
        Mockito.when(repository.existsBySlugIgnoreCaseAndIdNot("sea-view-apartment-2", 42L))
                .thenReturn(true);
        RentalPropertySlugGenerator generator = new RentalPropertySlugGenerator(repository);

        String slug = generator.generate("Sea View Apartment", 42L);

        Assertions.assertEquals("sea-view-apartment-3", slug);
    }
}
