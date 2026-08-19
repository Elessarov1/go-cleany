package com.cleany.admin;

import java.math.BigDecimal;

import com.cleany.finance.AcquisitionSource;
import com.cleany.finance.CustomerDiscountType;
import com.cleany.order.CleaningOrder;

public record AdminOrderFinancialResponse(
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

    static AdminOrderFinancialResponse from(CleaningOrder order) {
        return new AdminOrderFinancialResponse(
                order.getBasePrice(),
                order.getCommissionRate(),
                order.getBaseCommission(),
                order.getCustomerDiscount(),
                order.getPartnerPayout(),
                order.getFinalCustomerPrice(),
                order.getPlatformNet(),
                order.getAcquisitionSource(),
                order.getCustomerDiscountType()
        );
    }
}
