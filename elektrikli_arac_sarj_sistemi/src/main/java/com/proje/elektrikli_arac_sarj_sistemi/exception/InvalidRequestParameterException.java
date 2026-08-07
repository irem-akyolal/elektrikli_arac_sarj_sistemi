package com.proje.elektrikli_arac_sarj_sistemi.exception;

public class InvalidRequestParameterException extends RuntimeException {

    public InvalidRequestParameterException(String parameter, String message) {
        super("Invalid request parameter '" + parameter + "': " + message);
    }
}