package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.ChargingSession;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Provision;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ProvisionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ChargingSessionRepository;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.ProvisionRepository;
import com.proje.elektrikli_arac_sarj_sistemi.service.OcpiLocationSyncService;
import com.proje.elektrikli_arac_sarj_sistemi.service.session.ChargingSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final ChargingSessionRepository chargingSessionRepository;
    private final ProvisionRepository provisionRepository;
    private final ChargingSessionService chargingSessionService;
    private final OcpiLocationSyncService ocpiLocationSyncService; // ← EKLE!

    // ============================
    // 1. Zaman Aşımı Kontrolü (Her 5 dakikada bir)
    // ============================
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void checkSessionTimeouts() {
        log.info("Zaman aşımı kontrolü başlatıldı: {}", LocalDateTime.now());

        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(30);

        List<ChargingSession> expiredSessions = chargingSessionRepository
                .findByStatusAndStartedAtBefore(SessionStatus.CHARGING, timeoutThreshold);

        if (expiredSessions.isEmpty()) {
            log.info("Zaman aşımına uğramış oturum bulunamadı.");
            return;
        }

        log.info("{} oturum zaman aşımına uğradı, sonlandırılıyor...", expiredSessions.size());

        for (ChargingSession session : expiredSessions) {
            try {
                chargingSessionService.completeSession(session.getId(), java.math.BigDecimal.ZERO);
                log.info("Oturum sonlandırıldı: {}", session.getId());
            } catch (Exception e) {
                log.error("Oturum sonlandırılırken hata oluştu: {}, Hata: {}", session.getId(), e.getMessage());
            }
        }
    }

    // ============================
    // 2. Açık Provizyonları Kontrol Et (Her 10 dakikada bir)
    // ============================
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void cleanupPendingProvisions() {
        log.info("Açık provizyon temizliği başlatıldı: {}", LocalDateTime.now());

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);

        List<Provision> pendingProvisions = provisionRepository
                .findByStatusAndCreatedAtBefore(ProvisionStatus.PENDING, threshold);

        if (pendingProvisions.isEmpty()) {
            log.info("Temizlenecek provizyon bulunamadı.");
            return;
        }

        log.info("{} provizyon temizleniyor...", pendingProvisions.size());

        for (Provision provision : pendingProvisions) {
            try {
                provision.setStatus(ProvisionStatus.FAILED);
                provisionRepository.save(provision);
                log.info("Provizyon temizlendi: {}", provision.getId());
            } catch (Exception e) {
                log.error("Provizyon temizlenirken hata oluştu: {}, Hata: {}", provision.getId(), e.getMessage());
            }
        }
    }

    // ============================
    // 3. OCPI Senkronizasyonu (Her 30 dakikada bir)
    // ============================
    @Scheduled(fixedRate = 60000)
    public void syncOcpiLocations() {
        log.info("OCPI senkronizasyonu başlatıldı: {}", LocalDateTime.now());

        try {
            ocpiLocationSyncService.syncLocations(); // 
            log.info("OCPI senkronizasyonu tamamlandı.");
        } catch (Exception e) {
            log.error("OCPI senkronizasyonu sırasında hata oluştu: {}", e.getMessage());
        }
    }

    // ============================
    // 4. Günlük İstatistik Toplama (Her gece 03:00'te)
    // ============================
    @Scheduled(cron = "0 0 3 * * *")
    public void collectDailyStats() {
        log.info("Günlük istatistik toplama başlatıldı: {}", LocalDateTime.now());

        try {
            // İstatistik toplama işlemleri
            log.info("Günlük istatistikler toplandı.");
        } catch (Exception e) {
            log.error("İstatistik toplama sırasında hata oluştu: {}", e.getMessage());
        }
    }
}