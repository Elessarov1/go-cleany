package com.cleany.customer.activity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/account/activity")
@RequiredArgsConstructor
public class CustomerActivityController {

    private final CustomerActivityService activityService;

    @GetMapping
    public CustomerActivityResponse activity() {
        return activityService.current();
    }
}
