package com.proje.elektrikli_arac_sarj_sistemi.email;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.EmailQueue;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EmailQueueStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EmailQueueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailQueueProcessor {

    private static final int MAX_QUEUE_ATTEMPTS = 3;

    private final EmailQueueRepository emailQueueRepository;
    private final InvoiceEmailProcessor invoiceEmailProcessor;

    public EmailQueueProcessor(
            EmailQueueRepository emailQueueRepository,
            InvoiceEmailProcessor invoiceEmailProcessor) {

        this.emailQueueRepository = emailQueueRepository;
        this.invoiceEmailProcessor = invoiceEmailProcessor;
    }

    @Transactional
    public void process(UUID queueId) {

        EmailQueue queue = emailQueueRepository.findById(queueId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Email queue kaydı bulunamadı: " + queueId
                        ));

        if (queue.getStatus() != EmailQueueStatus.PENDING) {
            return;
        }

        queue.setStatus(EmailQueueStatus.PROCESSING);
        queue.setAttemptCount(queue.getAttemptCount() + 1);

        emailQueueRepository.save(queue);

        try {

            invoiceEmailProcessor.sendInvoice(queue.getInvoiceId());

            queue.setStatus(EmailQueueStatus.SENT);
            queue.setSentAt(LocalDateTime.now());
            queue.setLastError(null);

        } catch (Exception e) {

            queue.setLastError(e.getMessage());

            if (queue.getAttemptCount() >= MAX_QUEUE_ATTEMPTS) {

                queue.setStatus(EmailQueueStatus.FAILED);

            } else {

                queue.setStatus(EmailQueueStatus.PENDING);

                queue.setNextAttemptAt(
                        LocalDateTime.now().plusMinutes(5)
                );
            }
        }

        emailQueueRepository.save(queue);
    }
}