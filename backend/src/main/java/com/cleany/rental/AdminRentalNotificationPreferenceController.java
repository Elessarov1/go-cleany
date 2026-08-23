package com.cleany.rental;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/rental/notification-preferences")
@RequiredArgsConstructor
public class AdminRentalNotificationPreferenceController {

    private final RentalAdminNotificationPreferenceService preferenceService;

    @GetMapping
    public RentalAdminNotificationPreferenceResponse getCurrent() {
        return preferenceService.current();
    }

    @PutMapping
    public RentalAdminNotificationPreferenceResponse updateCurrent(
            @Valid @RequestBody UpdateRentalAdminNotificationPreferenceRequest request
    ) {
        return preferenceService.update(request);
    }
}
