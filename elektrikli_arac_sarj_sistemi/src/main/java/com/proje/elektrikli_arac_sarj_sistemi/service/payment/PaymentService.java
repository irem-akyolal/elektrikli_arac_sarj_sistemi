package com.proje.elektrikli_arac_sarj_sistemi.service.payment;

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
import com.proje.elektrikli_arac_sarj_sistemi.payment.RefundResult;
import com.proje.elektrikli_arac_sarj_sistemi.service.invoice.InvoiceService;
import com.proje.elektrikli_arac_sarj_sistemi.service.provision.ProvisionService;

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

    public PaymentService(
            PaymentRepository paymentRepository,
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

    // Asıl ödeme akışı:
    // 1. Provizyon kontrolü
    // 2. Gerçek tüketim tutarının hesaplanması
    // 3. Capture
    // 4. Fazla provizyon tutarının refund edilmesi
    // 5. Provision kapatılması
    // 6. Fatura oluşturulması
    //
    // Bu metot hem otomatik akıştan hem de admin manuel endpointinden çağrılabilir.
    @Transactional
    public PaymentResponse captureForProvision(UUID provisionId) {

        // --------------------------------------------------
        // 1. PROVİZYONU BUL
        // --------------------------------------------------

        Provision provision = provisionRepository.findById(provisionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Provizyon bulunamadı: " + provisionId
                        )
                );

        // --------------------------------------------------
        // 2. PROVİZYON DURUMUNU KONTROL ET
        // --------------------------------------------------

        if (provision.getStatus() != ProvisionStatus.APPROVED) {

            throw new BusinessRuleViolationException(
                    "INVALID_PROVISION_STATUS",
                    "Sadece APPROVED durumundaki provizyonlar tahsil edilebilir. " +
                    "Şu anki durum: " + provision.getStatus()
            );
        }

        // --------------------------------------------------
        // 3. DAHA ÖNCE ÖDEME OLUŞMUŞ MU?
        // --------------------------------------------------

        paymentRepository.findByProvisionId(provision.getId())
                .ifPresent(existing -> {

                    throw new BusinessRuleViolationException(
                            "PAYMENT_ALREADY_EXISTS",
                            "Bu provizyon için zaten bir ödeme kaydı var: "
                                    + existing.getId()
                    );
                });

        // --------------------------------------------------
        // 4. GERÇEK TÜKETİM TUTARINI HESAPLA
        // --------------------------------------------------

        BigDecimal actualAmount =
                calculateActualAmount(provision);

        BigDecimal maximumCaptureAmount =
                  provision.getRequestedAmount()
                     .multiply(BigDecimal.valueOf(1.10));

                  if (actualAmount.compareTo(maximumCaptureAmount) > 0) {
         throw new BusinessRuleViolationException(
            "CAPTURE_AMOUNT_EXCEEDED",
            "Gerçek tahsilat tutarı provizyon tutarının %10 üzerindedir. " +
            "Provizyon: " + provision.getRequestedAmount() +
            ", Maksimum tahsilat: " + maximumCaptureAmount +
            ", Gerçek tutar: " + actualAmount
         );
      }


        BigDecimal refundAmount =
                provision.getRequestedAmount()
                        .subtract(actualAmount);

        // Güvenlik açısından negatif refund oluşmasını engelle.
        if (refundAmount.compareTo(BigDecimal.ZERO) < 0) {

            throw new BusinessRuleViolationException(
                    "ACTUAL_AMOUNT_EXCEEDS_PROVISION",
                    "Gerçek tüketim tutarı provizyon tutarını aşamaz. " +
                    "Provizyon: " + provision.getRequestedAmount() +
                    ", Gerçek tutar: " + actualAmount
            );
        }

        // --------------------------------------------------
        // 5. İYZİCO CAPTURE
        // --------------------------------------------------

        CaptureResult captureResult =
                paymentProviderClient.captureAmount(
                        provision.getProviderReferenceId(),
                        actualAmount
                );

        if (!captureResult.isSuccess()) {

            throw new BusinessRuleViolationException(
                    "PAYMENT_CAPTURE_FAILED",
                    "Tahsilat ödeme kuruluşu tarafından reddedildi."
            );
        }

        // --------------------------------------------------
        // 6. PAYMENT KAYDI OLUŞTUR
        // --------------------------------------------------

        Payment payment = new Payment();

        payment.setProvision(provision);
        payment.setAmount(actualAmount);
        payment.setRefundAmount(refundAmount);

        // İlk aşamada CAPTURED.
        payment.setStatus(PaymentStatus.CAPTURED);

        payment.setProviderType(
                PaymentProviderType.IYZICO
        );

        payment.setTransactionId(
                captureResult.getTransactionId()
        );

        Payment saved =
                paymentRepository.save(payment);

        // --------------------------------------------------
        // 7. FAZLA TUTARI İADE ET
        // --------------------------------------------------

        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {

            RefundResult refundResult =
                    paymentProviderClient.refundAmount(
                            captureResult.getTransactionId(),
                            refundAmount
                    );

            if (!refundResult.isSuccess()) {

                throw new BusinessRuleViolationException(
                        "PAYMENT_REFUND_FAILED",
                        "Fazla provizyon tutarının iadesi " +
                        "ödeme kuruluşu tarafından gerçekleştirilemedi."
                );
            }

            
            saved.setStatus(
                    PaymentStatus.PARTIALLY_REFUNDED
            );

            paymentRepository.save(saved);
        }

        // --------------------------------------------------
        // 8. PROVİZYONU KAPAT
        // --------------------------------------------------

        provisionService.close(
                provision.getId()
        );

        // --------------------------------------------------
        // 9. FATURA OLUŞTUR
        // --------------------------------------------------

        invoiceService.generateForPayment(
                saved
        );

        // --------------------------------------------------
        // 10. RESPONSE
        // --------------------------------------------------

        return paymentMapper.toResponse(saved);
    }

    public PaymentResponse getById(UUID id) {

        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Ödeme bulunamadı: " + id
                                )
                        );

        return paymentMapper.toResponse(payment);
    }

    // --------------------------------------------------
    // GERÇEK TÜKETİM TUTARINI HESAPLA
    // --------------------------------------------------

    private BigDecimal calculateActualAmount(
            Provision provision) {

        var session =
                provision.getChargingSession();

        BigDecimal energyConsumed =
                session.getEnergyConsumedKwh();

        if (energyConsumed == null) {

            throw new BusinessRuleViolationException(
                    "ENERGY_NOT_RECORDED",
                    "Şarj oturumunun tüketim bilgisi henüz kaydedilmemiş."
            );
        }

        Connector connector =
                session.getConnector();

        return energyConsumed.multiply(
                connector.getUnitPrice()
        );
    }
}