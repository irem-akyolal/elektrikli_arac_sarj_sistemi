package com.proje.elektrikli_arac_sarj_sistemi.exception;

public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}