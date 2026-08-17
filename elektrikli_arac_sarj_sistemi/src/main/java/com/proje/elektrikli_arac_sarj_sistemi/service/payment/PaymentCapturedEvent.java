package com.proje.elektrikli_arac_sarj_sistemi.service.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCapturedEvent(
        UUID paymentId,
        String transactionId,
        BigDecimal refundAmount
) {
}