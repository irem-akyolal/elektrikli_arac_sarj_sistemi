package com.proje.elektrikli_arac_sarj_sistemi.email;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.EmailQueue;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EmailQueueStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EmailQueueRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailQueueProcessor {

    private static final int MAX_QUEUE_ATTEMPTS = 3;

    private final EmailQueueRepository emailQueueRepository;
    private final InvoiceEmailProcessor invoiceEmailProcessor;
    private final InvoiceStatusService invoiceStatusService;
      private static final Logger log = LoggerFactory.getLogger(EmailQueueProcessor.class);

    public EmailQueueProcessor(EmailQueueRepository emailQueueRepository,
                                InvoiceEmailProcessor invoiceEmailProcessor,
                                InvoiceStatusService invoiceStatusService) {
        this.emailQueueRepository = emailQueueRepository;
        this.invoiceEmailProcessor = invoiceEmailProcessor;
        this.invoiceStatusService = invoiceStatusService;
    }

    @Transactional
    public void process(UUID queueId) {
        EmailQueue queue = emailQueueRepository.findById(queueId)
                .orElseThrow(() -> new IllegalStateException("Email queue kaydı bulunamadı: " + queueId));

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
            invoiceStatusService.markAsSent(queue.getInvoiceId()); // artık burada çağrılıyor

       } catch (Exception e) {
              log.error("Email gönderimi başarısız - Queue ID: {}, Hata: {}", queue.getId(), e.getMessage(), e);
                queue.setLastError(e.getMessage());

             if (queue.getAttemptCount() >= MAX_QUEUE_ATTEMPTS) {
                queue.setStatus(EmailQueueStatus.FAILED);
                 invoiceStatusService.markAsFailed(queue.getInvoiceId());
             } else {
               queue.setStatus(EmailQueueStatus.PENDING);
               queue.setNextAttemptAt(LocalDateTime.now().plusMinutes(5));
          }
       }

        emailQueueRepository.save(queue);
    }
}