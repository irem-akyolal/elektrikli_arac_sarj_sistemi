package com.proje.elektrikli_arac_sarj_sistemi.util;

import com.proje.elektrikli_arac_sarj_sistemi.Entity.enums.ConnectorStandard;
import com.proje.elektrikli_arac_sarj_sistemi.exception.InvalidRequestParameterException;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class LocationSearchValidator {

    
    private static final int MAX_NAME_LENGTH = 100;

   
    private static final int MAX_CITY_LENGTH = 50;

    public void validate(
            String name,
            String city,
            String connectorType) {

        validateName(name);
        validateCity(city);
        validateConnectorType(connectorType);
    }

private void validateName(String name) {

    if (!StringUtils.hasText(name)) {
        return;
    }

    String trimmedName = name.trim();

    if (trimmedName.length() > MAX_NAME_LENGTH) {
        throw new InvalidRequestParameterException(
                "name",
                "received " + trimmedName.length()
                + " characters. Maximum allowed is "
                + MAX_NAME_LENGTH + "."
        );
    }
}

private void validateCity(String city) {

    if (!StringUtils.hasText(city)) {
        return;
    }

    String trimmedCity = city.trim();

    if (trimmedCity.length() > MAX_CITY_LENGTH) {
        throw new InvalidRequestParameterException(
                "city",
                "received " + trimmedCity.length()
                + " characters. Maximum allowed is "
                + MAX_CITY_LENGTH + "."
        );
    }
}

private void validateConnectorType(String connectorType) {

    if (!StringUtils.hasText(connectorType)) {
        return;
    }

    try {

        ConnectorStandard.valueOf(
                connectorType.trim().toUpperCase()
        );

    } catch (IllegalArgumentException ex) {

        String allowedValues = Arrays.stream(ConnectorStandard.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        throw new InvalidRequestParameterException(
                "connectorType",
                "received value '" + connectorType
                + "'. Supported values are: "
                + allowedValues + "."
        );
    }
}


}