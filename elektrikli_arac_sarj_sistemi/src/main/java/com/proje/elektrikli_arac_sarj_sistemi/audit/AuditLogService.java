package com.proje.elektrikli_arac_sarj_sistemi.audit;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.AuditLog;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AuditAction;
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


    // AuditLogService'e için giriş işleminde hem barsısız hemde başırılı girişlerde log kaydı oluşturmak için yeni bir metod ekledik. Çünkü Auditable sadece başarılı işlemler için çalışıyor. Login işlemi başarısız olursa exception fırlatıyor ve Auditable çalışmıyor. Bu yüzden login işlemi için manuel log kaydı oluşturmak için bu metod eklendi.
    public void logManual(String performedBy, AuditAction action, String entityType, String details) {
    AuditLog auditLog = new AuditLog();
    auditLog.setPerformedBy(performedBy);
    auditLog.setAction(action);
    auditLog.setEntityType(entityType);
    auditLog.setDetails(details);
    saveAuditLog(auditLog); // zaten var olan, REQUIRES_NEW transaction'lı metod
   }

   // AuditLogService'e ekle
     public String getCurrentUsername() {
    var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated()) {
        return auth.getName();
    }
    return "SYSTEM";
   }
}