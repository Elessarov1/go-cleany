package com.cleany.referral;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cleany.admin.AdminAccessService;

@RestController
@RequestMapping("/api/v1/admin/referrals")
public class AdminReferralController {

    private final AdminAccessService accessService;
    private final ReferralService referralService;

    public AdminReferralController(AdminAccessService accessService, ReferralService referralService) {
        this.accessService = accessService;
        this.referralService = referralService;
    }

    @GetMapping
    public AdminReferralOverviewResponse getOverview() {
        accessService.requireCurrentAdmin();
        return referralService.getAdminOverview();
    }

    @PostMapping("/partners")
    public ReferralPartnerResponse createPartner(
            @Valid @RequestBody CreateReferralPartnerRequest request
    ) {
        accessService.requireCurrentAdmin();
        return referralService.createPartner(request.name());
    }

    @PostMapping("/payouts/{id}/paid")
    public PartnerPayoutResponse markPayoutPaid(@PathVariable long id) {
        accessService.requireCurrentAdmin();
        return referralService.markPayoutPaid(id);
    }
}
