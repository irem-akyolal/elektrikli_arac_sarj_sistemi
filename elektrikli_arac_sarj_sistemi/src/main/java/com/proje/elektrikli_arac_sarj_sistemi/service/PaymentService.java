package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Payment;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Provision;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PaymentProviderType;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PaymentStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ProvisionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.PaymentRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ProvisionRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.payment.PaymentResponse;
import com.proje.elektrikli_arac_sarj_sistemi.exception.BusinessRuleViolationException;
import com.proje.elektrikli_arac_sarj_sistemi.exception.ResourceNotFoundException;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.PaymentMapper;
import com.proje.elektrikli_arac_sarj_sistemi.payment.CaptureResult;
import com.proje.elektrikli_arac_sarj_sistemi.payment.PaymentProviderClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProvisionRepository provisionRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentProviderClient paymentProviderClient;
    private final ProvisionService provisionService;
    private final InvoiceService invoiceService; 

    public PaymentService(PaymentRepository paymentRepository,
                           ProvisionRepository provisionRepository,
                           PaymentMapper paymentMapper,
                           PaymentProviderClient paymentProviderClient,
                           ProvisionService provisionService,
                           InvoiceService invoiceService) {
        this.paymentRepository = paymentRepository;
        this.provisionRepository = provisionRepository;
        this.paymentMapper = paymentMapper;
        this.paymentProviderClient = paymentProviderClient;
        this.provisionService = provisionService;
        this.invoiceService = invoiceService;
    }

    // Asıl iş mantığı burada — hem otomatik akış hem admin manuel endpoint bunu çağırır
    @Transactional
    public PaymentResponse captureForProvision(UUID provisionId) {
        Provision provision = provisionRepository.findById(provisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Provizyon bulunamadı: " + provisionId));

        if (provision.getStatus() != ProvisionStatus.APPROVED) {
            throw new BusinessRuleViolationException(
                    "INVALID_PROVISION_STATUS",
                    "Sadece APPROVED durumundaki provizyonlar tahsil edilebilir. Şu anki durum: " + provision.getStatus()
            );
        }

        paymentRepository.findByProvisionId(provision.getId()).ifPresent(existing -> {
            throw new BusinessRuleViolationException(
                    "PAYMENT_ALREADY_EXISTS",
                    "Bu provizyon için zaten bir ödeme kaydı var: " + existing.getId()
            );
        });

        BigDecimal actualAmount = calculateActualAmount(provision);
        BigDecimal refundAmount = provision.getRequestedAmount().subtract(actualAmount);

        CaptureResult captureResult = paymentProviderClient.captureAmount(
                provision.getProviderReferenceId(), actualAmount);

        if (!captureResult.isSuccess()) {
            throw new BusinessRuleViolationException(
                    "PAYMENT_CAPTURE_FAILED",
                    "Tahsilat ödeme kuruluşu tarafından reddedildi."
            );
        }

        Payment payment = new Payment();
        payment.setProvision(provision);
        payment.setAmount(actualAmount);
        payment.setRefundAmount(refundAmount);
        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setProviderType(PaymentProviderType.IYZICO);
        payment.setTransactionId(captureResult.getTransactionId());

        Payment saved = paymentRepository.save(payment);

        // Tahsilat başarılı olduğuna göre, provizyonu da kapat
        provisionService.close(provision.getId());

        invoiceService.generateForPayment(saved);

        return paymentMapper.toResponse(saved);
    }

    public PaymentResponse getById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ödeme bulunamadı: " + id));
        return paymentMapper.toResponse(payment);
    }

    private BigDecimal calculateActualAmount(Provision provision) {
        var session = provision.getChargingSession();
        BigDecimal energyConsumed = session.getEnergyConsumedKwh();

        if (energyConsumed == null) {
            throw new BusinessRuleViolationException(
                    "ENERGY_NOT_RECORDED",
                    "Şarj oturumunun tüketim bilgisi henüz kaydedilmemiş."
            );
        }

        Connector connector = session.getConnector();
        return energyConsumed.multiply(connector.getUnitPrice());
    }
}