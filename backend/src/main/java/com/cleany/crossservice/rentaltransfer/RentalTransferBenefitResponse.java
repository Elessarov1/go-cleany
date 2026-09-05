package com.cleany.crossservice.rentaltransfer;

import java.math.BigDecimal;

import com.cleany.transfer.TransferBenefitType;

public record RentalTransferBenefitResponse(
        TransferBenefitType type,
        BigDecimal discountRate
) {
}
