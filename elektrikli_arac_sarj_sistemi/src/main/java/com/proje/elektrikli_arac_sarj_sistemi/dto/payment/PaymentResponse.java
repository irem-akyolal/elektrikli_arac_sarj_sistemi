package com.proje.elektrikli_arac_sarj_sistemi.dto.payment;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PaymentProviderType;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentResponse {

    private UUID id;
    private UUID provisionId;
    private BigDecimal amount;
    private BigDecimal refundAmount;
    private PaymentStatus status;
    private PaymentProviderType providerType;
    private String transactionId;

    public PaymentResponse(UUID id, UUID provisionId, BigDecimal amount, BigDecimal refundAmount,
                            PaymentStatus status, PaymentProviderType providerType, String transactionId) {
        this.id = id;
        this.provisionId = provisionId;
        this.amount = amount;
        this.refundAmount = refundAmount;
        this.status = status;
        this.providerType = providerType;
        this.transactionId = transactionId;
    }

    public UUID getId() { return id; }
    public UUID getProvisionId() { return provisionId; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public PaymentStatus getStatus() { return status; }
    public PaymentProviderType getProviderType() { return providerType; }
    public String getTransactionId() { return transactionId; }
}