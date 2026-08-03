package com.proje.elektrikli_arac_sarj_sistemi.payment;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MockPaymentProviderClient implements PaymentProviderClient {

    @Override
    public ProvisionAuthorizationResult authorizeProvision(BigDecimal amount) {
        // Gerçek Iyzico entegrasyonu geldiğinde, bu sınıfın yerini IyzicoPaymentClient alacak.
        // Şimdilik her provizyonu otomatik onaylayacak (test/geliştirme amaçlı).
        String reference = "MOCK-PROV-" + System.currentTimeMillis();
        return new ProvisionAuthorizationResult(true, reference);
    }

    @Override
    public void closeProvision(String providerReferenceId) {
        
    }


    @Override
    public CaptureResult captureAmount(String providerReferenceId, BigDecimal amount) {
    String transactionId = "MOCK-TXN-" + System.currentTimeMillis();
    return new CaptureResult(true, transactionId);
}
}
