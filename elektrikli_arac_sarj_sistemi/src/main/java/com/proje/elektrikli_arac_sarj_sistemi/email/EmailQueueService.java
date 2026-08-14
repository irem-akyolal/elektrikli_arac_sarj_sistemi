package com.proje.elektrikli_arac_sarj_sistemi.email;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.EmailQueue;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Invoice;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EmailQueueStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EmailQueueRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class EmailQueueService {

    private final EmailQueueRepository emailQueueRepository;
    private final InvoiceRepository invoiceRepository;
    private static final Logger log = LoggerFactory.getLogger(EmailQueueService.class);


    public EmailQueueService(
            EmailQueueRepository emailQueueRepository,
            InvoiceRepository invoiceRepository) {
        this.emailQueueRepository = emailQueueRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public void enqueueInvoiceEmail(InvoiceCreatedEvent event) {

        Invoice invoice = invoiceRepository.findById(event.invoiceId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Email kuyruğuna eklenecek fatura bulunamadı: "
                                        + event.invoiceId()
                        ));

        EmailQueue queue = new EmailQueue();

        queue.setInvoiceId(invoice.getId());
        queue.setRecipient(invoice.getEmail());
        queue.setInvoiceNumber(invoice.getInvoiceNumber());
        queue.setStatus(EmailQueueStatus.PENDING);
        queue.setAttemptCount(0);
        queue.setNextAttemptAt(LocalDateTime.now());

        

        log.info(">>> EmailQueue oluşturuluyor. Invoice: {}, Email: {}",
        invoice.getId(),
        invoice.getEmail());

        emailQueueRepository.save(queue);

        log.info(">>> EmailQueue kaydedildi. Queue ID: {}", queue.getId());
    }
}