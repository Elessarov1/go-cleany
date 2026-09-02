package com.cleany.reminder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(
        prefix = "smart-reminders",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class SmartReminderJob {

    private static final Logger log = LoggerFactory.getLogger(SmartReminderJob.class);

    private final SmartReminderService reminderService;

    @Scheduled(cron = "${smart-reminders.cron}", zone = "${smart-reminders.zone-id}")
    public void run() {
        SmartReminderProcessingResult result = reminderService.process();
        if (result.notified() > 0 || result.superseded() > 0 || result.expired() > 0) {
            log.info(
                    "Smart reminders processed: notified={}, superseded={}, expired={}",
                    result.notified(),
                    result.superseded(),
                    result.expired()
            );
        }
    }
}
