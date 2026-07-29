package com.proje.elektrikli_arac_sarj_sistemi.Repository;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.ChargingSession;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChargingSessionRepository extends JpaRepository<ChargingSession, UUID> {

    Optional<ChargingSession> findByOcpiSessionId(String ocpiSessionId);

    // Scheduler için: şu an aktif (devam eden) tüm session'ları bulmak
    List<ChargingSession> findByStatus(SessionStatus status);

    List<ChargingSession> findByConnectorId(UUID connectorId);

    // Admin panel — durum bazlı, sayfalı listeleme
    Page<ChargingSession> findByStatus(SessionStatus status, Pageable pageable);

    // Admin panel — tarih aralığına göre filtreleme (örn. "bugünkü işlemler")
    Page<ChargingSession> findByStartedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Admin panel — email/plaka arama, sayfalı
    Page<ChargingSession> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<ChargingSession> findByPlateNumberContainingIgnoreCase(String plateNumber, Pageable pageable);

    // Genel listeleme (filtre yokken) — zaten JpaRepository'den findAll(Pageable) geliyor, ayrıca yazmana gerek yok
}