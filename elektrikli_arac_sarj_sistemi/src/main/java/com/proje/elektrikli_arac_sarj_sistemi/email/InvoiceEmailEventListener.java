package com.proje.elektrikli_arac_sarj_sistemi.email;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class InvoiceEmailEventListener {

    private final InvoiceEmailProcessor invoiceEmailProcessor;
    private final InvoiceStatusService invoiceStatusService;

    public InvoiceEmailEventListener(
            InvoiceEmailProcessor invoiceEmailProcessor,
            InvoiceStatusService invoiceStatusService) {

        this.invoiceEmailProcessor = invoiceEmailProcessor;
        this.invoiceStatusService = invoiceStatusService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInvoiceCreated(InvoiceCreatedEvent event) {

        try {

            invoiceEmailProcessor.sendInvoice(event.invoiceId());

        } catch (Exception e) {

            invoiceStatusService.markAsFailed(event.invoiceId());

            System.err.println(
                    "Fatura e-posta gönderimi başarısız oldu. Invoice ID: "
                            + event.invoiceId()
                            + " Hata: "
                            + e.getMessage()
            );
        }
    }
}