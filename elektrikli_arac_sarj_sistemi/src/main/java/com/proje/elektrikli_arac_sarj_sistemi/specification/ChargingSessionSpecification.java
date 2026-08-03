package com.proje.elektrikli_arac_sarj_sistemi.specification;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.ChargingSession;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.SessionStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class ChargingSessionSpecification {

    public static Specification<ChargingSession> hasStatus(SessionStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<ChargingSession> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(email)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),
                    "%" + email.toLowerCase() + "%"
            );
        };
    }

    public static Specification<ChargingSession> hasPlateNumber(String plateNumber) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(plateNumber)) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("plateNumber")),
                    "%" + plateNumber.toLowerCase() + "%"
            );
        };
    }

    public static Specification<ChargingSession> startedBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, criteriaBuilder) -> {
            if (start == null && end == null) {
                return criteriaBuilder.conjunction();
            }
            if (start != null && end != null) {
                return criteriaBuilder.between(root.get("startedAt"), start, end);
            }
            if (start != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("startedAt"), start);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("startedAt"), end);
        };
    }
}
