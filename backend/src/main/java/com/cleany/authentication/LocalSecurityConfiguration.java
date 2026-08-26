package com.cleany.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import lombok.RequiredArgsConstructor;

@Profile("local")
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class LocalSecurityConfiguration {

    private final TmaAuthorizationRequestMatcher tmaRequestMatcher;

    @Bean
    SecurityFilterChain localSecurity(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                tmaRequestMatcher,
                                request -> "/api/v1/telegram/webhook".equals(
                                        request.getRequestURI()
                                )
                        )
                )
                .build();
    }
}
