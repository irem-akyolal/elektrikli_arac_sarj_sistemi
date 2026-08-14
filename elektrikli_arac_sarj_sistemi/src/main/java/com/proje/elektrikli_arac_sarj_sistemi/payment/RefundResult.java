package com.proje.elektrikli_arac_sarj_sistemi.payment;

import java.math.BigDecimal;

public class RefundResult {

    private final boolean success;
    private final String refundReferenceId;
    private final BigDecimal refundedAmount;

    public RefundResult(
            boolean success,
            String refundReferenceId,
            BigDecimal refundedAmount) {

        this.success = success;
        this.refundReferenceId = refundReferenceId;
        this.refundedAmount = refundedAmount;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getRefundReferenceId() {
        return refundReferenceId;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }
}