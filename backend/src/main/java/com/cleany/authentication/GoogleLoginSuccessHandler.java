package com.cleany.authentication;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleLoginSuccessHandler implements AuthenticationSuccessHandler {

    static final String SUCCESS_TARGET_SESSION_ATTRIBUTE =
            GoogleLoginSuccessHandler.class.getName() + ".SUCCESS_TARGET";

    private final LoginTargetValidator loginTargetValidator;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        response.sendRedirect(response.encodeRedirectURL(successTarget(request.getSession(false))));
    }

    private String successTarget(HttpSession session) {
        if (session == null) {
            return loginTargetValidator.normalize(null);
        }
        var target = session.getAttribute(SUCCESS_TARGET_SESSION_ATTRIBUTE);
        session.removeAttribute(SUCCESS_TARGET_SESSION_ATTRIBUTE);
        return loginTargetValidator.normalize(target instanceof String path ? path : null);
    }
}
