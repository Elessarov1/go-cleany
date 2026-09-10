package com.cleany.support;

import com.cleany.action.ActionTarget;
import com.cleany.catalog.PlatformService;
import com.cleany.notification.CustomerNotification;
import com.cleany.notification.CustomerNotificationType;

public record SupportCaseAdminNotification(
        long caseId,
        PlatformService service,
        long sourceEntityId,
        SupportCaseCategory category
) implements CustomerNotification {

    @Override
    public CustomerNotificationType type() {
        return CustomerNotificationType.SUPPORT_CASE_CREATED;
    }

    @Override
    public ActionTarget action() {
        return new ActionTarget.OpenSupportCase(caseId);
    }

    @Override
    public String deduplicationKey() {
        return "support-case:" + caseId + ":created";
    }
}
