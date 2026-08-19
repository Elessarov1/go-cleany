package com.cleany.referral;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReferralPartnerRequest(
        @NotBlank @Size(max = 255) String name
) {
}
