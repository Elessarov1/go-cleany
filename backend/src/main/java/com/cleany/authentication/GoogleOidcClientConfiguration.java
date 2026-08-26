package com.cleany.authentication;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import com.cleany.configuration.GoogleOidcProperties;

@Configuration(proxyBeanMethods = false)
public class GoogleOidcClientConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "web-auth.google", name = "enabled", havingValue = "true")
    ClientRegistrationRepository googleClientRegistrationRepository(
            GoogleOidcProperties properties
    ) {
        var registration = CommonOAuth2Provider.GOOGLE.getBuilder("google")
                .clientId(properties.clientId())
                .clientSecret(properties.clientSecret())
                .scope("openid", "profile", "email")
                .build();
        return new InMemoryClientRegistrationRepository(registration);
    }
}
