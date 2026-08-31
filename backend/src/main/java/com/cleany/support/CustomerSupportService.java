package com.cleany.support;

import static com.cleany.common.text.TextValues.normalizeOptional;

import java.time.Clock;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.catalog.PlatformService;
import com.cleany.customer.CurrentCustomer;
import com.cleany.customer.CustomerAccountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerSupportService {

    private final SupportCaseRepository supportCaseRepository;
    private final TransactionFeedbackRepository feedbackRepository;
    private final SupportSourceResolver sourceResolver;
    private final CustomerAccountService customerAccountService;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional(readOnly = true)
    public TransactionSupportResponse source(PlatformService service, long sourceEntityId) {
        return source(customerAccountService.currentCustomer(), service, sourceEntityId);
    }

    @Transactional(readOnly = true)
    public TransactionSupportResponse source(
            CurrentCustomer customer,
            PlatformService service,
            long sourceEntityId
    ) {
        SupportSource source = sourceResolver.requireOwned(service, sourceEntityId, customer.customerId());
        return response(source);
    }

    @Transactional
    public SupportCaseCreationResult createCase(CreateSupportCaseRequest request) {
        return createCase(customerAccountService.currentCustomer(), request);
    }

    @Transactional
    public SupportCaseCreationResult createCase(
            CurrentCustomer customer,
            CreateSupportCaseRequest request
    ) {
        SupportSource source = sourceResolver.requireOwned(
                request.service(), request.sourceEntityId(), customer.customerId()
        );
        customerAccountService.lock(customer.customerId());
        OpenCaseResult result = openOrReuse(source, request.category(), request.description());
        return new SupportCaseCreationResult(SupportCaseResponse.from(result.supportCase()), result.created());
    }

    @Transactional
    public TransactionSupportResponse submitFeedback(CreateTransactionFeedbackRequest request) {
        CurrentCustomer customer = customerAccountService.currentCustomer();
        SupportSource source = sourceResolver.requireOwned(
                request.service(), request.sourceEntityId(), customer.customerId()
        );
        if (!source.completed()) {
            throw new InvalidSupportRequestException("Feedback is available only for completed transactions");
        }
        validateFeedback(request);
        customerAccountService.lock(customer.customerId());
        var existing = feedbackRepository.findByCustomerIdAndServiceAndSourceEntityId(
                customer.customerId(), request.service(), request.sourceEntityId()
        );
        if (existing.isPresent()) {
            if (sameFeedback(existing.get(), request)) {
                return response(source);
            }
            throw new FeedbackAlreadySubmittedException();
        }

        SupportCase supportCase = null;
        if (request.outcome() == FeedbackOutcome.PROBLEM) {
            supportCase = openOrReuse(source, request.category(), request.comment()).supportCase();
        }
        feedbackRepository.saveAndFlush(new TransactionFeedback(
                customer.customerId(),
                request.service(),
                request.sourceEntityId(),
                request.outcome(),
                request.category(),
                request.comment(),
                supportCase,
                clock.instant()
        ));
        return response(source);
    }

    private TransactionSupportResponse response(SupportSource source) {
        TransactionFeedbackResponse feedback = feedbackRepository
                .findByCustomerIdAndServiceAndSourceEntityId(
                        source.customerId(), source.service(), source.sourceEntityId()
                )
                .map(TransactionFeedbackResponse::from)
                .orElse(null);
        SupportCaseResponse latestCase = supportCaseRepository
                .findFirstByCustomerIdAndServiceAndSourceEntityIdOrderByCreatedAtDescIdDesc(
                        source.customerId(), source.service(), source.sourceEntityId()
                )
                .map(SupportCaseResponse::from)
                .orElse(null);
        return new TransactionSupportResponse(
                source.service(),
                source.sourceEntityId(),
                source.completed() && feedback == null,
                feedback,
                latestCase
        );
    }

    private OpenCaseResult openOrReuse(
            SupportSource source,
            SupportCaseCategory category,
            String description
    ) {
        var existing = supportCaseRepository.findByCustomerIdAndServiceAndSourceEntityIdAndStatus(
                source.customerId(), source.service(), source.sourceEntityId(), SupportCaseStatus.OPEN
        );
        if (existing.isPresent()) {
            return new OpenCaseResult(existing.get(), false);
        }
        SupportCase supportCase = supportCaseRepository.saveAndFlush(new SupportCase(
                source.customerId(),
                source.service(),
                source.sourceEntityId(),
                category,
                description,
                clock.instant()
        ));
        eventPublisher.publishEvent(new SupportCaseCreatedEvent(supportCase.getId()));
        return new OpenCaseResult(supportCase, true);
    }

    private static void validateFeedback(CreateTransactionFeedbackRequest request) {
        if (request.outcome() == FeedbackOutcome.GOOD) {
            if (request.category() != null || normalizeOptional(request.comment()) != null) {
                throw new InvalidSupportRequestException("Good feedback cannot contain problem details");
            }
            return;
        }
        if (request.category() == null) {
            throw new InvalidSupportRequestException("Problem feedback requires a category");
        }
    }

    private static boolean sameFeedback(
            TransactionFeedback existing,
            CreateTransactionFeedbackRequest request
    ) {
        return existing.getOutcome() == request.outcome()
                && existing.getCategory() == request.category()
                && Objects.equals(existing.getComment(), normalizeOptional(request.comment()));
    }

    private record OpenCaseResult(SupportCase supportCase, boolean created) {
    }
}
