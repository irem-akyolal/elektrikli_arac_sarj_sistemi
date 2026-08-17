package com.proje.elektrikli_arac_sarj_sistemi.service.payment;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Payment;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PaymentStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.PaymentRepository;
import com.proje.elektrikli_arac_sarj_sistemi.payment.PaymentProviderClient;
import com.proje.elektrikli_arac_sarj_sistemi.payment.RefundResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class RefundProcessingService {

    private static final Logger log = LoggerFactory.getLogger(RefundProcessingService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentProviderClient paymentProviderClient;

    public RefundProcessingService(PaymentRepository paymentRepository, PaymentProviderClient paymentProviderClient) {
        this.paymentRepository = paymentRepository;
        this.paymentProviderClient = paymentProviderClient;
    }

    // KENDİ bağımsız transaction'ı — burada hata olsa bile capture'ın Payment kaydı etkilenmez
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processRefund(UUID paymentId, String transactionId, BigDecimal refundAmount) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("Payment bulunamadı: " + paymentId));

        try {
            RefundResult refundResult = paymentProviderClient.refundAmount(transactionId, refundAmount);

            if (refundResult.isSuccess()) {
                payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
            } else {
                log.error("Refund başarısız oldu - Payment: {}, Tutar: {}", paymentId, refundAmount);
                payment.setStatus(PaymentStatus.REFUND_FAILED); // yeni bir enum değeri gerekecek
            }
        } catch (Exception ex) {
            log.error("Refund işlemi sırasında hata - Payment: {}, Hata: {}", paymentId, ex.getMessage(), ex);
            payment.setStatus(PaymentStatus.REFUND_FAILED);
        }

        paymentRepository.save(payment);
    }
}
