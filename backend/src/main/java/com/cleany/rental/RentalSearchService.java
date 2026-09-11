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
import com.cleany.pagination.InvalidCursorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalSearchService {

    static final int DEFAULT_PAGE_SIZE = 20;
    static final int MAX_PAGE_SIZE = 20;

    private final RentalSearchRepository repository;
    private final RentalStayPolicy stayPolicy;
    private final RentalPriceService priceService;
    private final PlatformServiceAccessService serviceAccessService;
    private final RentalSearchTrackingService trackingService;
    private final RentalSearchCursorCodec cursorCodec;
    private final Clock clock;

    public RentalSearchResponse search(
            RentalTermType termType,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer months,
            Integer guests,
            String encodedCursor,
            Integer requestedSize,
            UUID previousSearchId
    ) {
        long startedAt = System.nanoTime();
        serviceAccessService.requireCanStartCurrentCustomerFlow(PlatformService.RENTAL);
        int size = resolveSize(requestedSize);
        if (encodedCursor != null && previousSearchId != null) {
            throw new InvalidCursorException();
        }
        RentalSearchCriteriaResponse criteria;
        ResolvedRentalTerm term;
        if (termType == null) {
            requireBrowseAll(checkInDate, checkOutDate, months, guests);
            criteria = RentalSearchCriteriaResponse.browseAll();
            term = null;
        } else {
            if (guests == null || guests <= 0 || guests > 100) {
                throw new InvalidRentalBookingException("Guests must be between 1 and 100");
            }
            term = stayPolicy.resolve(
                    termType,
                    checkInDate,
                    checkOutDate,
                    months
            );
            criteria = RentalSearchCriteriaResponse.from(term, guests);
        }

        String fingerprint = cursorCodec.criteriaFingerprint(criteria);
        RentalSearchCursor cursor = encodedCursor == null ? null : cursorCodec.decode(encodedCursor);
        if (cursor != null && !fingerprint.equals(cursor.criteriaFingerprint())) {
            throw new InvalidCursorException();
        }
        Integer afterDisplayOrder = cursor == null ? null : cursor.displayOrder();
        Long afterPropertyId = cursor == null ? null : cursor.propertyId();
        List<RentalSearchPropertyRow> fetched = term == null
                ? repository.findPublished(afterDisplayOrder, afterPropertyId, size + 1)
                : repository.findAvailable(
                        term.checkInDate(),
                        term.checkOutDate(),
                        guests,
                        afterDisplayOrder,
                        afterPropertyId,
                        size + 1
                );
        boolean hasMore = fetched.size() > size;
        List<RentalSearchPropertyRow> properties = hasMore
                ? fetched.subList(0, size)
                : fetched;
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
        UUID executionId = cursor == null
                ? trackingService.recordExecutionSafely(
                        criteria.mode(),
                        responses.size(),
                        durationMs,
                        previousSearchId,
                        calculatedAt
                )
                : cursor.searchExecutionId();
        String nextCursor = hasMore
                ? cursorCodec.encode(new RentalSearchCursor(
                        executionId,
                        fingerprint,
                        properties.getLast().displayOrder(),
                        properties.getLast().id()
                ))
                : null;
        return new RentalSearchResponse(
                executionId,
                criteria,
                calculatedAt,
                responses,
                nextCursor,
                hasMore
        );
    }

    private static int resolveSize(Integer requestedSize) {
        int size = requestedSize == null ? DEFAULT_PAGE_SIZE : requestedSize;
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidCursorException();
        }
        return size;
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
