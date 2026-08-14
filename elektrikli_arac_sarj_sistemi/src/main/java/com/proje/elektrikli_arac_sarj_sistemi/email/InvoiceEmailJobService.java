package com.proje.elektrikli_arac_sarj_sistemi.email;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Invoice;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.InvoiceStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.InvoiceRepository;
import com.proje.elektrikli_arac_sarj_sistemi.service.invoice.InvoicePdfService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InvoiceEmailJobService {

    private final InvoiceRepository invoiceRepository;
    private final InvoicePdfService invoicePdfService;
    private final EmailQueueService emailQueueService;

    public InvoiceEmailJobService(
            InvoiceRepository invoiceRepository,
            InvoicePdfService invoicePdfService,
            EmailQueueService emailQueueService) {

        this.invoiceRepository = invoiceRepository;
        this.invoicePdfService = invoicePdfService;
        this.emailQueueService = emailQueueService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generatePdfAndEnqueueEmail(UUID invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Fatura bulunamadı: " + invoiceId
                        ));

        try {

            // 1. PDF oluştur
            String pdfPath =
                    invoicePdfService.generateInvoicePdf(invoice);

            // 2. PDF yolunu invoice'a kaydet
            invoice.setPdfPath(pdfPath);

            // 3. Invoice DB'ye kaydet
            invoiceRepository.save(invoice);

            // 4. Email kuyruğuna ekle
            emailQueueService.enqueueInvoiceEmail(
                    new InvoiceCreatedEvent(invoice.getId())
            );

        } catch (Exception ex) {

            invoice.setStatus(InvoiceStatus.FAILED);
            invoiceRepository.save(invoice);

            throw ex;
        }
    }
}