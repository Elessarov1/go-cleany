package com.cleany.analytics;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cleany.customer.CustomerAccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/acquisition")
@RequiredArgsConstructor
public class TelegramAcquisitionController {

    private final CustomerAccountService customerAccountService;
    private final AcquisitionCaptureService captureService;

    @PostMapping("/telegram")
    public AcquisitionCaptureResponse captureTelegram(
            @Valid @RequestBody TelegramAcquisitionCaptureRequest request
    ) {
        long customerId = customerAccountService.currentCustomer().customerId();
        return captureService.captureTelegram(customerId, request.publicCode());
    }
}
