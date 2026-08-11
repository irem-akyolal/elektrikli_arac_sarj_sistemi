package com.proje.elektrikli_arac_sarj_sistemi.email;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.EmailQueue;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EmailQueueStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EmailQueueRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EmailQueueScheduler {

    private final EmailQueueRepository emailQueueRepository;
    private final EmailQueueProcessor emailQueueProcessor;

    public EmailQueueScheduler(
            EmailQueueRepository emailQueueRepository,
            EmailQueueProcessor emailQueueProcessor) {

        this.emailQueueRepository = emailQueueRepository;
        this.emailQueueProcessor = emailQueueProcessor;
    }

    @Scheduled(fixedDelayString = "${email.queue.fixed-delay:5000}")
    public void processEmailQueue() {

        LocalDateTime now = LocalDateTime.now();

        List<EmailQueue> pendingEmails =
                emailQueueRepository
                        .findTop20ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                                EmailQueueStatus.PENDING,
                                now
                        );

        for (EmailQueue emailQueue : pendingEmails) {
            emailQueueProcessor.process(emailQueue.getId());
        }
    }
}