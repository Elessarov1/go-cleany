package com.cleany.customer.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/account/home")
@RequiredArgsConstructor
public class CustomerHomeController {

    private final CustomerHomeService homeService;

    @GetMapping
    public CustomerHomeResponse home() {
        return homeService.current();
    }
}
