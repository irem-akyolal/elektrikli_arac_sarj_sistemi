package com.proje.elektrikli_arac_sarj_sistemi.specification;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.Connector;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Evse;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.Location;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorStandard;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EvseStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class LocationSpecification {

    public static Specification<Location> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(name)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Location> hasCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(city)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("city")),
                    city.toLowerCase()
            );
        };
    }

    public static Specification<Location> isActive(Boolean active) {
        return (root, query, criteriaBuilder) -> {
            if (active == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("active"), active);
        };
    }

  
    public static Specification<Location> hasConnectorType(String connectorType) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(connectorType)) {
                return criteriaBuilder.conjunction();
            }

            ConnectorStandard standard;
            try {
                standard = ConnectorStandard.valueOf(connectorType.toUpperCase());
            } catch (IllegalArgumentException ex) {
                // geçersiz bir enum değeri gönderildiyse, hiçbir sonuç dönmesin
                return criteriaBuilder.disjunction();
            }

            Join<Location, Evse> evseJoin = root.join("evses", JoinType.INNER);
            Join<Evse, Connector> connectorJoin = evseJoin.join("connectors", JoinType.INNER);

            return criteriaBuilder.equal(connectorJoin.get("standard"), standard);
        };
    }

    public static Specification<Location> hasAvailableEvses() {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Evse> subRoot = subquery.from(Evse.class);

            subquery.select(criteriaBuilder.count(subRoot))
                    .where(criteriaBuilder.and(
                            criteriaBuilder.equal(subRoot.get("status"), EvseStatus.AVAILABLE),
                            criteriaBuilder.equal(subRoot.get("location"), root)
                    ));

            return criteriaBuilder.greaterThan(subquery, 0L);
        };
    }
}