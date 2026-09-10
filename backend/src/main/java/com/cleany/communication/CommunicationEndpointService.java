package com.cleany.communication;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.authentication.CurrentFirstPartySession;
import com.cleany.authentication.SessionTokenCrypto;
import com.cleany.customer.CustomerAccountService;
import com.cleany.customer.CustomerExternalIdentity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunicationEndpointService {
    private final CommunicationEndpointRepository repository;
    private final CustomerAccountService accountService;
    private final CurrentFirstPartySession currentSession;
    private final SessionTokenCrypto crypto;
    private final Clock clock;

    @Transactional
    public CommunicationEndpointResponse register(RegisterCommunicationEndpointRequest request) {
        if (request.platform() != CommunicationPlatform.IOS
                && request.platform() != CommunicationPlatform.ANDROID) {
            throw new IllegalArgumentException("FCM endpoint platform must be IOS or ANDROID");
        }
        var customer = accountService.currentCustomer();
        UUID sessionId = currentSession.id().orElseThrow(() ->
                new IllegalArgumentException("FCM endpoints require a native bearer session"));
        String hash = crypto.hash(request.registrationToken());
        String ciphertext = crypto.encrypt(request.registrationToken());
        CommunicationEndpoint endpoint = repository
                .findByTypeAndAddressHash(CommunicationEndpointType.FCM, hash)
                .orElseGet(() -> new CommunicationEndpoint(UUID.randomUUID(), customer.customerId(),
                        sessionId, CommunicationEndpointType.FCM, request.platform(), hash, ciphertext,
                        request.installationId(), request.locale(), request.appVersion(), clock.instant()));
        endpoint.reactivate(customer.customerId(), sessionId, ciphertext, request.installationId(),
                request.locale(), request.appVersion(), clock.instant());
        return CommunicationEndpointResponse.from(repository.save(endpoint));
    }

    @Transactional
    public void delete(UUID id) {
        long customerId = accountService.currentCustomer().customerId();
        repository.findByIdAndCustomerId(id, customerId).ifPresent(repository::delete);
    }

    @Transactional
    public void deleteCurrentSession() {
        currentSession.id().ifPresent(sessionId -> repository.findAllBySessionId(sessionId)
                .forEach(repository::delete));
    }

    @Transactional
    public CommunicationEndpoint ensureTelegram(CustomerExternalIdentity identity) {
        String address = identity.getExternalSubject();
        String hash = crypto.hash(address);
        CommunicationEndpoint endpoint = repository
                .findByTypeAndAddressHash(CommunicationEndpointType.TELEGRAM, hash)
                .orElseGet(() -> new CommunicationEndpoint(UUID.randomUUID(), identity.getCustomerId(), null,
                        CommunicationEndpointType.TELEGRAM, CommunicationPlatform.TELEGRAM,
                        hash, address, null, identity.getLanguageCode(), null, clock.instant()));
        endpoint.reactivate(identity.getCustomerId(), null, address, null,
                identity.getLanguageCode(), null, clock.instant());
        return repository.save(endpoint);
    }

    @Transactional
    public void disableSession(UUID sessionId) {
        repository.findAllBySessionId(sessionId).forEach(endpoint -> endpoint.disable(clock.instant()));
    }

    @Transactional
    public void disableCustomer(long customerId) {
        repository.findAllByCustomerId(customerId).forEach(endpoint -> endpoint.disable(clock.instant()));
    }

    @Transactional(readOnly = true)
    public List<CommunicationEndpoint> active(long customerId) {
        return repository.findAllByCustomerIdAndStatus(customerId, CommunicationEndpointStatus.ACTIVE);
    }
}
