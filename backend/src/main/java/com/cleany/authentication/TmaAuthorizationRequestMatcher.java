package com.cleany.authentication;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class TmaAuthorizationRequestMatcher implements RequestMatcher {

    private static final String PREFIX = "tma ";

    @Override
    public boolean matches(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        return authorization != null
                && authorization.regionMatches(true, 0, PREFIX, 0, PREFIX.length());
    }
}
