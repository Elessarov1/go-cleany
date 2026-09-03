package com.cleany.authentication;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import com.cleany.configuration.GoogleOidcProperties;

import lombok.RequiredArgsConstructor;

@Profile("!local")
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final GoogleOidcProperties googleProperties;
    private final TmaAuthenticationFilter tmaAuthenticationFilter;
    private final CurrentCustomerResolutionFilter currentCustomerResolutionFilter;
    private final TmaAuthorizationRequestMatcher tmaRequestMatcher;
    private final SecurityErrorWriter errorWriter;
    private final GoogleOidcUserService googleOidcUserService;
    private final GoogleLoginSuccessHandler googleLoginSuccessHandler;

    @Bean
    SecurityFilterChain applicationSecurity(
            HttpSecurity http,
            org.springframework.beans.factory.ObjectProvider<ClientRegistrationRepository> registrations
    ) throws Exception {
        var csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/actuator/health/**",
                                "/error",
                                "/oauth2/**",
                                "/login/**",
                                "/api/v1/auth/**",
                                "/api/v1/telegram/webhook"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/cleaning/configuration").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/rental/configuration").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/rental/properties/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog/services").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfRepository)
                        .ignoringRequestMatchers(
                                tmaRequestMatcher,
                                request -> "/api/v1/telegram/webhook".equals(request.getRequestURI())
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> errorWriter.write(
                                response,
                                HttpServletResponse.SC_UNAUTHORIZED,
                                "authentication_required",
                                "Authentication is required"
                        ))
                        .accessDeniedHandler((request, response, exception) -> errorWriter.write(
                                response,
                                HttpServletResponse.SC_FORBIDDEN,
                                "access_denied",
                                "Access is denied"
                        ))
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("SESSION", "XSRF-TOKEN")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(
                                HttpStatus.NO_CONTENT
                        ))
                )
                .addFilterBefore(tmaAuthenticationFilter, AnonymousAuthenticationFilter.class)
                .addFilterAfter(currentCustomerResolutionFilter, TmaAuthenticationFilter.class);

        if (googleProperties.enabled()) {
            if (registrations.getIfAvailable() == null) {
                throw new IllegalStateException("Google OIDC client registration is unavailable");
            }
            http.oauth2Login(oauth -> oauth
                    .userInfoEndpoint(userInfo -> userInfo
                            .oidcUserService(googleOidcUserService::loadUser)
                    )
                    .successHandler(googleLoginSuccessHandler)
            );
        }
        return http.build();
    }
}
