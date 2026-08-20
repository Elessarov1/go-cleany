package com.cleany.customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers/me")
public class CustomerProfileController {

    private final CustomerAccountService customerAccountService;

    public CustomerProfileController(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }

    @GetMapping
    CustomerProfileResponse getCurrentProfile() {
        return customerAccountService.currentProfile();
    }
}
