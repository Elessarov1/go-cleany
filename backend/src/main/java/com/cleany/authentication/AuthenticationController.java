package com.cleany.authentication;

import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.cleany.authorization.PlatformRoleService;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CurrentCustomer;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final CustomerAccountService customerAccountService;
    private final PlatformRoleService roleService;

    @GetMapping("/me")
    public CurrentAuthenticationResponse me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            try {
                return authenticated(customerAccountService.currentCustomer());
            } catch (CustomerAuthenticationRequiredException exception) {
                return CurrentAuthenticationResponse.anonymous();
            }
        }
        return authenticated(customerAccountService.currentCustomer());
    }

    private CurrentAuthenticationResponse authenticated(CurrentCustomer customer) {
        return new CurrentAuthenticationResponse(
                true,
                customer.customerId(),
                customer.displayName(),
                customer.provider(),
                roleService.roles(customer.customerId())
        );
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of(
                "headerName", csrfToken.getHeaderName(),
                "token", csrfToken.getToken()
        );
    }

    @GetMapping("/google/admin")
    public RedirectView googleAdminLogin(HttpSession session) {
        session.setAttribute(
                GoogleLoginSuccessHandler.SUCCESS_TARGET_SESSION_ATTRIBUTE,
                "/admin"
        );
        return new RedirectView("/oauth2/authorization/google");
    }
}
