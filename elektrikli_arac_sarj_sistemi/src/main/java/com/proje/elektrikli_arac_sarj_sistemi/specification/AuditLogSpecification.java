package com.proje.elektrikli_arac_sarj_sistemi.specification;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.AuditLog;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.AuditAction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class AuditLogSpecification {

    public static Specification<AuditLog> hasAction(AuditAction action) {
        return (root, query, cb) -> {
            if (action == null) return cb.conjunction();
            return cb.equal(root.get("action"), action);
        };
    }

    public static Specification<AuditLog> hasEntityType(String entityType) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(entityType)) return cb.conjunction();
            return cb.equal(cb.lower(root.get("entityType")), entityType.toLowerCase());
        };
    }

    public static Specification<AuditLog> hasPerformedBy(String performedBy) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(performedBy)) return cb.conjunction();
            return cb.like(cb.lower(root.get("performedBy")), "%" + performedBy.toLowerCase() + "%");
        };
    }
}