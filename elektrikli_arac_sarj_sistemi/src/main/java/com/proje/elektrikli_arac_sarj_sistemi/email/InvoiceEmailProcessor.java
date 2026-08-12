package com.proje.elektrikli_arac_sarj_sistemi.email;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Invoice;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InvoiceEmailProcessor {

    private final InvoiceRepository invoiceRepository;
    private final EmailService emailService;

    public InvoiceEmailProcessor(InvoiceRepository invoiceRepository, EmailService emailService) {
        this.invoiceRepository = invoiceRepository;
        this.emailService = emailService;
    }

    // Retry mantığı artık burada değil, EmailQueueProcessor'da (dakikalar arayla, daha mantıklı)
    public void sendInvoice(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalStateException(
                        "Email gönderilecek fatura bulunamadı: " + invoiceId));

        emailService.sendInvoiceEmail(invoice.getEmail(), invoice.getInvoiceNumber(), invoice.getPdfPath());
    }
}