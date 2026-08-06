package com.proje.elektrikli_arac_sarj_sistemi.util;

import com.proje.elektrikli_arac_sarj_sistemi.exception.InvalidPageRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PageableValidator {
    private static final int MAX_PAGE_SIZE = 50;

    public void validate(Pageable pageable, Set<String> allowedSortFields) {

    System.out.println("===== PAGEABLE VALIDATOR =====");
    System.out.println("Page : " + pageable.getPageNumber());
    System.out.println("Size : " + pageable.getPageSize());

    for (Sort.Order order : pageable.getSort()) {
        System.out.println("Sort : " + order.getProperty());
    }

    if (pageable.getPageNumber() < 0) {
        throw new InvalidPageRequestException("Page cannot be negative.");
    }

    if (pageable.getPageSize() < 1 || pageable.getPageSize() > MAX_PAGE_SIZE) {
        throw new InvalidPageRequestException(
                "Page size must be between 1 and " + MAX_PAGE_SIZE
        );
    }

    for (Sort.Order order : pageable.getSort()) {

        if (!allowedSortFields.contains(order.getProperty())) {
            throw new InvalidPageRequestException(
                    "Sorting by '" + order.getProperty() + "' is not allowed."
            );
        }
    }
}
}