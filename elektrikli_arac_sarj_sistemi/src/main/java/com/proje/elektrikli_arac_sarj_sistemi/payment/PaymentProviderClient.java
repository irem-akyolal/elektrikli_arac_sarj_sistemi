package com.proje.elektrikli_arac_sarj_sistemi.payment;

import java.math.BigDecimal;

public interface PaymentProviderClient {

    ProvisionAuthorizationResult authorizeProvision(BigDecimal amount);

    void closeProvision(String providerReferenceId);

    CaptureResult captureAmount(String providerReferenceId, BigDecimal amount); // yeni eklenen
}