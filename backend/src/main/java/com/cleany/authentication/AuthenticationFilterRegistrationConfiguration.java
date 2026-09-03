package com.cleany.authentication;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class AuthenticationFilterRegistrationConfiguration {

    @Bean
    FilterRegistrationBean<CurrentCustomerResolutionFilter> currentCustomerResolutionRegistration(
            CurrentCustomerResolutionFilter filter
    ) {
        var registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
