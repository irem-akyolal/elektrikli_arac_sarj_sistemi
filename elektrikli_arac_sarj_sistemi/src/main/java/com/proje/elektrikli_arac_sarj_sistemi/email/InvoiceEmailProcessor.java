package com.proje.elektrikli_arac_sarj_sistemi.email;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Invoice;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.InvoiceRepository;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InvoiceEmailProcessor {

    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;
    private final InvoiceStatusService invoiceStatusService;

    public InvoiceEmailProcessor(
            InvoiceRepository invoiceRepository,
            EmailService emailService,
            InvoiceStatusService invoiceStatusService) {

        this.invoiceRepository = invoiceRepository;
        this.emailService = emailService;
        this.invoiceStatusService = invoiceStatusService;
    }

    @Retryable(
            maxRetries = 3,
            delay = 2000,
            multiplier = 2
    )
    public void sendInvoice(UUID invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Email gönderilecek fatura bulunamadı: " + invoiceId
                        ));

        emailService.sendInvoiceEmail(
                invoice.getEmail(),
                invoice.getInvoiceNumber()
        );

        invoiceStatusService.markAsSent(invoiceId);
    }
}