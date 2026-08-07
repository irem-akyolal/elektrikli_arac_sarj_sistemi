package com.proje.elektrikli_arac_sarj_sistemi.util;

import com.proje.elektrikli_arac_sarj_sistemi.exception.InvalidPageRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PageableValidator {
    private static final int MAX_PAGE_SIZE = 50;
    private static final Logger log = LoggerFactory.getLogger(PageableValidator.class);


    public void validate(Pageable pageable, Set<String> allowedSortFields) {

     log.debug("Pageable validation - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

    for (Sort.Order order : pageable.getSort()) {
        log.debug("Sort : {}", order.getProperty());
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