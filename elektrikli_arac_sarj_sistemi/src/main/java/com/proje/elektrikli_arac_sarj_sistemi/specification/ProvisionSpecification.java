package com.proje.elektrikli_arac_sarj_sistemi.specification;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Provision;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ProvisionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ProvisionSpecification {

    public static Specification<Provision> hasStatus(ProvisionStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Provision> closedBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, criteriaBuilder) -> {
            if (start == null && end == null) {
                return criteriaBuilder.conjunction();
            }
            if (start != null && end != null) {
                return criteriaBuilder.between(root.get("closedAt"), start, end);
            }
            if (start != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("closedAt"), start);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("closedAt"), end);
        };
    }
}