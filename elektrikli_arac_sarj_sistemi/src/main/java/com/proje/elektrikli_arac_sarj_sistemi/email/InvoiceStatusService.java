package com.proje.elektrikli_arac_sarj_sistemi.email;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Invoice;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.InvoiceStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InvoiceStatusService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceStatusService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsSent(UUID invoiceId) {

        Invoice invoice = findInvoice(invoiceId);

        invoice.setStatus(InvoiceStatus.SENT);
        invoice.setSentAt(LocalDateTime.now());

        invoiceRepository.save(invoice);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsFailed(UUID invoiceId) {

        Invoice invoice = findInvoice(invoiceId);

        invoice.setStatus(InvoiceStatus.FAILED);

        invoiceRepository.save(invoice);
    }

    private Invoice findInvoice(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Fatura bulunamadı: " + invoiceId
                        ));
    }
}
