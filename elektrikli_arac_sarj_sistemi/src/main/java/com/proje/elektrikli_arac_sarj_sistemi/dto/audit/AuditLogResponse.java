package com.proje.elektrikli_arac_sarj_sistemi.dto.audit;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AuditAction;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLogResponse {
    private UUID id;
    private String performedBy;
    private AuditAction action;
    private String entityType;
    private String entityId;
    private LocalDateTime createdAt;
    

    public AuditLogResponse(UUID id, String performedBy, AuditAction action,
                             String entityType, String entityId, LocalDateTime createdAt) {
        this.id = id;
        this.performedBy = performedBy;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getPerformedBy() { return performedBy; }
    public AuditAction getAction() { return action; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}