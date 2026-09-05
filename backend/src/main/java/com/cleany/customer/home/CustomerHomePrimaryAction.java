package com.cleany.customer.home;

import java.time.LocalDate;

import com.cleany.catalog.PlatformService;
import com.cleany.crossservice.rentaltransfer.RentalTransferBenefitResponse;

public record CustomerHomePrimaryAction(
        CustomerHomePrimaryActionType type,
        PlatformService sourceService,
        long sourceEntityId,
        PlatformService targetService,
        LocalDate relevantDate,
        LocalDate eligibleFrom,
        LocalDate expiresOn,
        String targetPath,
        RentalTransferBenefitResponse benefit
) {
}
