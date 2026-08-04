package com.proje.elektrikli_arac_sarj_sistemi.specification;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class EvseSpecification {

    public static Specification<Evse> hasStatus(EvseStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Evse> belongsToLocation(UUID locationId) {
        return (root, query, criteriaBuilder) -> {
            if (locationId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("location").get("id"), locationId);
        };
    }
}