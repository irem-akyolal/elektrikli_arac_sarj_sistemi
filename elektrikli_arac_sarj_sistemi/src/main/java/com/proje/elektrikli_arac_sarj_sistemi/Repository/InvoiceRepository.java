package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByChargingSessionId(UUID chargingSessionId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

     boolean existsByInvoiceNumber(String invoiceNumber);
}     