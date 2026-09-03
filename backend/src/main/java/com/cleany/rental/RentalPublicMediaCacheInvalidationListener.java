package com.cleany.rental;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class RentalPublicMediaCacheInvalidationListener {

    private final RentalPublicMediaCache cache;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void invalidate(RentalPropertyMediaChangedEvent event) {
        cache.invalidateProperty(event.propertyId());
    }
}
