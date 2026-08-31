package com.cleany.support;

import java.time.Clock;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cleany.admin.AdminAccessService;
import com.cleany.catalog.PlatformService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminSupportService {

    private final SupportCaseRepository supportCaseRepository;
    private final SupportSourceResolver sourceResolver;
    private final AdminAccessService accessService;
    private final Clock clock;

    @Transactional(readOnly = true)
    public AdminSupportCasePageResponse queue(
            String statusValue,
            String serviceValue,
            int page,
            int size
    ) {
        accessService.requireCurrentAdmin();
        if (page < 0 || size < 1 || size > 100) {
            throw new InvalidSupportRequestException("Invalid support queue pagination");
        }
        SupportCaseStatus status = parseStatus(statusValue);
        PlatformService service = parseService(serviceValue);
        var result = supportCaseRepository.findQueue(status, service, PageRequest.of(page, size));
        var content = result.getContent().stream()
                .map(supportCase -> AdminSupportCaseSummaryResponse.from(
                        supportCase,
                        sourceResolver.requireOwned(
                                supportCase.getService(),
                                supportCase.getSourceEntityId(),
                                supportCase.getCustomerId()
                        )
                ))
                .toList();
        return new AdminSupportCasePageResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public AdminSupportCaseDetailsResponse details(long caseId) {
        accessService.requireCurrentAdmin();
        SupportCase supportCase = requireCase(caseId);
        return details(supportCase);
    }

    @Transactional
    public AdminSupportCaseDetailsResponse resolve(long caseId, String resolutionComment) {
        long adminCustomerId = accessService.requireCurrentAdmin();
        SupportCase supportCase = supportCaseRepository.findByIdForUpdate(caseId)
                .orElseThrow(() -> new SupportCaseNotFoundException(caseId));
        supportCase.resolve(adminCustomerId, resolutionComment, clock.instant());
        supportCaseRepository.flush();
        return details(supportCase);
    }

    private AdminSupportCaseDetailsResponse details(SupportCase supportCase) {
        SupportSource source = sourceResolver.requireOwned(
                supportCase.getService(),
                supportCase.getSourceEntityId(),
                supportCase.getCustomerId()
        );
        return AdminSupportCaseDetailsResponse.from(supportCase, source);
    }

    private SupportCase requireCase(long caseId) {
        return supportCaseRepository.findById(caseId)
                .orElseThrow(() -> new SupportCaseNotFoundException(caseId));
    }

    private static SupportCaseStatus parseStatus(String value) {
        if (value == null || value.isBlank() || "OPEN".equalsIgnoreCase(value)) {
            return SupportCaseStatus.OPEN;
        }
        if ("ALL".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return SupportCaseStatus.valueOf(value.toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new InvalidSupportRequestException("Unknown support case status");
        }
    }

    private static PlatformService parseService(String value) {
        if (value == null || value.isBlank() || "ALL".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return PlatformService.valueOf(value.toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new InvalidSupportRequestException("Unknown support service");
        }
    }
}
