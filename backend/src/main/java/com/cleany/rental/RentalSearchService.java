package com.cleany.rental;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cleany.catalog.PlatformService;
import com.cleany.catalog.PlatformServiceAccessService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalSearchService {

    private final RentalSearchRepository repository;
    private final RentalStayPolicy stayPolicy;
    private final RentalPriceService priceService;
    private final PlatformServiceAccessService serviceAccessService;
    private final RentalSearchTrackingService trackingService;
    private final Clock clock;

    public RentalSearchResponse search(
            RentalTermType termType,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer months,
            Integer guests,
            UUID previousSearchId
    ) {
        long startedAt = System.nanoTime();
        serviceAccessService.requireCanStartCurrentCustomerFlow(PlatformService.RENTAL);
        if (termType == null) {
            requireBrowseAll(checkInDate, checkOutDate, months, guests);
            List<RentalSearchPropertyRow> properties = repository.findPublished();
            return response(
                    RentalSearchCriteriaResponse.browseAll(),
                    properties,
                    null,
                    startedAt,
                    previousSearchId
            );
        }
        if (guests == null || guests <= 0 || guests > 100) {
            throw new InvalidRentalBookingException("Guests must be between 1 and 100");
        }
        ResolvedRentalTerm term = stayPolicy.resolve(
                termType,
                checkInDate,
                checkOutDate,
                months
        );
        List<RentalSearchPropertyRow> properties = repository.findAvailable(
                term.checkInDate(),
                term.checkOutDate(),
                guests
        );
        return response(
                RentalSearchCriteriaResponse.from(term, guests),
                properties,
                term,
                startedAt,
                previousSearchId
        );
    }

    private RentalSearchResponse response(
            RentalSearchCriteriaResponse criteria,
            List<RentalSearchPropertyRow> properties,
            ResolvedRentalTerm term,
            long startedAt,
            UUID previousSearchId
    ) {
        Map<Long, String> covers = new HashMap<>();
        repository.findCovers(properties.stream().map(RentalSearchPropertyRow::id).toList())
                .forEach(cover -> covers.putIfAbsent(cover.propertyId(), cover.cardUrl()));
        List<RentalSearchPropertyResponse> responses = properties.stream()
                .map(property -> RentalSearchPropertyResponse.from(
                        property,
                        covers.get(property.id()),
                        term == null ? null : priceService.calculate(
                                new RentalPriceInput(
                                        property.baseDailyPrice(),
                                        property.currency()
                                ),
                                term
                        )
                ))
                .toList();
        var calculatedAt = clock.instant();
        long durationMs = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
        UUID executionId = trackingService.recordExecutionSafely(
                criteria.mode(),
                responses.size(),
                durationMs,
                previousSearchId,
                calculatedAt
        );
        return new RentalSearchResponse(executionId, criteria, calculatedAt, responses);
    }

    private static void requireBrowseAll(
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer months,
            Integer guests
    ) {
        if (checkInDate != null || checkOutDate != null || months != null || guests != null) {
            throw new InvalidRentalBookingException(
                    "Browse-all search must not contain rental criteria"
            );
        }
    }
}
