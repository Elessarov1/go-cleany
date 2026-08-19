package com.cleany.referral;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cleany.customer.CustomerAccountService;

@RestController
@RequestMapping("/api/v1/referrals")
public class ReferralController {

    private final CustomerAccountService customerAccountService;
    private final ReferralService referralService;

    public ReferralController(
            CustomerAccountService customerAccountService,
            ReferralService referralService
    ) {
        this.customerAccountService = customerAccountService;
        this.referralService = referralService;
    }

    @GetMapping("/me")
    public ReferralSummaryResponse getCurrentCustomerReferralSummary() {
        long customerId = customerAccountService.currentCustomer().id();
        return referralService.getSummary(customerId);
    }
}
