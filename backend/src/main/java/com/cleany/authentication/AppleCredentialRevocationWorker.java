package com.cleany.authentication;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AppleCredentialRevocationWorker {
    private final AppleCredentialRevocationQueue queue;
    private final AppleTokenClient client;
    private final SessionTokenCrypto crypto;

    @Scheduled(fixedDelayString = "${native-auth.apple.revocation-worker-delay:10s}")
    void process() {
        for (Long id : queue.claim()) {
            try {
                client.revoke(crypto.decrypt(queue.payload(id)));
                queue.delivered(id);
            } catch (RestClientResponseException exception) {
                int status = exception.getStatusCode().value();
                queue.failed(id, status == 429 || status >= 500, "apple_http_" + status);
            } catch (RuntimeException exception) {
                queue.failed(id, true, "apple_revocation_error");
            }
        }
    }
}
