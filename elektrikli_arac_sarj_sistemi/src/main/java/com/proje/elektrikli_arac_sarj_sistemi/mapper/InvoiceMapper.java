package com.proje.elektrikli_arac_sarj_sistemi.mapper;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Invoice;
import com.proje.elektrikli_arac_sarj_sistemi.dto.invoice.InvoiceResponse;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    public InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getChargingSession().getId(),
                invoice.getInvoiceNumber(),
                invoice.getSubTotal(),
                invoice.getTaxRate(),
                invoice.getTaxAmount(),
                invoice.getAmount(),
                invoice.getEmail(),
                invoice.getStatus(),
                invoice.getSentAt()
        );
    }
}