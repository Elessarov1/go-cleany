package com.cleany.customer;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountIdentityService identityService;
    private final AccountLinkingService linkingService;

    @GetMapping("/identities")
    public AccountIdentitiesResponse identities() {
        return identityService.current();
    }

    @PostMapping("/link/telegram")
    public AccountLinkInitiatedResponse initiateTelegramLink() {
        return linkingService.initiateTelegramLink();
    }

    @PostMapping("/link/telegram/confirm")
    public AccountIdentitiesResponse confirmTelegramLink(
            @Valid @RequestBody ConfirmTelegramAccountLinkRequest request
    ) {
        return linkingService.confirmTelegramLink(request.token());
    }
}
