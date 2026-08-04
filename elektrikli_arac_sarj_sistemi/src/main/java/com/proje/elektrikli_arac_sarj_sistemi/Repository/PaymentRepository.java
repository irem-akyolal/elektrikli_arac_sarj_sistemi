package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Payment;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByProvisionId(UUID provisionId);

    Optional<Payment> findByTransactionId(String transactionId);

    // Admin panel — Ödeme İşlemleri ekranı
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'CAPTURED' AND p.createdAt >= :startOfDay")
    BigDecimal sumRevenueSince(@Param("startOfDay") LocalDateTime startOfDay);

     @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'CAPTURED'")
     BigDecimal sumTotalRevenue();
}