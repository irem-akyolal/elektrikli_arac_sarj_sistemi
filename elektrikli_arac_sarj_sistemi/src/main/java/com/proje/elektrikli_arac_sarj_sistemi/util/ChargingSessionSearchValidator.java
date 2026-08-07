package com.proje.elektrikli_arac_sarj_sistemi.util;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.proje.elektrikli_arac_sarj_sistemi.exception.InvalidRequestParameterException;


@Component
public class ChargingSessionSearchValidator {

    public void validate(
            LocalDateTime startedAfter,
            LocalDateTime startedBefore) {

        if (startedAfter != null &&
            startedBefore != null &&
            startedAfter.isAfter(startedBefore)) {

            throw new InvalidRequestParameterException(
                    "startedAfter",
                    "must be earlier than or equal to startedBefore."
            );
        }
    }
}
    

