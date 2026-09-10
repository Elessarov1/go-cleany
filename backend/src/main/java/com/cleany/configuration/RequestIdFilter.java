package com.cleany.configuration;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {
    public static final String ATTRIBUTE = RequestIdFilter.class.getName() + ".requestId";
    public static final String HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestId = normalized(request.getHeader(HEADER));
        request.setAttribute(ATTRIBUTE, requestId);
        response.setHeader(HEADER, requestId);
        filterChain.doFilter(request, response);
    }

    private static String normalized(String candidate) {
        if (candidate != null && candidate.matches("[A-Za-z0-9._:-]{1,128}")) return candidate;
        return UUID.randomUUID().toString();
    }
}
