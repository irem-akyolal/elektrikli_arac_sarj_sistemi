package com.proje.elektrikli_arac_sarj_sistemi.mapper;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Payment;
import com.proje.elektrikli_arac_sarj_sistemi.dto.payment.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getProvision().getId(),
                payment.getAmount(),
                payment.getRefundAmount(),
                payment.getStatus(),
                payment.getProviderType(),
                payment.getTransactionId()
        );
    }
}