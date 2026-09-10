package com.cleany.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import com.cleany.pagination.CursorPageResponse;

@Validated
@RestController
@RequestMapping("/api/v1/account/notifications")
@RequiredArgsConstructor
public class CustomerNotificationController {

    private final CustomerNotificationInboxService inboxService;

    @GetMapping
    public CursorPageResponse<CustomerNotificationResponse> notifications(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer size
    ) {
        return inboxService.current(cursor, size);
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
