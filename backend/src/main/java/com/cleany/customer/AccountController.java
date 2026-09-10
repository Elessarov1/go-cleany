package com.cleany.customer;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountIdentityService identityService;
    private final AccountSecurityLifecycleService lifecycleService;

    @GetMapping("/identities")
    public AccountIdentitiesResponse identities() {
        return identityService.current();
    }

    @PostMapping("/identity-links")
    public IdentityLinkAttemptResponse createIdentityLink(
            @Valid @RequestBody CreateIdentityLinkRequest request
    ) {
        return lifecycleService.createLink(request);
    }

    @PostMapping("/identity-links/{id}/verify")
    public void verifyIdentityLink(@PathVariable UUID id,
                                   @Valid @RequestBody VerifyIdentityLinkRequest request) {
        lifecycleService.verifyLink(id, request);
    }

    @PostMapping("/identity-links/{id}/confirm")
    public AccountIdentitiesResponse confirmIdentityLink(
            @PathVariable UUID id,
            @Valid @RequestBody ConfirmIdentityLinkRequest request
    ) {
        return lifecycleService.confirmLink(id, request);
    }

    @PostMapping("/reauthentication-challenges")
    public ReauthenticationChallengeResponse createReauthenticationChallenge() {
        return lifecycleService.createReauthentication();
    }

    @DeleteMapping("/identities/{identityId}")
    public AccountIdentitiesResponse unlinkIdentity(
            @PathVariable long identityId,
            @Valid @RequestBody SensitiveAccountActionRequest request
    ) {
        return lifecycleService.unlink(identityId, request);
    }

    @PostMapping("/deletion-requests")
    public AccountDeletionRequestResponse createDeletionRequest() {
        return lifecycleService.createDeletionRequest();
    }

    @PostMapping("/deletion-requests/{id}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmDeletion(@PathVariable UUID id,
                                @Valid @RequestBody SensitiveAccountActionRequest request,
                                HttpServletRequest servletRequest) {
        lifecycleService.deleteAccount(id, request);
        var session = servletRequest.getSession(false);
        if (session != null) session.invalidate();
    }
}
