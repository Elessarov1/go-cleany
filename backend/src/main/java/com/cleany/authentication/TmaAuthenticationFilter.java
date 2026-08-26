package com.cleany.authentication;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cleany.customer.AuthenticatedCustomerIdentity;
import com.cleany.telegram.TelegramInitDataValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TmaAuthenticationFilter extends OncePerRequestFilter {

    private static final String SCHEME = "tma";

    private final TelegramInitDataValidator validator;
    private final SecurityErrorWriter errorWriter;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        List<String> headers = Collections.list(request.getHeaders(HttpHeaders.AUTHORIZATION));
        if (headers.stream().noneMatch(TmaAuthenticationFilter::hasTmaScheme)) {
            filterChain.doFilter(request, response);
            return;
        }
        AuthenticatedCustomerIdentity identity;
        try {
            identity = authenticate(headers);
        } catch (CustomerAuthenticationRequiredException exception) {
            errorWriter.write(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "authentication_required",
                    "Valid Telegram Mini App authentication is required"
            );
            return;
        }

        var previousContext = SecurityContextHolder.getContext();
        var tmaContext = SecurityContextHolder.createEmptyContext();
        tmaContext.setAuthentication(new TmaAuthenticationToken(identity));
        SecurityContextHolder.setContext(tmaContext);
        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.setContext(previousContext);
        }
    }

    private AuthenticatedCustomerIdentity authenticate(List<String> headers) {
        if (headers.size() != 1) {
            throw new CustomerAuthenticationRequiredException();
        }
        String authorization = headers.getFirst();
        int separator = authorization.indexOf(' ');
        if (separator < 1
                || !SCHEME.equalsIgnoreCase(authorization.substring(0, separator))) {
            throw new CustomerAuthenticationRequiredException();
        }
        String initData = authorization.substring(separator + 1);
        if (initData.isBlank() || !initData.equals(initData.strip())) {
            throw new CustomerAuthenticationRequiredException();
        }
        return validator.validate(initData).authenticatedIdentity();
    }

    private static boolean hasTmaScheme(String authorization) {
        if (authorization == null) {
            return false;
        }
        int separator = authorization.indexOf(' ');
        return separator > 0
                && SCHEME.equalsIgnoreCase(authorization.substring(0, separator));
    }
}
