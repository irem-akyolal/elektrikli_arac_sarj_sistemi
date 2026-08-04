package com.proje.elektrikli_arac_sarj_sistemi.specification;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorStandard;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.PowerType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ConnectorSpecification {

    public static Specification<Connector> hasStandard(ConnectorStandard standard) {
        return (root, query, criteriaBuilder) -> {
            if (standard == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("standard"), standard);
        };
    }

    public static Specification<Connector> hasPowerType(PowerType powerType) {
        return (root, query, criteriaBuilder) -> {
            if (powerType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("powerType"), powerType);
        };
    }

    public static Specification<Connector> belongsToEvse(UUID evseId) {
        return (root, query, criteriaBuilder) -> {
            if (evseId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("evse").get("id"), evseId);
        };
    }
}
