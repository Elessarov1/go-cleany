package com.cleany.admin;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminQueryService queryService;
    private final AdminAccessService accessService;

    public AdminController(AdminQueryService queryService, AdminAccessService accessService) {
        this.queryService = queryService;
        this.accessService = accessService;
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
}
