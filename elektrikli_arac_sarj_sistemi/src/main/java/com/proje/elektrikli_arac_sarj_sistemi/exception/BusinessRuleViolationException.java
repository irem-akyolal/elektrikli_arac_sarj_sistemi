package com.proje.elektrikli_arac_sarj_sistemi.exception;

public class BusinessRuleViolationException extends ApplicationException {
    public BusinessRuleViolationException(String errorCode, String message) {
        super(errorCode, message);
    }
}