package com.cleany.authentication;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cleany.authorization.PlatformRoleService;
import com.cleany.analytics.AcquisitionCaptureService;
import com.cleany.configuration.GoogleOidcProperties;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CurrentCustomer;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final CustomerAccountService customerAccountService;
    private final PlatformRoleService roleService;
    private final GoogleOidcProperties googleProperties;
    private final LoginTargetValidator loginTargetValidator;
    private final SecurityErrorWriter errorWriter;
    private final AcquisitionCaptureService acquisitionCaptureService;

    @GetMapping("/me")
    public CurrentAuthenticationResponse me(
            Authentication authentication,
            HttpServletRequest request
    ) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            try {
                return authenticated(customerAccountService.currentCustomer());
            } catch (CustomerAuthenticationRequiredException exception) {
                return CurrentAuthenticationResponse.anonymous(loginProviders());
            }
        }
        CurrentCustomer customer = customerAccountService.currentCustomer();
        acquisitionCaptureService.attachPending(customer.customerId(), request.getSession(false));
        return authenticated(customer);
    }

    private CurrentAuthenticationResponse authenticated(CurrentCustomer customer) {
        return new CurrentAuthenticationResponse(
                true,
                customer.customerId(),
                customer.displayName(),
                customer.provider(),
                roleService.roles(customer.customerId()),
                loginProviders()
        );
    }

    private LoginProvidersResponse loginProviders() {
        return LoginProvidersResponse.from(googleProperties);
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of(
                "headerName", csrfToken.getHeaderName(),
                "token", csrfToken.getToken()
        );
    }

    @GetMapping("/google/login")
    public void googleLogin(
            @RequestParam(name = "returnTo", required = false) String returnTo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        if (!googleProperties.enabled()) {
            errorWriter.write(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "login_provider_unavailable",
                    "Google login is not available"
            );
            return;
        }
        request.getSession().setAttribute(
                GoogleLoginSuccessHandler.SUCCESS_TARGET_SESSION_ATTRIBUTE,
                loginTargetValidator.normalize(returnTo)
        );
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/google/admin")
    public void googleAdminLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        googleLogin("/admin", request, response);
    }
}
