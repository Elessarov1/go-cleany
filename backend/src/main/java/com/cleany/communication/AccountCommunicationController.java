package com.cleany.communication;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountCommunicationController {
    private final NotificationPreferenceService preferenceService;
    private final CommunicationEndpointService endpointService;

    @GetMapping("/notification-preferences")
    public NotificationPreferencesResponse preferences() {
        return preferenceService.current();
    }

    @PatchMapping("/notification-preferences")
    public NotificationPreferencesResponse updatePreferences(
            @RequestBody UpdateNotificationPreferencesRequest request
    ) {
        return preferenceService.update(request);
    }

    @PostMapping("/communication-endpoints")
    public CommunicationEndpointResponse register(
            @Valid @RequestBody RegisterCommunicationEndpointRequest request
    ) {
        return endpointService.register(request);
    }

    @DeleteMapping("/communication-endpoints/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        endpointService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/communication-endpoints")
    public ResponseEntity<Void> deleteCurrentSessionEndpoints() {
        endpointService.deleteCurrentSession();
        return ResponseEntity.noContent().build();
    }
}
