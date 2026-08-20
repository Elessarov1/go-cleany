package com.cleany.admin;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cleany.order.OnsiteIssueService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminQueryService queryService;
    private final AdminAccessService accessService;
    private final OnsiteIssueService onsiteIssueService;

    public AdminController(
            AdminQueryService queryService,
            AdminAccessService accessService,
            OnsiteIssueService onsiteIssueService
    ) {
        this.queryService = queryService;
        this.accessService = accessService;
        this.onsiteIssueService = onsiteIssueService;
    }

    @GetMapping("/access")
    public Map<String, Boolean> getAccess() {
        accessService.requireCurrentAdmin();
        return Map.of("authorized", true);
    }

    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard(
            @RequestParam(defaultValue = "50") int limit
    ) {
        return queryService.getCurrentAdminDashboard(limit);
    }

    @GetMapping("/orders/{id}")
    public AdminOrderDetailsResponse getOrder(@PathVariable long id) {
        return queryService.getCurrentAdminOrder(id);
    }

    @GetMapping("/orders/{orderId}/issues/photos/{photoId}")
    public ResponseEntity<byte[]> getIssuePhoto(
            @PathVariable long orderId,
            @PathVariable long photoId
    ) {
        AdminIssuePhotoContent photo = queryService.getCurrentAdminIssuePhoto(orderId, photoId);
        byte[] content = photo.content();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.contentType()))
                .contentLength(content.length)
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .body(content);
    }

    @PostMapping("/orders/{id}/issues/resolve")
    public AdminOrderDetailsResponse resolveIssue(
            @PathVariable long id,
            @Valid @RequestBody ResolveOnsiteIssueRequest request
    ) {
        long adminTelegramUserId = accessService.requireCurrentAdmin();
        onsiteIssueService.resolve(id, adminTelegramUserId, request.resolutionComment());
        return queryService.getOrder(adminTelegramUserId, id);
    }
}
