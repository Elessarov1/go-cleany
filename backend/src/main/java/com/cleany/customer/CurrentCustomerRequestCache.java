package com.cleany.customer;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class CurrentCustomerRequestCache {

    private static final String ATTRIBUTE = CurrentCustomerRequestCache.class.getName()
            + ".currentCustomer";
    private static final String ACTIVE_ATTRIBUTE = CurrentCustomerRequestCache.class.getName()
            + ".active";

    public void activate(HttpServletRequest request) {
        request.setAttribute(ACTIVE_ATTRIBUTE, Boolean.TRUE);
    }

    public Optional<CurrentCustomer> current() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return get(attributes.getRequest());
        }
        return Optional.empty();
    }

    public void store(HttpServletRequest request, CurrentCustomer customer) {
        if (isActive(request)) {
            request.setAttribute(ATTRIBUTE, customer);
        }
    }

    public void store(CurrentCustomer customer) {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            store(attributes.getRequest(), customer);
        }
    }

    private static Optional<CurrentCustomer> get(HttpServletRequest request) {
        if (!isActive(request)) {
            return Optional.empty();
        }
        Object value = request.getAttribute(ATTRIBUTE);
        return value instanceof CurrentCustomer customer
                ? Optional.of(customer)
                : Optional.empty();
    }

    private static boolean isActive(HttpServletRequest request) {
        return Boolean.TRUE.equals(request.getAttribute(ACTIVE_ATTRIBUTE));
    }
}
