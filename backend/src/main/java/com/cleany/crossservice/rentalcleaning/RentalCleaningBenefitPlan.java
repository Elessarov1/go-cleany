package com.cleany.crossservice.rentalcleaning;

import com.cleany.finance.OrderFinancialSnapshot;

public record RentalCleaningBenefitPlan(
        OrderFinancialSnapshot financialSnapshot,
        long benefitId
) {
}
