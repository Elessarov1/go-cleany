package com.cleany.rental;

import java.math.BigDecimal;
import java.util.Set;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.ExternalIdentityProvider;

final class RentalTestFixtures {

    private static final byte[] JPEG = {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x03, (byte) 0xD9
    };

    private RentalTestFixtures() {
    }

    static RentalPropertyResponse publishedProperty(
            RentalPropertyService propertyService,
            RentalPropertyMediaService mediaService,
            String slug,
            BigDecimal dailyPrice
    ) {
        RentalPropertyResponse draft = propertyService.createDraft();
        propertyService.update(draft.id(), details(slug, dailyPrice));
        mediaService.add(draft.id(), JPEG, true);
        return propertyService.publish(draft.id());
    }

    static RentalPropertyDetails details(String slug, BigDecimal dailyPrice) {
        return new RentalPropertyDetails(
                slug,
                "Апартаменты " + slug,
                "Apartment " + slug,
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
                "TRY",
                Set.of(RentalAmenity.WIFI, RentalAmenity.POOL)
        );
    }

    static CurrentCustomer customer(CustomerAccountService service, String externalSubject) {
        return service.resolveCustomer(new AuthenticatedCustomerIdentity(
                ExternalIdentityProvider.TELEGRAM,
                externalSubject,
                "customer_" + externalSubject,
                "Customer " + externalSubject,
                "ru"
        ));
    }
}
