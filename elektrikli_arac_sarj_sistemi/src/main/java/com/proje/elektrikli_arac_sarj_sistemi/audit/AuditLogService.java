package com.proje.elektrikli_arac_sarj_sistemi.audit;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.AuditLog;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // REQUIRES_NEW: mevcut transaction ne olursa olsun, bu her zaman KENDİ, BAĞIMSIZ bir transaction'da çalışır
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuditLog(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }
}