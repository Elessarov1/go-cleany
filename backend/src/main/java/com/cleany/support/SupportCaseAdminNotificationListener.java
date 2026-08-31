package com.cleany.support;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.cleany.authorization.CustomerRoleRepository;
import com.cleany.authorization.PlatformRole;
import com.cleany.customer.CustomerExternalIdentityRepository;
import com.cleany.notification.CustomerNotificationDispatcher;
import com.cleany.notification.CustomerNotificationRecorder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SupportCaseAdminNotificationListener {

    private final CustomerRoleRepository roleRepository;
    private final CustomerExternalIdentityRepository identityRepository;
    private final CustomerNotificationDispatcher dispatcher;
    private final CustomerNotificationRecorder recorder;
    private final SupportCaseNotificationQueryService queryService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyAdmins(SupportCaseCreatedEvent event) {
        SupportCaseAdminNotification notification;
        try {
            notification = queryService.created(event.caseId());
        } catch (RuntimeException exception) {
            log.error("Support notification preparation failed for case {}", event.caseId(), exception);
            return;
        }
        roleRepository.findAllByRole(PlatformRole.ADMIN).stream()
                .map(role -> role.getCustomerId())
                .distinct()
                .forEach(customerId -> notifyAdmin(customerId, notification));
    }

    private void notifyAdmin(long customerId, SupportCaseAdminNotification notification) {
        var identities = identityRepository.findAllByCustomerIdOrderByProvider(customerId);
        try {
            if (identities.isEmpty()) {
                recorder.record(customerId, notification);
                return;
            }
            dispatcher.send(customerId, identities.getFirst().getId(), notification);
        } catch (RuntimeException exception) {
            log.error(
                    "Support notification failed for case {} and admin {}",
                    notification.caseId(), customerId, exception
            );
        }
    }
}
