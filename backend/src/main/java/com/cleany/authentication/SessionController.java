package com.cleany.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cleany.customer.CustomerAccountService;
import com.cleany.communication.CommunicationEndpointService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionTokenService tokenService;
    private final CustomerAccountService customerAccountService;
    private final CommunicationEndpointService endpointService;

    @PostMapping("/refresh")
    public SessionTokensResponse refresh(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody RefreshSessionRequest request
    ) {
        return tokenService.refresh(request.refreshToken(), idempotencyKey);
    }

    @DeleteMapping("/current")
    public ResponseEntity<Void> revokeCurrent(Authentication authentication, HttpServletRequest request) {
        long customerId = customerAccountService.currentCustomer().customerId();
        if (authentication instanceof BearerAuthenticationToken bearer) {
            endpointService.disableSession(bearer.sessionId());
            tokenService.revoke(bearer.sessionId(), customerId, "CUSTOMER_LOGOUT");
        }
        var httpSession = request.getSession(false);
        if (httpSession != null) httpSession.invalidate();
        ResponseCookie sessionCookie = expiredCookie("SESSION", true);
        ResponseCookie csrfCookie = expiredCookie("XSRF-TOKEN", false);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString(), csrfCookie.toString())
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> revokeAll() {
        long customerId = customerAccountService.currentCustomer().customerId();
        endpointService.disableCustomer(customerId);
        tokenService.revokeAll(customerId, "CUSTOMER_REVOKE_ALL");
        return ResponseEntity.noContent().build();
    }

    private static ResponseCookie expiredCookie(String name, boolean httpOnly) {
        return ResponseCookie.from(name, "")
                .path("/")
                .httpOnly(httpOnly)
                .sameSite("Lax")
                .maxAge(0)
                .build();
    }
}
