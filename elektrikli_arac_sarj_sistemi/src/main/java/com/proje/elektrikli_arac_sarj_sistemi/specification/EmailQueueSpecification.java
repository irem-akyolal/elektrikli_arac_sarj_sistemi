package com.proje.elektrikli_arac_sarj_sistemi.specification;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.EmailQueue;
import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.EmailQueueStatus;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class EmailQueueSpecification {

    public static Specification<EmailQueue> hasStatus(
            EmailQueueStatus status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("status"),
                    status
            );
        };
    }


    public static Specification<EmailQueue> hasRecipient(
            String recipient) {

        return (root, query, criteriaBuilder) -> {

            if (!StringUtils.hasText(recipient)) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(
                            root.get("recipient")
                    ),
                    "%" + recipient.toLowerCase() + "%"
            );
        };
    }


    public static Specification<EmailQueue> hasInvoiceNumber(
            String invoiceNumber) {

        return (root, query, criteriaBuilder) -> {

            if (!StringUtils.hasText(invoiceNumber)) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(
                            root.get("invoiceNumber")
                    ),
                    "%" + invoiceNumber.toLowerCase() + "%"
            );
        };
    }


    public static Specification<EmailQueue> createdBetween(
            LocalDateTime start,
            LocalDateTime end) {

        return (root, query, criteriaBuilder) -> {

            if (start == null && end == null) {
                return criteriaBuilder.conjunction();
            }

            if (start != null && end != null) {

                return criteriaBuilder.between(
                        root.get("createdAt"),
                        start,
                        end
                );
            }

            if (start != null) {

                return criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        start
                );
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"),
                    end
            );
        };
    }
}