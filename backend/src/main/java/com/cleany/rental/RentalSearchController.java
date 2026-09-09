package com.cleany.rental;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rental")
@RequiredArgsConstructor
public class RentalSearchController {

    static final String PREVIOUS_SEARCH_HEADER = "X-Rental-Previous-Search-Id";

    private final RentalSearchService searchService;
    private final RentalSearchTrackingService trackingService;

    @GetMapping("/search")
    public ResponseEntity<RentalSearchResponse> search(
            @RequestParam(required = false) RentalTermType termType,
            @RequestParam(required = false) LocalDate checkInDate,
            @RequestParam(required = false) LocalDate checkOutDate,
            @RequestParam(required = false) Integer months,
            @RequestParam(required = false) Integer guests,
            @RequestHeader(name = PREVIOUS_SEARCH_HEADER, required = false) UUID previousSearchId
    ) {
        RentalSearchResponse response = searchService.search(
                termType,
                checkInDate,
                checkOutDate,
                months,
                guests,
                previousSearchId
        );
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(response);
    }

    @PostMapping("/searches/{executionId}/opened")
    public ResponseEntity<Void> opened(@PathVariable UUID executionId) {
        trackingService.recordOpenedSafely(executionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/searches/{executionId}/first-card")
    public ResponseEntity<Void> firstCard(
            @PathVariable UUID executionId,
            @Valid @RequestBody RentalFirstCardRequest request
    ) {
        trackingService.recordFirstCardSafely(executionId, request.durationMs());
        return ResponseEntity.noContent().build();
    }
}
