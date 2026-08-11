package com.proje.elektrikli_arac_sarj_sistemi.email;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.EmailQueue;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Invoice;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EmailQueueStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.EmailQueueRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmailQueueService {

    private final EmailQueueRepository emailQueueRepository;
    private final InvoiceRepository invoiceRepository;

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

        emailQueueRepository.save(queue);
    }
}