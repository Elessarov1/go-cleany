package com.cleany.referral;

import java.math.BigDecimal;
import java.time.Instant;

public record PartnerPayoutResponse(
        long id,
        long partnerId,
        String partnerName,
        long sourceOrderId,
        BigDecimal amount,
        String currency,
        PartnerPayoutStatus status,
        Instant createdAt,
        Instant paidAt
) {
}
