package com.cleany.support;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/support/cases")
@RequiredArgsConstructor
public class AdminSupportController {

    private final AdminSupportService supportService;

    @GetMapping
    public AdminSupportCasePageResponse queue(
            @RequestParam(defaultValue = "OPEN") String status,
            @RequestParam(defaultValue = "ALL") String service,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return supportService.queue(status, service, page, size);
    }

    @GetMapping("/{caseId}")
    public AdminSupportCaseDetailsResponse details(@PathVariable long caseId) {
        return supportService.details(caseId);
    }

    @PostMapping("/{caseId}/resolve")
    public AdminSupportCaseDetailsResponse resolve(
            @PathVariable long caseId,
            @Valid @RequestBody ResolveSupportCaseRequest request
    ) {
        return supportService.resolve(caseId, request.resolutionComment());
    }
}
