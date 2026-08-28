package com.cleany.notification;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/v1/account/notifications")
@RequiredArgsConstructor
public class CustomerNotificationController {

    private final CustomerNotificationInboxService inboxService;

    @GetMapping
    public CustomerNotificationPageResponse notifications(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return inboxService.current(page, size);
    }

    @GetMapping("/unread-count")
    public CustomerNotificationUnreadCountResponse unreadCount() {
        return inboxService.unreadCount();
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markRead(@PathVariable long notificationId) {
        inboxService.markRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        inboxService.markAllRead();
        return ResponseEntity.noContent().build();
    }
}
