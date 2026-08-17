package com.proje.elektrikli_arac_sarj_sistemi.service.payment;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentCapturedEventListener {

    private final RefundProcessingService refundProcessingService;

    public PaymentCapturedEventListener(
            RefundProcessingService refundProcessingService) {

        this.refundProcessingService = refundProcessingService;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handlePaymentCaptured(
            PaymentCapturedEvent event) {

        refundProcessingService.processRefund(
                event.paymentId(),
                event.transactionId(),
                event.refundAmount()
        );
    }
}