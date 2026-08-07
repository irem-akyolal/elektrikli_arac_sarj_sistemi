package com.proje.elektrikli_arac_sarj_sistemi.util;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.proje.elektrikli_arac_sarj_sistemi.exception.InvalidRequestParameterException;

@Component
public class ProvisionSearchValidator {

    public void validate(
            LocalDateTime closedAfter,
            LocalDateTime closedBefore) {

        if (closedAfter != null &&
            closedBefore != null &&
            closedAfter.isAfter(closedBefore)) {

            throw new InvalidRequestParameterException(
                    "closedAfter",
                    "must be earlier than or equal to closedBefore."
            );
        }
    }
}