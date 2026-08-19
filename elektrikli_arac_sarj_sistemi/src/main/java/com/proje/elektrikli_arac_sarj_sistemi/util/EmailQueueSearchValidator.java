package com.proje.elektrikli_arac_sarj_sistemi.util;

import com.proje.elektrikli_arac_sarj_sistemi.exception.InvalidRequestParameterException;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EmailQueueSearchValidator {

    public void validate(
            LocalDateTime createdAfter,
            LocalDateTime createdBefore) {

        if (createdAfter != null &&
            createdBefore != null &&
            createdAfter.isAfter(createdBefore)) {

            throw new InvalidRequestParameterException(
                    "createdAfter",
                    "must be earlier than or equal to createdBefore."
            );
        }
    }
}