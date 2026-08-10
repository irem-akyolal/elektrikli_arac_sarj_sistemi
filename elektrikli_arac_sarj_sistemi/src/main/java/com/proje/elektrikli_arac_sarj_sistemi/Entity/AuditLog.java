package com.proje.elektrikli_arac_sarj_sistemi.Entity;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AuditAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "audit_logs",
    indexes = {
        @Index(name = "idx_audit_entity", columnList = "entityType, entityId"),
        @Index(name = "idx_audit_performed_by", columnList = "performedBy")
    }
)
@Getter
@Setter
public class AuditLog extends BaseEntity {

    @Column(nullable = false)
    private String performedBy; // username, ya da "SYSTEM" (otomatik akışlar için)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Column(nullable = false)
    private String entityType; // "Location", "AdminUser" gibi

    private String entityId; // etkilenen kaydın UUID'si (String olarak, esneklik için)

    @Column(length = 1000)
    private String details; // opsiyonel, ek açıklama

    private String ipAddress;
}