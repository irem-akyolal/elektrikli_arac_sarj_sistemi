package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.AuditLog;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AuditAction;
import com.proje.elektrikli_arac_sarj_sistemi.Repository.AuditLogRepository;
import com.proje.elektrikli_arac_sarj_sistemi.dto.audit.AuditLogResponse;
import com.proje.elektrikli_arac_sarj_sistemi.specification.AuditLogSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogAdminService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogAdminService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'OPERATOR', 'VIEWER')")
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> search(AuditAction action, String entityType, String performedBy, Pageable pageable) {
        Specification<AuditLog> spec = Specification
                .where(AuditLogSpecification.hasAction(action))
                .and(AuditLogSpecification.hasEntityType(entityType))
                .and(AuditLogSpecification.hasPerformedBy(performedBy));

        return auditLogRepository.findAll(spec, pageable).map(this::toResponse);
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(), log.getPerformedBy(), log.getAction(),
                log.getEntityType(), log.getEntityId(), log.getCreatedAt()
        );
    }
}
