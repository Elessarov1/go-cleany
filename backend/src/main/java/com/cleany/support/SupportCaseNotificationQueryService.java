package com.cleany.support;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupportCaseNotificationQueryService {

    private final SupportCaseRepository repository;

    @Transactional(readOnly = true)
    public SupportCaseAdminNotification created(long caseId) {
        SupportCase supportCase = repository.findById(caseId)
                .orElseThrow(() -> new SupportCaseNotFoundException(caseId));
        return new SupportCaseAdminNotification(
                supportCase.getId(),
                supportCase.getService(),
                supportCase.getSourceEntityId(),
                supportCase.getCategory()
        );
    }
}
