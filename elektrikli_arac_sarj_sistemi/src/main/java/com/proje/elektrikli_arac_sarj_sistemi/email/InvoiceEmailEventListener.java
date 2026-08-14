package com.proje.elektrikli_arac_sarj_sistemi.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class InvoiceEmailEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(InvoiceEmailEventListener.class);

    private final InvoiceEmailJobService invoiceEmailJobService;

    public InvoiceEmailEventListener(
            InvoiceEmailJobService invoiceEmailJobService) {

        this.invoiceEmailJobService = invoiceEmailJobService;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleInvoiceCreated(InvoiceCreatedEvent event) {

        log.info(
                "InvoiceCreatedEvent alındı: {}",
                event.invoiceId()
        );

        invoiceEmailJobService.generatePdfAndEnqueueEmail(
                event.invoiceId()
        );
    }
}