package com.proje.elektrikli_arac_sarj_sistemi.payment;

import java.math.BigDecimal;

public interface PaymentProviderClient {

    ProvisionAuthorizationResult authorizeProvision(
            BigDecimal amount,
            PaymentCardInfo cardInfo
    );

    CaptureResult captureAmount(
            String providerReferenceId,
            BigDecimal amount
    );

    RefundResult refundAmount(
            String paymentId,
            BigDecimal amount
    );

    boolean cancelProvision(
            String providerReferenceId
    );
}