package com.cleany.authentication;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.cleany.customer.CustomerAccountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleOidcUserService {

    private final OidcCustomerIdentityMapper identityMapper;
    private final CustomerAccountService customerAccountService;
    private final OidcUserService delegate = new OidcUserService();

    public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
        OidcUser user = delegate.loadUser(request);
        customerAccountService.resolveCustomer(identityMapper.map(user));
        return user;
    }
}
