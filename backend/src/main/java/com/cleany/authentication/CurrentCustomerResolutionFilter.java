package com.cleany.authentication;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CurrentCustomerRequestCache;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentCustomerResolutionFilter extends OncePerRequestFilter {

    private final CustomerAccountService customerAccountService;
    private final CurrentCustomerRequestCache requestCache;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/v1/")
                || path.equals("/api/v1/telegram/webhook");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        requestCache.activate(request);
        try {
            CurrentCustomer customer = customerAccountService.currentCustomer();
            requestCache.store(request, customer);
        } catch (CustomerAuthenticationRequiredException ignored) {
            // Public APIs and authentication discovery remain available to anonymous users.
        }
        filterChain.doFilter(request, response);
    }
}
