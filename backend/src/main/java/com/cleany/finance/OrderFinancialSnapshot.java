package com.cleany.finance;

import java.math.BigDecimal;

public record OrderFinancialSnapshot(
        BigDecimal basePrice,
        BigDecimal commissionRate,
        BigDecimal baseCommission,
        BigDecimal customerDiscount,
        BigDecimal partnerPayout,
        BigDecimal finalCustomerPrice,
        BigDecimal platformNet,
        AcquisitionSource acquisitionSource,
        CustomerDiscountType customerDiscountType
) {
}
