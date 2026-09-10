package com.cleany.authentication;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cleany.customer.ExternalIdentityProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/native")
@RequiredArgsConstructor
public class NativeAuthenticationController {
    private final NativeLoginService loginService;
    private final TelegramNativeLoginService telegramLoginService;

    @PostMapping("/challenges")
    public NativeChallengeResponse challenge(@Valid @RequestBody CreateNativeChallengeRequest request) {
        return loginService.challenge(request);
    }

    @PostMapping("/google")
    public SessionTokensResponse google(@Valid @RequestBody NativeProviderLoginRequest request) {
        return loginService.complete(ExternalIdentityProvider.GOOGLE, request);
    }

    @PostMapping("/apple")
    public SessionTokensResponse apple(@Valid @RequestBody NativeProviderLoginRequest request) {
        return loginService.complete(ExternalIdentityProvider.APPLE, request);
    }

    @PostMapping("/telegram/attempts")
    public TelegramLoginAttemptResponse telegramAttempt() {
        return telegramLoginService.create();
    }

    @PostMapping("/telegram/attempts/{attemptId}/exchange")
    public SessionTokensResponse telegramExchange(
            @org.springframework.web.bind.annotation.PathVariable java.util.UUID attemptId,
            @Valid @RequestBody TelegramLoginExchangeRequest request
    ) {
        return telegramLoginService.exchange(attemptId, request);
    }
}
