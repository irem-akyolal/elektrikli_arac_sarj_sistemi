package com.proje.elektrikli_arac_sarj_sistemi.email;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class InvoiceEmailEventListener {

    private final EmailQueueService emailQueueService;

    public InvoiceEmailEventListener(
            EmailQueueService emailQueueService) {
        this.emailQueueService = emailQueueService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInvoiceCreated(InvoiceCreatedEvent event) {

        emailQueueService.enqueueInvoiceEmail(event);
    }
}