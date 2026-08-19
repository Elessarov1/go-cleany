package com.cleany.order;

import java.math.BigDecimal;

import com.cleany.finance.CustomerDiscountType;
import com.cleany.finance.OrderFinancialSnapshot;

public record CleaningOrderQuoteResponse(
        BigDecimal basePrice,
        BigDecimal customerDiscount,
        BigDecimal finalCustomerPrice,
        CustomerDiscountType customerDiscountType,
        String currency
) {

    static CleaningOrderQuoteResponse from(OrderFinancialSnapshot snapshot, String currency) {
        return new CleaningOrderQuoteResponse(
                snapshot.basePrice(),
                snapshot.customerDiscount(),
                snapshot.finalCustomerPrice(),
                snapshot.customerDiscountType(),
                currency
        );
    }
}
