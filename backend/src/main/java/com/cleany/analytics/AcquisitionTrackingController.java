package com.cleany.analytics;

import java.net.URI;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cleany.customer.CustomerAccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AcquisitionTrackingController {

    private final AcquisitionCaptureService captureService;
    private final CustomerAccountService customerAccountService;

    @GetMapping("/a/{publicCode}")
    public ResponseEntity<Void> capture(
            @PathVariable String publicCode,
            HttpServletRequest request,
            Authentication authentication
    ) {
        HttpSession session = request.getSession(true);
        String targetPath = captureService.captureWeb(publicCode, session);
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            long customerId = customerAccountService.currentCustomer().customerId();
            captureService.attachPending(customerId, session);
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(targetPath))
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.PRAGMA, "no-cache")
                .build();
    }
}
