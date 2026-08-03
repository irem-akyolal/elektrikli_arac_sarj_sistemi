package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.ChargingSession;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Invoice;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Payment;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.InvoiceStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.InvoiceRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.invoice.InvoiceResponse;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.InvoiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class InvoiceService {

    // Şimdilik sabit — ileride SystemParameter'dan okunacak (Faz 3'te)
    private static final BigDecimal TAX_RATE = new BigDecimal("0.20");

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    // Otomatik akıştan çağrılıyor — Payment başarılı olunca tetikleniyor
    @Transactional
    public InvoiceResponse generateForPayment(Payment payment) {
        ChargingSession session = payment.getProvision().getChargingSession();

        invoiceRepository.findByChargingSessionId(session.getId()).ifPresent(existing -> {
            throw new IllegalStateException(
                    "Bu oturum için zaten bir fatura üretilmiş: " + existing.getId());
        });

        BigDecimal totalAmount = payment.getAmount();
        BigDecimal subTotal = totalAmount.divide(BigDecimal.ONE.add(TAX_RATE), 2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = totalAmount.subtract(subTotal);

        Invoice invoice = new Invoice();
        invoice.setChargingSession(session);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setSubTotal(subTotal);
        invoice.setTaxRate(TAX_RATE);
        invoice.setTaxAmount(taxAmount);
        invoice.setAmount(totalAmount);
        invoice.setEmail(session.getEmail());
        invoice.setStatus(InvoiceStatus.CREATED);

        Invoice saved = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(saved);
    }

    public InvoiceResponse getById(UUID id) {
        Invoice invoice = findInvoice(id);
        return invoiceMapper.toResponse(invoice);
    }

    public InvoiceResponse getByChargingSessionId(UUID chargingSessionId) {
        Invoice invoice = invoiceRepository.findByChargingSessionId(chargingSessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bu oturum için fatura bulunamadı: " + chargingSessionId));
        return invoiceMapper.toResponse(invoice);
    }

    // ============================
    // Private Methods
    // ============================

    private String generateInvoiceNumber() {
        String candidate;
        do {
            candidate = "INV-" + System.currentTimeMillis();
        } while (invoiceRepository.existsByInvoiceNumber(candidate));
        return candidate;
    }

    private Invoice findInvoice(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura bulunamadı: " + id));
    }
}
