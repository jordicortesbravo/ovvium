package com.ovvium.services.app.config.schedulers;

import com.ovvium.services.model.user.event.UserVerificationReminderEvent;
import com.ovvium.services.service.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final EventPublisherService eventPublisherService;

    @Scheduled(cron = "${scheduled.sendVerifyReminderEmails.cron}")
    public void sendVerifyReminderEmails() {
        eventPublisherService.emit(new UserVerificationReminderEvent());
    }

}
