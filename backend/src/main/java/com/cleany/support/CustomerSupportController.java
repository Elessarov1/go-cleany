package com.cleany.support;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cleany.catalog.PlatformService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/account/support")
@RequiredArgsConstructor
public class CustomerSupportController {

    private final CustomerSupportService supportService;

    @GetMapping("/sources/{service}/{sourceEntityId}")
    public TransactionSupportResponse source(
            @PathVariable PlatformService service,
            @PathVariable long sourceEntityId
    ) {
        return supportService.source(service, sourceEntityId);
    }

    @PostMapping("/cases")
    public ResponseEntity<SupportCaseResponse> createCase(
            @Valid @RequestBody CreateSupportCaseRequest request
    ) {
        SupportCaseCreationResult result = supportService.createCase(request);
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(result.supportCase());
    }

    @PostMapping("/feedback")
    public TransactionSupportResponse submitFeedback(
            @Valid @RequestBody CreateTransactionFeedbackRequest request
    ) {
        return supportService.submitFeedback(request);
    }
}
