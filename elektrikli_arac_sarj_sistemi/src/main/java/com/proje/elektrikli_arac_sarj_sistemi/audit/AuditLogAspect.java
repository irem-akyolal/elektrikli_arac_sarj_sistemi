package com.proje.elektrikli_arac_sarj_sistemi.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class AuditLogAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    private final AuditLogService auditLogService; // artık Repository değil, bu yeni Service

    public AuditLogAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Around("@annotation(auditable)")
    public Object logAudit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object result = joinPoint.proceed(); // asıl metod başarıyla tamamlanmadan buraya geçilmez

        try {
            String performedBy = getCurrentUsername();
            String entityId = extractEntityId(result);

            com.proje.elektrikli_arac_sarj_sistemi.Entity.AuditLog auditLog =
                    new com.proje.elektrikli_arac_sarj_sistemi.Entity.AuditLog();
            auditLog.setPerformedBy(performedBy);
            auditLog.setAction(auditable.action());
            auditLog.setEntityType(auditable.entityType());
            auditLog.setEntityId(entityId);

            auditLogService.saveAuditLog(auditLog); // artık BAĞIMSIZ transaction'da kaydediliyor
        } catch (Exception ex) {
            log.error("Audit log kaydı oluşturulamadı: {}", ex.getMessage(), ex);
        }

        return result;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "SYSTEM";
    }

    private String extractEntityId(Object result) {
        try {
            Method getIdMethod = result.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(result);
            return id != null ? id.toString() : "N/A";
        } catch (Exception ex) {
            return "N/A";
        }
    }
}