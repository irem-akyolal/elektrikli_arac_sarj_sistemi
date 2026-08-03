package com.proje.elektrikli_arac_sarj_sistemi.service.payment;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Payment;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PaymentStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.PaymentRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.payment.PaymentResponse;
import com.proje.elektrikli_arac_sarj_sistemi.mapper.PaymentMapper;
import com.proje.elektrikli_arac_sarj_sistemi.specification.PaymentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentAdminService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentAdminService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @Transactional(readOnly = true)
    public Page<PaymentResponse> search(PaymentStatus status, String transactionId, Pageable pageable) {
        Specification<Payment> spec = Specification
                .where(PaymentSpecification.hasStatus(status))
                .and(PaymentSpecification.hasTransactionId(transactionId));

        return paymentRepository.findAll(spec, pageable)
                .map(paymentMapper::toResponse);
    }
}