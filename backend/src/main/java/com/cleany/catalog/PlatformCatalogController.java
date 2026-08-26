package com.cleany.catalog;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlatformCatalogController {

    private final PlatformServiceAccessService accessService;

    @GetMapping("/api/v1/catalog/services")
    public List<PlatformServiceStateResponse> customerCatalog() {
        return accessService.currentCustomerCatalog();
    }

    @GetMapping("/api/v1/admin/platform/services")
    public List<PlatformServiceStateResponse> adminStates() {
        return accessService.adminStates();
    }

    @PatchMapping("/api/v1/admin/platform/services/{service}")
    public PlatformServiceStateResponse update(
            @PathVariable PlatformService service,
            @Valid @RequestBody UpdatePlatformServiceStatusRequest request
    ) {
        return accessService.update(service, request.status());
    }
}
