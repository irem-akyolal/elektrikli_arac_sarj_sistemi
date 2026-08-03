package com.proje.elektrikli_arac_sarj_sistemi.dto.invoice;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class InvoiceResponse {

    private UUID id;
    private UUID chargingSessionId;
    private String invoiceNumber;

    private BigDecimal subTotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal amount;

    private String email;

    private String pdfPath;

    private InvoiceStatus status;

    private LocalDateTime sentAt;

    public InvoiceResponse(
            UUID id,
            UUID chargingSessionId,
            String invoiceNumber,
            BigDecimal subTotal,
            BigDecimal taxRate,
            BigDecimal taxAmount,
            BigDecimal amount,
            String email,
            String pdfPath,
            InvoiceStatus status,
            LocalDateTime sentAt
    ) {
        this.id = id;
        this.chargingSessionId = chargingSessionId;
        this.invoiceNumber = invoiceNumber;
        this.subTotal = subTotal;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.amount = amount;
        this.email = email;
        this.pdfPath = pdfPath;
        this.status = status;
        this.sentAt = sentAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getChargingSessionId() {
        return chargingSessionId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
}