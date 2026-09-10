package com.cleany.authentication;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BearerAuthenticationFilter extends OncePerRequestFilter {
    private final SessionTokenService tokenService;
    private final SecurityErrorWriter errorWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            var authenticated = tokenService.authenticate(authorization.substring(7).trim());
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new BearerAuthenticationToken(
                    authenticated.sessionId(), authenticated.identity()));
            SecurityContextHolder.setContext(context);
            filterChain.doFilter(request, response);
        } catch (SessionTokenException exception) {
            errorWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED,
                    exception.code(), exception.getMessage());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
